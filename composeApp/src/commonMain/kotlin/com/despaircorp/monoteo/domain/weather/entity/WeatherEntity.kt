package com.despaircorp.monoteo.domain.weather.entity

data class WeatherEntity(
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
    val condition: String,
    val sentence: String
)