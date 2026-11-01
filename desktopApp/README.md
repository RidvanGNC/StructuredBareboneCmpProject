# Desktop entry point

`desktopApp` owns only the JVM window and native distribution packaging. Shared CMP UI and logic are
reused through `:composeApp`; desktop-specific behavior belongs in JVM source sets.

Before enabling an optional feature package, confirm that it supports JVM and that its backend/auth
integration works on desktop. Do not duplicate React implementation code here.

Configuration: `-PapiBaseUrl=https://...` or `APP_API_BASE_URL` passes the API root to the app as the
`app.apiBaseUrl` system property (also for `:desktopApp:run`); without it the app runs on fixture data and
shows no sign-in. The optional `desktopPackageName` brand value names the installer package (see
"Branding" in `AGENTS.md`).

## Release build (MSI)

`gradlew.bat :desktopApp:packageReleaseMsi` builds a per-user installer (no install directory page, Start
menu and desktop shortcuts). It needs WiX 3 on the machine (present on GitHub's Windows runners) and
refuses to run without the brand key `upgradeUuid`: Windows tells releases of one product apart by that
code, so generate one UUID per product and never change it. `vendor` (publisher) and `desktopIcon`
(.ico) are optional brand keys. ProGuard stays off in the release build because it removed the Ktor
service providers. The MSI is not code-signed; Windows SmartScreen warns about unsigned installers.
The whole flow is in `docs/release.md`.
