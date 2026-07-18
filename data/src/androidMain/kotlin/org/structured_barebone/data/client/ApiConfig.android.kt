package org.structured_barebone.data.client

// Backend'in "http" launch profili (launchSettings.json) port 5100'de HTTP olarak calisir;
// gelistirme ortaminda self-signed HTTPS sertifikasi sorunu yasamamak icin HTTP + cleartext tercih edildi.
// Emulator "10.0.2.2" ile host'a erisir; gercek cihaz ise ayni Wi-Fi agindaki PC'nin gercek IP'sini bilmeli.
// Gercek cihazda test ederken bu IP'yi (ipconfig ile) guncel tutmak gerekir - DHCP degisirse bu satir da guncellenmeli.
actual val platformBaseUrl: String = "http://192.168.0.12:5100"
