package com.despaircorp.monoteo.domain.weather

import com.despaircorp.monoteo.domain.weather.entity.WeatherApiRequestEntity
import com.despaircorp.monoteo.domain.weather.entity.WeatherEntity

interface WeatherRepository {
    suspend fun requestWeather(request: WeatherApiRequestEntity): WeatherEntity?
}