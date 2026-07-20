package org.structured_barebone.data.auth

// iOS'ta MSAL SDK'sini Kotlin/Native cinterop ile dogrudan cagirmak (CocoaPods/SPM kurulumu +
// cinterop bagi Xcode/Mac gerektirir) yerine, gercek MSAL cagrilari Swift tarafinda (iosApp hedefi,
// bkz. iosApp/iosApp/MsalBridge.swift) yapilir. Bu arayuz Kotlin<->Swift arasindaki koprudur; Swift
// bunu implemente edip MsalBridgeProvider.bridge'e atar (bkz. iosApp/iosApp/iOSApp.swift).
// ":data" modulu composeApp'in iOS framework'unden export edildigi icin (composeApp/build.gradle.kts)
// bu arayuz ve MsalBridgeProvider, ComposeApp.h uzerinden Swift'e Objective-C interop ile gorunur.
interface MsalBridge {
    suspend fun acquireTokenSilent(scopes: List<String>): String?
    suspend fun signIn(host: AuthHost, scopes: List<String>): MsalBridgeResult
    fun signOut()
    fun currentAccountExists(): Boolean
}

sealed class MsalBridgeResult {
    class Success(val accessToken: String) : MsalBridgeResult()
    class Error(val message: String) : MsalBridgeResult()
    object Cancelled : MsalBridgeResult()
}

object MsalBridgeProvider {
    var bridge: MsalBridge? = null
}
