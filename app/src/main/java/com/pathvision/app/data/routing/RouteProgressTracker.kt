package com.pathvision.app.data.routing

class RouteProgressTracker(private var edges: List<SegmentEdge> = emptyList()) {

    var currentIndex: Int = 0
        private set

    fun reset(newEdges: List<SegmentEdge>) {
        edges = newEdges
        currentIndex = 0
    }

    fun currentEdge(): SegmentEdge? = edges.getOrNull(currentIndex)

    fun advanceEdge(): SegmentEdge? {
        if (currentIndex + 1 < edges.size) {
            currentIndex++
        }
        return currentEdge()
    }

    fun remainingDistanceMeters(): Float {
        if (currentIndex >= edges.size) return 0f
        return edges.drop(currentIndex).sumOf { it.distanceMeters.toDouble() }.toFloat()
    }
}