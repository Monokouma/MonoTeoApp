package com.despaircorp.monoteo.domain.location

interface LocationRepository {
    suspend fun getUserPosition(): Pair<Double, Double>?
}