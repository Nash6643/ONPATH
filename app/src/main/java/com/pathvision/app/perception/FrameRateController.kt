package com.pathvision.app.perception

import android.os.SystemClock

class FrameRateController(
    private val normalIntervalMs: Long = 250L,
    private val powerSaverIntervalMs: Long = 750L
) {
    private var lastProcessedMs: Long = 0L

    fun shouldProcessFrame(powerSaverActive: Boolean): Boolean {
        val now = SystemClock.elapsedRealtime()
        val interval = if (powerSaverActive) powerSaverIntervalMs else normalIntervalMs
        if (now - lastProcessedMs >= interval) {
            lastProcessedMs = now
            return true
        }
        return false
    }
}