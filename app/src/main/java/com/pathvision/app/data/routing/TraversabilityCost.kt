package com.pathvision.app.data.routing

import kotlin.math.pow
import kotlin.math.sqrt

data class MobilityProfile(
    val maxSlopeDegrees: Float = 5.0f,
    val avoidsStairs: Boolean = true,
    val minSidewalkWidthMeters: Float = 1.2f,
    val weightDistance: Float = 1.0f,
    val weightSlope: Float = 3.0f,
    val weightSurface: Float = 2.0f,
    val weightObstacle: Float = 5.0f,
    val weightUncertainty: Float = 2.0f
)

data class SegmentEdge(
    val id: Long,
    val startNodeId: Long,
    val endNodeId: Long,
    val distanceMeters: Float,
    val slopeDegrees: Float,
    val isStairs: Boolean,
    val surfaceQuality: Float, // 0.0 (poor) to 1.0 (smooth)
    var obstacleCost: Float = 0.0f,
    var dataUncertainty: Float = 0.1f
)

class TraversabilityCalculator(private val profile: MobilityProfile) {

    fun calculateEdgeCost(edge: SegmentEdge): Float {
        // Hard constraints: Return infinity if path is physically impassable
        if (profile.avoidsStairs && edge.isStairs) return Float.POSITIVE_INFINITY
        if (edge.slopeDegrees > profile.maxSlopeDegrees) return Float.POSITIVE_INFINITY

        // Soft cost calculation equation: J(P|u)
        val distanceCost = profile.weightDistance * edge.distanceMeters
        val slopeCost = profile.weightSlope * edge.slopeDegrees
        val surfaceCost = profile.weightSurface * (1.0f - edge.surfaceQuality)
        val obstacleCost = profile.weightObstacle * edge.obstacleCost
        val uncertaintyCost = profile.weightUncertainty * edge.dataUncertainty

        return distanceCost + slopeCost + surfaceCost + obstacleCost + uncertaintyCost
    }
}