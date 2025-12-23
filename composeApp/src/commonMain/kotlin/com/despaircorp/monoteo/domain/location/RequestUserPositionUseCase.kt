package com.despaircorp.monoteo.domain.location

import com.despaircorp.monoteo.domain.error_manager.ErrorManager
import com.despaircorp.monoteo.domain.error_manager.ErrorManagerException
import com.despaircorp.monoteo.domain.location.entity.UserPositionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class RequestUserPositionUseCase(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(): Flow<Result<UserPositionEntity>> = locationRepository.getUserPosition().transform {
        if (it == null) {
            emit(Result.failure(ErrorManagerException(ErrorManager.LOCATION_ERROR)))
        } else {
            emit(Result.success(UserPositionEntity(
                it.first,
                it.second,
            )))
        }
    }
}