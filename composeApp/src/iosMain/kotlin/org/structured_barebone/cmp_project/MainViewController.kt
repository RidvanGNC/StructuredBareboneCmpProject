package org.structured_barebone.cmp_project

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import org.structured_barebone.di.commonModule
import org.structured_barebone.di.platformModule

class IosPlatform {
    fun initKoin() {
        startKoin {
            modules(
                commonModule(),
                platformModule(),
            )
        }
    }
}

fun MainViewController() = ComposeUIViewController {
    IosPlatform().initKoin()
    App()
}