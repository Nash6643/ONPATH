package com.pathvision.app.data.spatial

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class GpsLocationTracker(context: Context, private val onLocationChanged: (Location) -> Unit) : LocationListener {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 1.0f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        onLocationChanged.invoke(location)
    }

    fun stopTracking() {
        locationManager.removeUpdates(this)
    }
}