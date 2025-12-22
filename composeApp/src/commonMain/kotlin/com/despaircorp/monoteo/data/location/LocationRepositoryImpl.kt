package com.despaircorp.monoteo.data.location

import com.despaircorp.monoteo.domain.location.LocationRepository

class LocationRepositoryImpl(
    private val locationService: LocationService
) : LocationRepository {

}