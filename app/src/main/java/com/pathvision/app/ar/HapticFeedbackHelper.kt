package com.pathvision.app.ar

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

class HapticFeedbackHelper(context: Context) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun vibrateHazardAlert() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}