package com.pathvision.app

import com.pathvision.app.data.routing.MobilityProfile
import com.pathvision.app.data.routing.SegmentEdge
import com.pathvision.app.data.routing.TraversabilityCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class TraversabilityTest {

    private val wheelchairProfile = MobilityProfile(
        maxSlopeDegrees = 5.0f,
        avoidsStairs = true,
        weightDistance = 1.0f,
        weightSlope = 3.0f,
        weightSurface = 2.0f,
        weightObstacle = 5.0f
    )

    private val calculator = TraversabilityCalculator(wheelchairProfile)

    @Test
    fun testHardConstraintStairsExclusion() {
        val stairEdge = SegmentEdge(1L, 101L, 102L, 10.0f, 0.0f, true, 0.9f)
        assertEquals(Float.POSITIVE_INFINITY, calculator.calculateEdgeCost(stairEdge))
    }

    @Test
    fun testSoftCostCalculationValidEdge() {
        val validEdge = SegmentEdge(3L, 103L, 104L, 20.0f, 2.0f, false, 0.8f, 0.0f, 0.1f)
        // Cost = (1.0*20) + (3.0*2) + (2.0*(1-0.8)) + (5.0*0) + (2.0*0.1) = 26.6
        assertEquals(26.6f, calculator.calculateEdgeCost(validEdge), 0.001f)
    }
}