package org.structured_barebone.cmp_project

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

// NOT: NSUserDefaults "AppleLanguages" override'i iOS'ta genellikle uygulamanin
// yeniden baslatilmasiyla tam etkili olur; bu dosya Mac/Xcode olmadan derlenip
// calisma zamaninda dogrulanamadi.
actual fun systemLanguageTag(): String {
    val preferred = NSLocale.preferredLanguages.firstOrNull() as? String ?: return "en"
    return preferred.substringBefore("-")
}

actual fun applyLanguageTag(tag: String) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(tag), forKey = "AppleLanguages")
    NSUserDefaults.standardUserDefaults.synchronize()
}
