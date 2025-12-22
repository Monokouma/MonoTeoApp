package com.despaircorp.monoteo.domain.location

import com.despaircorp.monoteo.domain.location.entity.UserPositionEntity

class RequestUserPositionUseCase(
    private val locationRepository: LocationRepository
) {
    fun invoke(): Result<UserPositionEntity> {

        return Result.success(UserPositionEntity(
            0.0,
            0.0
        ))
    }
}