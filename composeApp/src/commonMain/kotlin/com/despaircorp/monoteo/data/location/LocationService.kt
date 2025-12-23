package com.despaircorp.monoteo.data.location

import kotlinx.coroutines.flow.Flow

expect class LocationService {
    fun getCurrentLocation(): Flow<Pair<Double, Double>?>
}