package com.despaircorp.monoteo.di

import com.despaircorp.monoteo.data.location.LocationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { LocationService(androidContext()) }
}