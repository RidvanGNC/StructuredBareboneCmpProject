package org.structured_barebone.data.auth

import android.content.Context
import android.util.Log
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.SilentAuthenticationCallback
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.structured_barebone.data.R
import org.structured_barebone.shared.model.Result
import kotlin.coroutines.resume

// Tek hesap modunda (SINGLE) MSAL Android SDK'sini saran AuthService implementasyonu.
// msal_config.json'daki client_id/tenant_id/redirect_uri Azure App Registration'dan doldurulmadan
// (ve o kayda ayni redirect URI eklenmeden) gercek girisi tamamlayamaz; bkz. res/raw/msal_config.json.
class MsalAuthService(context: Context) : AuthService {
    private val scopes = arrayOf(AuthConfig.API_SCOPE)
    private var app: ISingleAccountPublicClientApplication? = null
    private var currentAccount: IAccount? = null

    private val _isSignedIn = MutableStateFlow(false)
    override val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    // Baslangicta yuklenen hesabin token'inin gecerliligini arka planda dogrulamak icin
    // (bkz. validateLoadedAccount) - servis Koin single oldugundan uygulama omru boyunca yasar.
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            context,
            R.raw.msal_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    app = application
                    // getCurrentAccount() senkron overload'u ana thread'den cagrilamaz (disk IO icerebilir);
                    // bu yuzden async varyanti kullaniliyor.
                    application.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                        override fun onAccountLoaded(activeAccount: IAccount?) {
                            currentAccount = activeAccount
                            validateLoadedAccount(activeAccount)
                        }

