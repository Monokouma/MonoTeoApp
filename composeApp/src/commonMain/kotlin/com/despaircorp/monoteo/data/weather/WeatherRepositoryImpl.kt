package com.despaircorp.monoteo.data.weather

import com.despaircorp.monoteo.BuildKonfig
import com.despaircorp.monoteo.data.weather.dto.WeatherResponseDto
import com.despaircorp.monoteo.data.weather.mapper.toEntity
import com.despaircorp.monoteo.domain.weather.WeatherRepository
import com.despaircorp.monoteo.domain.weather.entity.WeatherApiRequestEntity
import com.despaircorp.monoteo.domain.weather.entity.WeatherEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl(
    private val httpClient: HttpClient
): WeatherRepository {

    override suspend fun requestWeather(request: WeatherApiRequestEntity): WeatherEntity? = withContext(Dispatchers.IO) {
        try {
            val dto = httpClient.submitFormWithBinaryData(
                url = "https://monoteo-production.up.railway.app/get_weather",
                formData = formData {
                    append("latitude", request.lat.toString())
                    append("longitude", request.long.toString())
                    append("language", request.language)
                }
            ) {
                header("Authorization", request.apiKey)
            }.body<WeatherResponseDto>()

            dto.toEntity()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println(e.stackTraceToString())
            null
        }
    }

}