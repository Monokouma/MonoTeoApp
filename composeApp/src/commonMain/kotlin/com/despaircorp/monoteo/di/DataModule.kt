package com.despaircorp.monoteo.di

import com.despaircorp.monoteo.data.location.LocationRepositoryImpl
import com.despaircorp.monoteo.domain.location.LocationRepository
import org.koin.dsl.module

val dataModule = module {
    single<LocationRepository> {
        LocationRepositoryImpl(
            get()
        )
    }
}