package com.despaircorp.monoteo

import android.app.Application
import com.despaircorp.monoteo.di.dataModule
import com.despaircorp.monoteo.di.domainModule
import com.despaircorp.monoteo.di.networkModule
import com.despaircorp.monoteo.di.platformModule
import com.despaircorp.monoteo.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(
                platformModule,
                dataModule,
                domainModule,
                uiModule,
                networkModule,
            )
        }
    }
}