package com.pathvision.app.ar

import com.google.ar.core.Anchor
import com.google.ar.core.Session

class ARAnchorManager {
    private val activeAnchors = mutableListOf<Anchor>()

    fun registerAnchor(anchor: Anchor) {
        activeAnchors.add(anchor)
        if (activeAnchors.size > 20) {
            activeAnchors.removeAt(0).detach()
        }
    }

    fun clearAll(session: Session?) {
        activeAnchors.forEach { it.detach() }
        activeAnchors.clear()
    }
}