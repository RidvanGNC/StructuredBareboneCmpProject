import SwiftUI

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.initKoinForIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
