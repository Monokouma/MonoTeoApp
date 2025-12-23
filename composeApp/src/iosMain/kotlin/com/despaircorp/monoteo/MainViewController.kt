package com.despaircorp.monoteo

import androidx.compose.ui.window.ComposeUIViewController
import com.despaircorp.monoteo.di.initKoin
import com.despaircorp.monoteo.ui.main.MainApp
import com.despaircorp.monoteo.ui.theme.MonoTeoTheme

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    MonoTeoTheme {
        MainApp()
    }
}