package com.despaircorp.monoteo.data.location

// iosMain
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class LocationService {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): Pair<Double, Double>? {
        return suspendCoroutine { cont ->
            val manager = CLLocationManager()
            manager.requestWhenInUseAuthorization()
            val location = manager.location
            if (location != null) {
                cont.resume(
                    Pair(
                        location.coordinate.useContents { latitude },
                        location.coordinate.useContents { longitude }
                    )
                )
            } else {
                cont.resume(null)
            }
        }
    }
}