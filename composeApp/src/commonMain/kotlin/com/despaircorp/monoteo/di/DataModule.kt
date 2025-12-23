package com.despaircorp.monoteo.di

import com.despaircorp.monoteo.data.location.LocationRepositoryImpl
import com.despaircorp.monoteo.data.system.SystemRepositoryImpl
import com.despaircorp.monoteo.data.weather.WeatherRepositoryImpl
import com.despaircorp.monoteo.domain.location.LocationRepository
import com.despaircorp.monoteo.domain.system.SystemRepository
import com.despaircorp.monoteo.domain.weather.WeatherRepository
import org.koin.dsl.module

val dataModule = module {
    single<LocationRepository> {
        LocationRepositoryImpl(
            locationService = get()
        )
    }

    single<WeatherRepository> {
        WeatherRepositoryImpl(
            httpClient = get()
        )
    }

    single<SystemRepository> {
        SystemRepositoryImpl(
            systemService = get()
        )
    }
}