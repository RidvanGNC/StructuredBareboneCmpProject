package org.structured_barebone.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.structured_barebone.cmp_project.App
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.client.AppInfo
import org.structured_barebone.di.initKoin

fun main() {
    AppInfo.version = System.getProperty("app.version").orEmpty()
    initKoin(config = AppConfig(System.getProperty("app.apiBaseUrl")?.ifBlank { null }))

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Structured Barebone CMP"
        ) {
            App()
        }
    }
}
