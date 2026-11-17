package com.pathvision.app.data

import android.util.Log

object AppLogger {
    private const val TAG = "PATHVISION_AI"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}