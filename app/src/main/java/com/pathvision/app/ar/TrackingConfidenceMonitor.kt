package com.pathvision.app.ar

import com.google.ar.core.Camera
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState

data class TrackingStatus(
    val isReliable: Boolean,
    val advisoryMessage: String?
)

object TrackingConfidenceMonitor {

    fun evaluate(camera: Camera): TrackingStatus {
        return when (camera.trackingState) {
            TrackingState.TRACKING -> TrackingStatus(isReliable = true, advisoryMessage = null)
            TrackingState.PAUSED -> {
                val msg = when (camera.trackingFailureReason) {
                    TrackingFailureReason.INSUFFICIENT_LIGHT -> "Low light: move slowly or use voice guidance."
                    TrackingFailureReason.EXCESSIVE_MOTION -> "Move camera more steadily to keep ground alignment."
                    TrackingFailureReason.INSUFFICIENT_FEATURES -> "Point camera toward textured pavement or ground."
                    else -> "Adjusting ground tracking..."
                }
                TrackingStatus(isReliable = false, advisoryMessage = msg)
            }
            TrackingState.STOPPED -> TrackingStatus(
                isReliable = false,
                advisoryMessage = "AR tracking paused. Relying on offline route and voice guidance."
            )
        }
    }
}