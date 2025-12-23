package com.despaircorp.monoteo.ui.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.despaircorp.monoteo.domain.weather.entity.WeatherEntity
import com.despaircorp.monoteo.ui.background.WeatherBackground
import com.despaircorp.monoteo.ui.theme.MonoTeoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WeatherScreen(
    weather: WeatherEntity,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        WeatherBackground(condition = weather.condition)
        //WeatherBackground(condition = "Tornado")
        WeatherContent(
            weather = weather
        )
    }

}

@Composable
private fun getColorFoSpecialCondition(condition: String): Color {
    print(condition)
    return when (condition) {
        "Snow", "Clouds" ->  Color.Black
        "Smoke", "Ash", "Squall", "Tornado", "Clear", "Rain" -> Color.White
        else -> MaterialTheme.colorScheme.onPrimary
    }
}



@Composable
private fun WeatherContent(
    weather: WeatherEntity,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "${weather.city}, ${weather.country}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = getColorFoSpecialCondition(weather.condition)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = weather.icon,
            contentDescription = weather.description,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "${weather.temperature.toInt()}°",
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = getColorFoSpecialCondition(weather.condition)
        )

        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            fontSize = 18.sp,
            color = getColorFoSpecialCondition(weather.condition).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ressenti ${weather.feelsLike.toInt()}°",
            fontSize = 14.sp,
            color = getColorFoSpecialCondition(weather.condition).copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = weather.sentence,
            fontSize = 16.sp,
            color = getColorFoSpecialCondition(weather.condition),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        WeatherDetailsCard(weather)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun WeatherDetailsCard(
    weather: WeatherEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDetail(label = "Humidité", value = "${weather.humidity}%")
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetail(label = "Visibilité", value = "${weather.visibility / 1000} km")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDetail(label = "Vent", value = "${weather.windSpeed} km/h")
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetail(label = "Lever", value = formatTime(weather.sunrise))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDetail(label = "Pression", value = "${weather.pressure} hPa")
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetail(label = "Coucher", value = formatTime(weather.sunset))
            }
        }
    }
}

@Composable
private fun WeatherDetail(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val hours = (timestamp % 86400) / 3600
    val minutes = (timestamp % 3600) / 60
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}

@Composable
@Preview
private fun WeatherScreenLightPreview() {
    MonoTeoTheme(darkTheme = false) {
        WeatherScreen(
            weather = WeatherEntity(
                city = "Paris",
                country = "FR",
                temperature = 18.5,
                feelsLike = 17.0,
                humidity = 65,
                description = "partiellement nuageux",
                icon = "https://openweathermap.org/img/wn/02d@2x.png",
                windSpeed = 12.0,
                pressure = 1015,
                visibility = 10000,
                sunrise = 1766416785,
                sunset = 1766451264,
                condition = "Snow",
                sentence = "Beau temps pour une balade 🌤️"
            )
        )
    }
}

@Composable
@Preview
private fun WeatherScreenDarkPreview() {
    MonoTeoTheme(darkTheme = true) {
        WeatherScreen(
            weather = WeatherEntity(
                city = "Paris",
                country = "FR",
                temperature = 18.5,
                feelsLike = 17.0,
                humidity = 65,
                description = "partiellement nuageux",
                icon = "https://openweathermap.org/img/wn/02d@2x.png",
                windSpeed = 12.0,
                pressure = 1015,
                visibility = 10000,
                sunrise = 1766416785,
                sunset = 1766451264,
                condition = "Snow",
                sentence = "Beau temps pour une balade 🌤\n️Beau ne balade 🌤"
            )
        )
    }
}