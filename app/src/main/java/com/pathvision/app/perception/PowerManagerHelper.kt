package com.pathvision.app.perception

import android.content.Context
import android.os.PowerManager

class PowerManagerHelper(context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun isPowerSaveModeActive(): Boolean {
        return powerManager.isPowerSaveMode
    }
}