package com.despaircorp.monoteo.ui.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.despaircorp.monoteo.ui.background.ash.AshBackground
import com.despaircorp.monoteo.ui.background.clear.ClearBackground
import com.despaircorp.monoteo.ui.background.clouds.CloudsBackground
import com.despaircorp.monoteo.ui.background.drizzle.DrizzleBackground
import com.despaircorp.monoteo.ui.background.dust.DustBackground
import com.despaircorp.monoteo.ui.background.fog.FogBackground
import com.despaircorp.monoteo.ui.background.haze.HazeBackground
import com.despaircorp.monoteo.ui.background.mist.MistBackground
import com.despaircorp.monoteo.ui.background.rain.RainBackground
import com.despaircorp.monoteo.ui.background.sand.SandBackground
import com.despaircorp.monoteo.ui.background.smoke.SmokeBackground
import com.despaircorp.monoteo.ui.background.snow.SnowBackground
import com.despaircorp.monoteo.ui.background.squall.SquallBackground
import com.despaircorp.monoteo.ui.background.thunderstorm.ThunderstormBackground
import com.despaircorp.monoteo.ui.background.tornado.TornadoBackground
import com.despaircorp.monoteo.ui.loading.LoadingScreen
import com.despaircorp.monoteo.ui.theme.MonoTeoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WeatherBackground(
    condition: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (condition) {
            "Thunderstorm" -> ThunderstormBackground()
            "Drizzle" -> DrizzleBackground()
            "Rain" -> RainBackground()
            "Snow" -> SnowBackground()
            "Mist" -> MistBackground()
            "Smoke" -> SmokeBackground()
            "Haze" -> HazeBackground()
            "Dust" -> DustBackground()
            "Fog" -> FogBackground()
            "Sand" -> SandBackground()
            "Ash" -> AshBackground()
            "Squall" -> SquallBackground()
            "Tornado" -> TornadoBackground()
            "Clear" -> ClearBackground()
            "Clouds" -> CloudsBackground()

            else -> Unit
        }
    }
}

@Composable
@Preview
private fun WeatherBackgroundLightPreview() {
    MonoTeoTheme(darkTheme = false) {
        WeatherBackground(
            "Clouds"
        )
    }
}

@Composable
@Preview
private fun WeatherBackgroundDarkPreview() {
    MonoTeoTheme(darkTheme = true) {
        WeatherBackground(
            "Fog"
        )
    }
}
