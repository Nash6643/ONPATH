package com.pathvision.app.ar

import android.os.SystemClock

class VoiceAlertDebouncer(
    private val voiceManager: VoiceGuidanceManager,
    private val minIntervalMs: Long = 3500L
) {
    private var lastSpokenMessage: String? = null
    private var lastSpokenTimeMs: Long = 0L

    fun speakIfNeeded(message: String, force: Boolean = false) {
        val now = SystemClock.elapsedRealtime()
        val sameMessageTooSoon = message == lastSpokenMessage && (now - lastSpokenTimeMs) < minIntervalMs
        val anyMessageTooSoon = !force && (now - lastSpokenTimeMs) < 1500L

        if (sameMessageTooSoon || anyMessageTooSoon) return

        voiceManager.speak(message)
        lastSpokenMessage = message
        lastSpokenTimeMs = now
    }
}