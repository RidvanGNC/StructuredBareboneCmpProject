# Sign-in screen

Email and password sign-in against `POST /v1/auth/login` (see `contracts/common.openapi.yaml`). The app
shell shows it while the session state is signed out and replaces it as soon as the session opens, so
there is no navigation route and no back stack to clean after sign-out.

Without a configured backend (`AppConfig.apiBaseUrl` empty) the shell skips sign-in entirely.

To build an app without sign-in, remove `:screen:login` and `:screen:profile`'s sign-out row, the
`SessionViewModel` gate in `composeApp/App.kt` and the auth endpoints from the contract. Social sign-in,
password reset and two-factor are not part of the template; add them as an optional package.
