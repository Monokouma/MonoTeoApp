package com.despaircorp.monoteo.di

import com.despaircorp.monoteo.ui.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainViewModel(get()) }

}