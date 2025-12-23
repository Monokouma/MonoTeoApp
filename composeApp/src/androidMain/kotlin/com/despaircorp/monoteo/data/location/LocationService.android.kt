package com.despaircorp.monoteo.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

actual class LocationService(private val context: Context) {
    @SuppressLint("MissingPermission")
    actual fun getCurrentLocation(): Flow<Pair<Double, Double>?> = callbackFlow {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            while (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                delay(500)
            }
        }

        fusedClient.lastLocation.addOnSuccessListener { lastLocation ->
            lastLocation?.let {
                trySend(Pair(it.latitude, it.longitude))
            }
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    trySend(Pair(it.latitude, it.longitude))
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60_000L)
            .setMinUpdateDistanceMeters(5000f)
            .build()

        fusedClient.requestLocationUpdates(
            locationRequest,
            Dispatchers.IO.asExecutor(),
            locationCallback,
        )

        awaitClose { fusedClient.removeLocationUpdates(locationCallback) }
    }.flowOn(Dispatchers.IO)
}