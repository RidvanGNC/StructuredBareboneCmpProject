package org.structured_barebone.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.structured_barebone.cmp_project.App
import org.structured_barebone.di.initKoin

fun main() {
    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Structured Barebone CMP"
        ) {
            App()
        }
    }
}
