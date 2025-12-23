package com.despaircorp.monoteo.domain.weather

import com.despaircorp.monoteo.BuildKonfig
import com.despaircorp.monoteo.domain.error_manager.ErrorManager
import com.despaircorp.monoteo.domain.error_manager.ErrorManagerException
import com.despaircorp.monoteo.domain.location.RequestUserPositionUseCase
import com.despaircorp.monoteo.domain.system.GetSystemLanguageUseCase
import com.despaircorp.monoteo.domain.weather.entity.WeatherApiRequestEntity
import com.despaircorp.monoteo.domain.weather.entity.WeatherEntity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

class GetWeatherForCurrentLocationUseCase(
    private val requestUserPositionUseCase: RequestUserPositionUseCase,
    private val getSystemLanguageUseCase: GetSystemLanguageUseCase,
    private val requestWeatherApiUseCase: RequestWeatherApiUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Result<WeatherEntity>> = requestUserPositionUseCase()
        .flatMapLatest { positionResult ->
            flow {
                positionResult.fold(
                    onSuccess = { userPosition ->
                        emit(
                            requestWeatherApiUseCase(
                                WeatherApiRequestEntity(
                                    lat = userPosition.lat,
                                    long = userPosition.lon,
                                    language = getSystemLanguageUseCase(),
                                    apiKey = BuildKonfig.API_KEY
                                )
                            )
                        )
                    },
                    onFailure = { emit(Result.failure(it)) }
                )
            }
        }
}