package org.structured_barebone.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.structured_barebone.shared.model.Result

// iOS icin MSAL entegrasyonu bu asamada yapilmadi: bu sandbox'ta Xcode/CocoaPods bulunmadigindan
// MSAL iOS SDK'sinin cinterop/derleme dogrulamasi yapilamiyor. Android tarafi calisir durumda;
// iOS'ta gercek MSAL SDK entegrasyonu ayri bir adim olarak, gercek bir Mac/Xcode ortaminda yapilmali.
class UnimplementedAuthService : AuthService {
    override val isSignedIn: StateFlow<Boolean> = MutableStateFlow(false)

    override suspend fun getAccessToken(): String? = null

    override suspend fun getGraphAccessToken(): String? = null

    override suspend fun signIn(host: AuthHost): Result<Unit> =
        Result.Error("iOS icin kimlik dogrulama entegrasyonu henuz tamamlanmadi.")

    override fun signOut() = Unit
}
