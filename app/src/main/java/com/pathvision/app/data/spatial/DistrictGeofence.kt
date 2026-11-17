package com.pathvision.app.data.spatial

object DistrictGeofence {
    // Bounding box for Istanbul metropolitan test corridors (Kadıköy/Beşiktaş)
    private const val MIN_LAT = 40.85
    private const val MAX_LAT = 41.20
    private const val MIN_LON = 28.80
    private const val MAX_LON = 29.40

    fun isWithinIstanbulScope(lat: Double, lon: Double): Boolean {
        return lat in MIN_LAT..MAX_LAT && lon in MIN_LON..MAX_LON
    }
}