package org.structured_barebone.data.auth

object AuthConfig {
    // Backend'in expose ettigi API scope'u, orn. api://<backend-client-id>/access_as_user
    // Azure App Registration'daki backend kaydindan alinir (web'deki VITE_AZURE_API_SCOPE ile ayni deger).
    const val API_SCOPE = "api://8762b0e0-3b4c-4d84-ae90-3a6e577c54bb/access_as_user"

    // Backend On-Behalf-Of ile Graph token'i turetemiyor (kisisel Microsoft hesaplari OBO'yu
    // desteklemiyor); bu yuzden Graph icin ayrica bir token biz aliyoruz ve backend'e
    // X-Graph-Token header'i ile iletiyoruz (bkz. HttpClientConfig.kt).
    // "offline_access" reserved bir scope oldugu icin MSAL Android'de scope listesine eklenmiyor.
    val GRAPH_SCOPES = arrayOf("Mail.Read", "Mail.Send", "Mail.ReadWrite")

    // "common": kisisel Microsoft hesaplarinin kendi kutusuna erisebilmesi icin gerekli (web'deki
    // AZURE_TENANT_ID ile ayni mantik). Ozel bir kiraci hedeflenirse kisisel hesaplar ancak "guest"
    // olarak katilir ve gercek posta kutusuna erisemez.
    val AUTHORITY_URL = "https://login.microsoftonline.com/common"
}
