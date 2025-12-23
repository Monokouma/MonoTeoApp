package com.despaircorp.monoteo.domain.weather

import com.despaircorp.monoteo.domain.error_manager.ErrorManager
import com.despaircorp.monoteo.domain.error_manager.ErrorManagerException
import com.despaircorp.monoteo.domain.weather.entity.WeatherApiRequestEntity
import com.despaircorp.monoteo.domain.weather.entity.WeatherEntity

class RequestWeatherApiUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(request: WeatherApiRequestEntity): Result<WeatherEntity> {
        val result = weatherRepository.requestWeather(request) ?: return Result.failure(
            ErrorManagerException(ErrorManager.API_ERROR)
        )
        return Result.success(result)
    }
}