package org.structured_barebone.cmp_project

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import org.structured_barebone.di.commonModule

class IosPlatform {
    fun initKoin() {
        startKoin {
            modules(
                commonModule()
            )
        }
    }
}

fun MainViewController() = ComposeUIViewController {
    IosPlatform().initKoin()
    App()
}