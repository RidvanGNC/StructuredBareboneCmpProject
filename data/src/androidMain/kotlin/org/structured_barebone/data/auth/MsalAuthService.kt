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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                            _isSignedIn.value = activeAccount != null
                        }

                        override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                            this@MsalAuthService.currentAccount = currentAccount
                            _isSignedIn.value = currentAccount != null
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

    override suspend fun signIn(host: AuthHost): Result<Unit> {
        val application = app ?: return Result.Error("Kimlik dogrulama servisi henuz hazir degil.")
        // Hesap arka planda az once yuklenmis olabilir (bkz. getCurrentAccountAsync); bu durumda
        // MSAL "An account is already signed in" hatasi verir - aslinda zaten girisliyiz, hata degil.
        if (currentAccount != null) {
            _isSignedIn.value = true
            return Result.Success(Unit)
        }
        return suspendCancellableCoroutine { continuation ->
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
}
