package com.despaircorp.monoteo.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            platformModule,
            dataModule,
            domainModule,
            networkModule,
            uiModule,
        )
    }
}