package com.despaircorp.monoteo.data.location

import com.despaircorp.monoteo.domain.location.LocationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocationRepositoryImpl(
    private val locationService: LocationService
) : LocationRepository {

    override fun getUserPosition(): Flow<Pair<Double, Double>?> = locationService.getCurrentLocation()
}