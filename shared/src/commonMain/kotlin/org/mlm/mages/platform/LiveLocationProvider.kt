package org.mlm.mages.platform

import kotlinx.coroutines.flow.Flow

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null,
    val speed: Float? = null,
    val bearing: Float? = null
)

sealed class LocationResult {
    data class Success(val location: LocationData) : LocationResult()
    data class Error(val message: String) : LocationResult()
    data object NotSupported : LocationResult()
    data object PermissionDenied : LocationResult()
}

expect class LiveLocationProvider() {
    val isSupported: Boolean
    val canSend: Boolean

    suspend fun getCurrentLocation(): LocationResult

    suspend fun startLocationUpdates()

    fun locationUpdates(): Flow<LocationData>

    fun stopLocationUpdates()

    fun hasLocationPermission(): Boolean

    suspend fun requestLocationPermission(): Boolean
}
