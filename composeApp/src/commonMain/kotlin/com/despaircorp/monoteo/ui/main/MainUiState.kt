package com.despaircorp.monoteo.ui.main

import com.despaircorp.monoteo.domain.weather.entity.WeatherEntity

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val weather: WeatherEntity) : MainUiState
    data class Error(val message: String) : MainUiState
}