                        override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                            this@MsalAuthService.currentAccount = currentAccount
                            validateLoadedAccount(currentAccount)
                        }

                        override fun onError(exception: MsalException) {
                            Log.e("MsalAuthService", "Hesap yuklenemedi: ${exception.errorCode} - ${exception.message}", exception)
                        }
                    })
                }

                override fun onError(exception: MsalException) {
                    Log.e("MsalAuthService", "PublicClientApplication olusturulamadi: ${exception.errorCode} - ${exception.message}", exception)
                    app = null
                }
            },
        )
    }

    // Yerelde/broker'da bulunan bir hesap kaydinin token'inin hala gecerli oldugunu dogrular.
    // Aksi halde giris ekrani hicbir zaman gosterilmeden uygulama isSignedIn=true sanip sessiz
    // token isteklerini sonsuza kadar tekrarlar (bkz. AADSTS500202 vakasi) - bu yuzden "hesap
    // kaydi var" bilgisi tek basina yeterli sayilmiyor, gercekten token alinabiliyor mu kontrol ediliyor.
    private fun validateLoadedAccount(account: IAccount?) {
        if (account == null) {
            _isSignedIn.value = false
            return
        }
        serviceScope.launch {
            val isValid = getAccessToken() != null
            if (isValid) {
                _isSignedIn.value = true
            } else if (currentAccount === account) {
                app?.let { signOutSuspend(it) }
            }
        }
    }

    override suspend fun getAccessToken(): String? {
        val application = app ?: return null
        currentAccount ?: return null
        return suspendCancellableCoroutine { continuation ->
            application.acquireTokenSilentAsync(
                scopes,
                AuthConfig.AUTHORITY_URL,
                object : SilentAuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        continuation.resume(authenticationResult.accessToken)
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("MsalAuthService", "Sessiz token alma basarisiz: ${exception.errorCode} - ${exception.message}", exception)
                        continuation.resume(null)
                    }
                },
            )
        }
    }

    override suspend fun getGraphAccessToken(): String? {
        val application = app ?: return null
        currentAccount ?: return null
        return suspendCancellableCoroutine { continuation ->
            application.acquireTokenSilentAsync(
                AuthConfig.GRAPH_SCOPES,
                AuthConfig.AUTHORITY_URL,
                object : SilentAuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        continuation.resume(authenticationResult.accessToken)
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("MsalAuthService", "Graph icin sessiz token alma basarisiz: ${exception.errorCode} - ${exception.message}", exception)
                        continuation.resume(null)
                    }
                },
            )
        }
    }

    override suspend fun signIn(host: AuthHost): Result<Unit> {
        val application = app ?: return Result.Error("Kimlik dogrulama servisi henuz hazir degil.")

        // Hesap arka planda az once yuklenmis olabilir (bkz. getCurrentAccountAsync), ama bu hesabin
        // token'i artik gecersiz olabilir (orn. Azure App Registration ayarlari hesap yuklendikten
        // sonra degisti - bkz. AADSTS500202 vakasi). Bu yuzden "hesap var" bilgisine korukorune
        // guvenilmiyor; sessiz bir token denemesiyle dogrulaniyor, basarisiz olursa hesap
        // (hem yerelde hem broker'da) temizlenip gercek bir interaktif giris yapiliyor.
        if (currentAccount != null) {
            if (getAccessToken() != null) {
                _isSignedIn.value = true
                return Result.Success(Unit)
            }
            signOutSuspend(application)
        }

        // AAD v2 endpoint'i tek bir istekte FARKLI kaynaklara (resource) ait scope'lari kabul
        // etmiyor (bkz. AADSTS70011: "api://.../access_as_user" ile Microsoft Graph scope'lari
        // "are not compatible with each other"). Bu yuzden once kendi API'miz icin interaktif
        // giris yapilir, basarili olursa Graph icin ayri bir izin istegi (incremental consent)
        // gonderilir; Graph izni her nedense alinamazsa ana giris yine de basarili sayilir.
        val primaryResult = signInInteractive(application, host)
        if (primaryResult is Result.Success) {
            acquireGraphConsent(application, host)
        }
        return primaryResult
    }

    private suspend fun signInInteractive(application: ISingleAccountPublicClientApplication, host: AuthHost): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            application.signIn(
                host,
                null,
                scopes,
                object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        currentAccount = authenticationResult.account
                        _isSignedIn.value = true
                        continuation.resume(Result.Success(Unit))
                    }

                    override fun onError(exception: MsalException) {
                        // Hesap arka planda tam bu sirada yeniden yuklendiyse MSAL "an account is
                        // already signed in" hatasi verebilir - bu durumda gercekten girisliyiz demektir.
                        if (currentAccount != null) {
                            _isSignedIn.value = true
                            continuation.resume(Result.Success(Unit))
                        } else {
                            continuation.resume(Result.Error(exception.message ?: "Giris basarisiz oldu."))
                        }
                    }

                    override fun onCancel() {
                        continuation.resume(Result.Error("Giris iptal edildi."))
                    }
                },
            )
        }

    private suspend fun acquireGraphConsent(application: ISingleAccountPublicClientApplication, host: AuthHost) {
        suspendCancellableCoroutine<Unit> { continuation ->
            application.acquireToken(
                host,
                AuthConfig.GRAPH_SCOPES,
                object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        continuation.resume(Unit)
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("MsalAuthService", "Graph icin izin alinamadi: ${exception.errorCode} - ${exception.message}", exception)
                        continuation.resume(Unit)
                    }

                    override fun onCancel() {
                        continuation.resume(Unit)
                    }
                },
            )
        }
    }

    override fun signOut() {
        app?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                currentAccount = null
                _isSignedIn.value = false
            }

            override fun onError(exception: MsalException) = Unit
        })
    }

    private suspend fun signOutSuspend(application: ISingleAccountPublicClientApplication) {
        suspendCancellableCoroutine<Unit> { continuation ->
            application.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                override fun onSignOut() {
                    currentAccount = null
                    _isSignedIn.value = false
                    continuation.resume(Unit)
                }

                override fun onError(exception: MsalException) {
                    Log.e("MsalAuthService", "Gecersiz hesap temizlenemedi: ${exception.errorCode} - ${exception.message}", exception)
                    currentAccount = null
                    _isSignedIn.value = false
                    continuation.resume(Unit)
                }
            })
        }
    }
}
