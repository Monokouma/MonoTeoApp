package com.despaircorp.monoteo.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.despaircorp.monoteo.ui.error.ErrorScreen
import com.despaircorp.monoteo.ui.loading.LoadingScreen
import com.despaircorp.monoteo.ui.weather.WeatherScreen
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import monoteo.composeapp.generated.resources.Res
import monoteo.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Suppress("EffectKeys")
@Composable
fun MainApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()

) {
    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController = remember(permissionsFactory) {
        permissionsFactory.createPermissionsController()
    }

    val uiState by viewModel.mainUiStateFlow.collectAsStateWithLifecycle()

    BindEffect(permissionsController)

    LaunchedEffect(permissionsController) {
        try {
            permissionsController.providePermission(Permission.LOCATION)
        } catch (e: Exception) {
            println(e.stackTraceToString())
        }
    }

    when (uiState) {
        is MainUiState.Loading -> LoadingScreen()
        is MainUiState.Error -> ErrorScreen(
            (uiState as MainUiState.Error).message,
        )
        is MainUiState.Success -> {
            val weather = (uiState as MainUiState.Success).weather
            WeatherScreen(weather)
        }
    }

}

