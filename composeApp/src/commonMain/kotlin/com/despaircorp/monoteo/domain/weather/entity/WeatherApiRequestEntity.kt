package com.despaircorp.monoteo.domain.weather.entity

data class WeatherApiRequestEntity(
    val lat: Double,
    val long: Double,
    val language: String,
    val apiKey: String,
)
