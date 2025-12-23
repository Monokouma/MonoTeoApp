package com.despaircorp.monoteo.domain.error_manager

enum class ErrorManager(
    val message: String,
) {
    LOCATION_ERROR("Location error"),
    LOCATION_PERMISSION_ERROR("Location permission error"),
    API_ERROR("API error"),
}

class ErrorManagerException(val error: ErrorManager) : Exception(error.message)