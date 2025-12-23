package com.despaircorp.monoteo.ui.main

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.despaircorp.monoteo.domain.weather.GetWeatherForCurrentLocationUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Stable
class MainViewModel(
    private val getWeatherForCurrentLocationUseCase: GetWeatherForCurrentLocationUseCase
): ViewModel() {


    private val _mainUiStateFlow = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val mainUiStateFlow: StateFlow<MainUiState> = _mainUiStateFlow

    init {
        observeWeather()
    }

    private fun observeWeather() {
        viewModelScope.launch {
            getWeatherForCurrentLocationUseCase().collect { result ->
                result.fold(
                    onSuccess = { weather ->
                        _mainUiStateFlow.value = MainUiState.Success(weather)
                    },
                    onFailure = { error ->
                        _mainUiStateFlow.value = MainUiState.Error(error.message ?: "Unknown error")
                    }
                )
            }
        }
    }
}