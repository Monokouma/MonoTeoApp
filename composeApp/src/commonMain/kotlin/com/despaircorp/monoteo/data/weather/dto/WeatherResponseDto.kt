package com.despaircorp.monoteo.data.weather.dto

import kotlinx.serialization.Serializable


@Serializable
data class WeatherResponseDto(
    val openWeatherResponseResult: OpenWeatherResultDto,
    val sentence: String
)

@Serializable
data class OpenWeatherResultDto(
    val city: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val pressure: Int,
    val visibility: Int,
    val sunrise: Long,
    val sunset: Long,
    val condition: String
)