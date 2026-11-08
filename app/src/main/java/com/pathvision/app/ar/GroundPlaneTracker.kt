package com.pathvision.app.ar

import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState

class GroundPlaneTracker {

    private var activePathAnchor: Anchor? = null

    fun updateGroundAnchor(frame: Frame): Anchor? {
        val camera = frame.camera
        if (camera.trackingState != TrackingState.TRACKING) {
            return null
        }

        // Search for horizontal surfaces facing upwards (pavement/sidewalk)
        val updatedPlanes = frame.getUpdatedTrackables(Plane::class.java)
        for (plane in updatedPlanes) {
            if (plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING && 
                plane.trackingState == TrackingState.TRACKING) {
                
                // If we do not have an active anchor, place one on the nearest floor plane
                if (activePathAnchor == null) {
                    activePathAnchor = plane.createAnchor(plane.centerPose)
                    break
                }
            }
        }
        return activePathAnchor
    }

    fun release() {
        activePathAnchor?.detach()
        activePathAnchor = null
    }
}