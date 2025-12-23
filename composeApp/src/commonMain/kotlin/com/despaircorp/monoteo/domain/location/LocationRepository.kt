package com.despaircorp.monoteo.domain.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getUserPosition(): Flow<Pair<Double, Double>?>
}