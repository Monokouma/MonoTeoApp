package com.despaircorp.monoteo.data.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class LocationService {
    private lateinit var locationManager: CLLocationManager
    private var delegate: CLLocationManagerDelegateProtocol? = null

    @OptIn(ExperimentalForeignApi::class)
    actual fun getCurrentLocation(): Flow<Pair<Double, Double>?> = callbackFlow {
        locationManager = CLLocationManager()

        delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation
                location?.let {
                    val lat = it.coordinate.useContents { latitude }
                    val lon = it.coordinate.useContents { longitude }
                    trySend(Pair(lat, lon))
                }
            }

            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                val status = manager.authorizationStatus

                when (status) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> {
                        manager.startUpdatingLocation()
                    }
                }
            }
        }

        locationManager.delegate = delegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = 5000.0

        locationManager.requestWhenInUseAuthorization()

        awaitClose {
            println("DEBUG iOS: Closing flow")
            locationManager.stopUpdatingLocation()
            locationManager.delegate = null
            delegate = null
        }
    }.flowOn(Dispatchers.Main)
}