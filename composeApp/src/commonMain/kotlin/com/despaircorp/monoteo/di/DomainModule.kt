package com.despaircorp.monoteo.di

import com.despaircorp.monoteo.domain.location.RequestUserPositionUseCase
import com.despaircorp.monoteo.domain.system.GetSystemLanguageUseCase
import com.despaircorp.monoteo.domain.weather.GetWeatherForCurrentLocationUseCase
import com.despaircorp.monoteo.domain.weather.RequestWeatherApiUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        RequestUserPositionUseCase(
            locationRepository = get()
        )
    }
    factory {
        GetWeatherForCurrentLocationUseCase(
            requestUserPositionUseCase = get(),
            getSystemLanguageUseCase = get(),
            requestWeatherApiUseCase = get()
        )
    }

    factory {
        GetSystemLanguageUseCase(
            systemRepository = get()
        )
    }

    factory {
        RequestWeatherApiUseCase(
            weatherRepository = get()
        )
    }
}