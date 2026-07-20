package org.structured_barebone.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.structured_barebone.shared.model.Result

// Gercek MSAL cagrilari MsalBridgeProvider.bridge uzerinden Swift tarafina delege edilir
// (bkz. MsalBridge.kt). Bu dosya Mac/Xcode olmadan derlenip calisma zamaninda dogrulanamadi;
// Swift tarafinda bridge implementasyonu ve MSAL SPM paketi kurulumu tamamlanmadan (bkz.
// iosApp/iosApp/MsalBridge.swift) gercek bir giris akisi calismaz.
class MsalAuthService : AuthService {
    private val scopes = listOf(AuthConfig.API_SCOPE)
    private val graphScopes = AuthConfig.GRAPH_SCOPES.toList()
    private val signInScopes = listOf(AuthConfig.API_SCOPE) + graphScopes

    private val _isSignedIn = MutableStateFlow(MsalBridgeProvider.bridge?.currentAccountExists() ?: false)
    override val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    override suspend fun getAccessToken(): String? =
        MsalBridgeProvider.bridge?.acquireTokenSilent(scopes)

    override suspend fun getGraphAccessToken(): String? =
        MsalBridgeProvider.bridge?.acquireTokenSilent(graphScopes)

    override suspend fun signIn(host: AuthHost): Result<Unit> {
        val bridge = MsalBridgeProvider.bridge
            ?: return Result.Error("MSAL bridge henuz kurulmadi (iOS tarafinda MsalBridgeProvider.bridge atanmamis).")

        return when (val result = bridge.signIn(host, signInScopes)) {
            is MsalBridgeResult.Success -> {
                _isSignedIn.value = true
                Result.Success(Unit)
            }
            is MsalBridgeResult.Error -> Result.Error(result.message)
            MsalBridgeResult.Cancelled -> Result.Error("Giris iptal edildi.")
        }
    }

    override fun signOut() {
        MsalBridgeProvider.bridge?.signOut()
        _isSignedIn.value = false
    }
}
