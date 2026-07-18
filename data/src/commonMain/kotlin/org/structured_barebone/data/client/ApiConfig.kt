package org.structured_barebone.data.client

object ApiConfig {
    // Gercek backend + Azure App Registration degerleri hazir olana kadar true birakilmali.
    // Web tarafindaki VITE_USE_MOCK bayragi ile ayni amaci gorur.
    const val USE_MOCK = false
}

// Android emulatorunde host makinenin localhost'una 10.0.2.2 uzerinden erisilir;
// iOS simulator ise host localhost'una dogrudan erisebilir.
expect val platformBaseUrl: String
