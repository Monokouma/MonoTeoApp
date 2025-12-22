package com.despaircorp.monoteo.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class LocationService(private val context: Context) {
    @SuppressLint("MissingPermission")

    actual suspend fun getCurrentLocation(): Pair<Double, Double>? {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        return suspendCoroutine { cont ->
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(Pair(location.latitude, location.longitude))
                } else {
                    cont.resume(null)
                }
            }.addOnFailureListener {
                cont.resume(null)
            }
        }
    }
}