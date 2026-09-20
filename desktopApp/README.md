# Desktop entry point

`desktopApp` owns only the JVM window and native distribution packaging. Shared CMP UI and logic are
reused through `:composeApp`; desktop-specific behavior belongs in JVM source sets.

Before enabling an optional feature package, confirm that it supports JVM and that its backend/auth
integration works on desktop. Do not duplicate React implementation code here.
