package com.despaircorp.monoteo.data.location

expect class LocationService {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}