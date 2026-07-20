import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        // Kotlin tarafi (MsalAuthService.ios.kt) gercek MSAL cagrilarini bu bridge uzerinden yapar.
        MsalBridgeProvider.shared.bridge = MsalBridgeImpl()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}