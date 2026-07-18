package org.structured_barebone.data.auth

object AuthConfig {
    // Backend'in expose ettigi API scope'u, orn. api://<backend-client-id>/access_as_user
    // Azure App Registration'daki backend kaydindan alinir (web'deki VITE_AZURE_API_SCOPE ile ayni deger).
    const val API_SCOPE = "api://8762b0e0-3b4c-4d84-ae90-3a6e577c54bb/access_as_user"

    // msal_config.json'daki authorities[0].audience.tenant_id ile ayni deger olmali.
    const val TENANT_ID = "0cd48d9d-13d1-4a63-a2a9-57a2dd7f92c9"

    // IAccount.authority bazi MSAL surumlerinde gecersiz/parse edilemeyen bir deger dondurebiliyor
    // ("Invalid authority URL"); bu yuzden sessiz token istegi icin authority'yi kendimiz kuruyoruz.
    val AUTHORITY_URL = "https://login.microsoftonline.com/$TENANT_ID"
}
