# Desktop entry point

`desktopApp` owns only the JVM window and native distribution packaging. Shared CMP UI and logic are
reused through `:composeApp`; desktop-specific behavior belongs in JVM source sets.

Before enabling an optional feature package, confirm that it supports JVM and that its backend/auth
integration works on desktop. Do not duplicate React implementation code here.

Configuration: `-PapiBaseUrl=https://...` or `APP_API_BASE_URL` passes the API root to the app as the
`app.apiBaseUrl` system property (also for `:desktopApp:run`); without it the app runs on fixture data and
shows no sign-in. The optional `desktopPackageName` brand value names the installer package (see
"Branding" in `AGENTS.md`).
