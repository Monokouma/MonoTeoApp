package com.despaircorp.monoteo.data.weather.mapper

import com.despaircorp.monoteo.data.weather.dto.WeatherResponseDto
import com.despaircorp.monoteo.domain.weather.entity.WeatherEntity

fun WeatherResponseDto.toEntity() = WeatherEntity(
    city = openWeatherResponseResult.city,
    country = openWeatherResponseResult.country,
    temperature = openWeatherResponseResult.temperature,
    feelsLike = openWeatherResponseResult.feelsLike,
    humidity = openWeatherResponseResult.humidity,
    description = openWeatherResponseResult.description,
    icon = openWeatherResponseResult.icon,
    windSpeed = openWeatherResponseResult.windSpeed,
    pressure = openWeatherResponseResult.pressure,
    visibility = openWeatherResponseResult.visibility,
    sunrise = openWeatherResponseResult.sunrise,
    sunset = openWeatherResponseResult.sunset,
    condition = openWeatherResponseResult.condition,
    sentence = sentence
)