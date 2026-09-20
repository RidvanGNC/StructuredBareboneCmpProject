package org.structured_barebone.cmp_project

import androidx.compose.ui.window.ComposeUIViewController
import org.structured_barebone.di.initKoin

fun initKoinForIos() = initKoin()

fun MainViewController() = ComposeUIViewController {
    App()
}
