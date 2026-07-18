package org.structured_barebone.data.auth

import kotlinx.coroutines.flow.StateFlow
import org.structured_barebone.shared.model.Result

// Interaktif giris platforma gore farkli bir "host" gerektirir (Android: Activity, iOS: UIViewController).
expect class AuthHost

interface AuthService {
    // MSAL hesabi arka planda asenkron yuklendigi icin gozlemlenebilir olmali;
    // aksi halde UI, hesap yuklendikten sonra guncellenmez (bkz. "An account is already signed in" hatasi).
    val isSignedIn: StateFlow<Boolean>
    suspend fun getAccessToken(): String?
    suspend fun signIn(host: AuthHost): Result<Unit>
    fun signOut()
}
