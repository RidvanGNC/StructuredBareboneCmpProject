import Foundation
import UIKit
import MSAL
import ComposeApp

// Kotlin tarafindaki MsalBridge arayuzunun (bkz. data/src/iosMain/.../auth/MsalBridge.kt) gercek
// MSAL SDK cagrilarini yapan implementasyonu. MsalAuthService.ios.kt bu siniftan haberdar degildir;
// bunun yerine MsalBridgeProvider.bridge global degiskenine atanmis bir MsalBridge bekler (bkz. iOSApp.swift).
//
// ONEMLI - bu dosya Mac/Xcode olmadan derlenip dogrulanamadi:
// 1) Xcode projesine MSAL iOS SDK'sini Swift Package Manager ile eklemek gerekir:
//    https://github.com/AzureAD/microsoft-authentication-library-for-objc (urun: MSAL)
// 2) Kotlin'deki sealed class MsalBridgeResult (Success/Error/Cancelled) Objective-C'ye
//    "MsalBridgeResultSuccess" / "MsalBridgeResultError" / "MsalBridgeResultCancelled" gibi
//    isimlerle yansir; kesin isimler ComposeApp.h derlenmeden dogrulanamaz - Xcode'da
//    "Jump to Definition" ile kontrol edilmeli.
// 3) client_id, Android'deki data/src/androidMain/res/raw/msal_config.json ile AYNI Azure App
//    Registration'a ait olmalidir; redirect URI ise Info.plist'teki CFBundleURLSchemes girisiyle
//    (msauth.<bundle-id>) eslesir.
final class MsalBridgeImpl: NSObject, MsalBridge {
    private let application: MSALPublicClientApplication?

    override init() {
        do {
            let config = MSALPublicClientApplicationConfig(
                clientId: "beb4b4cf-b7f1-49ac-b0dd-34e8281712a3",
                redirectUri: nil,
                authority: try MSALAADAuthority(url: URL(string: "https://login.microsoftonline.com/common")!)
            )
            application = try MSALPublicClientApplication(configuration: config)
        } catch {
            print("MSAL PublicClientApplication olusturulamadi: \(error)")
            application = nil
        }
        super.init()
    }

    func currentAccountExists() -> Bool {
        guard let application else { return false }
        return ((try? application.allAccounts()) ?? []).isEmpty == false
    }

    func acquireTokenSilent(scopes: [String], completionHandler: @escaping (String?, Error?) -> Void) {
        guard let application, let account = (try? application.allAccounts())?.first else {
            completionHandler(nil, nil)
            return
        }
        let parameters = MSALSilentTokenParameters(scopes: scopes, account: account)
        application.acquireTokenSilent(with: parameters) { result, _ in
            completionHandler(result?.accessToken, nil)
        }
    }

    func signIn(host: UIViewController, scopes: [String], completionHandler: @escaping (MsalBridgeResult?, Error?) -> Void) {
        guard let application else {
            completionHandler(MsalBridgeResultError(message: "MSAL yapilandirilamadi."), nil)
            return
        }

        DispatchQueue.main.async {
            let webParameters = MSALWebviewParameters(authPresentationViewController: host)
            let interactiveParameters = MSALInteractiveTokenParameters(scopes: scopes, webviewParameters: webParameters)

            application.acquireToken(with: interactiveParameters) { result, error in
                if let nsError = error as NSError? {
                    if nsError.domain == MSALErrorDomain,
                       nsError.code == MSALError.userCanceled.rawValue {
                        // Kotlin "object" alt siniflari (Cancelled) ObjC'de tekil (singleton) olarak
                        // disari verilir; ".shared" ile erisilir, "()" ile ornek olusturulamaz.
                        completionHandler(MsalBridgeResultCancelled.shared, nil)
                    } else {
                        completionHandler(MsalBridgeResultError(message: nsError.localizedDescription), nil)
                    }
                    return
                }
                guard let accessToken = result?.accessToken else {
                    completionHandler(MsalBridgeResultError(message: "Token alinamadi."), nil)
                    return
                }
                completionHandler(MsalBridgeResultSuccess(accessToken: accessToken), nil)
            }
        }
    }

    func signOut() {
        guard let application, let account = (try? application.allAccounts())?.first else { return }
        try? application.remove(account)
    }
}
