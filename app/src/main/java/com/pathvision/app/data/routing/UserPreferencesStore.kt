package com.pathvision.app.data.routing

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pathvision_prefs", Context.MODE_PRIVATE)

    var avoidsStairs: Boolean
        get() = prefs.getBoolean("avoids_stairs", true)
        set(value) = prefs.edit().putBoolean("avoids_stairs", value).apply()

    var maxSlope: Float
        get() = prefs.getFloat("max_slope", 5.0f)
        set(value) = prefs.edit().putFloat("max_slope", value).apply()
}