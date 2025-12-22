package com.despaircorp.monoteo

import androidx.compose.ui.window.ComposeUIViewController
import com.despaircorp.monoteo.di.initKoin
import com.despaircorp.monoteo.ui.MainApp

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { MainApp() }