package com.pathvision.app

import com.pathvision.app.data.routing.MobilityProfile
import com.pathvision.app.data.routing.SegmentEdge
import com.pathvision.app.data.routing.TraversabilityCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val stairEdge = SegmentEdge(
            id = 1L,
            startNodeId = 101L,
            endNodeId = 102L,
            distanceMeters = 10.0f,
            slopeDegrees = 0.0f,
            isStairs = true, // Stairs present
            surfaceQuality = 0.9f
        )

        val cost = calculator.calculateEdgeCost(stairEdge)
        // Hard constraint must return positive infinity for wheelchair profile avoiding stairs
        assertEquals(Float.POSITIVE_INFINITY, cost)
    }

    @Test
    fun testHardConstraintSlopeExclusion()  {
        val steepEdge = SegmentEdge(
            id = 2L,
            startNodeId = 102L,
            endNodeId = 103L,
            distanceMeters = 15.0f,
            slopeDegrees = 8.5f, // Exceeds maxSlopeDegrees (5.0)
            isStairs = false,
            surfaceQuality = 0.9f
        )

        val cost = calculator.calculateEdgeCost(steepEdge)
        assertEquals(Float.POSITIVE_INFINITY, cost)
    }

    @Test
    fun testSoftCostCalculationValidEdge() {
        val validEdge = SegmentEdge(
            id = 3L,
            startNodeId = 103L,
            endNodeId = 104L,
            distanceMeters = 20.0f,
            slopeDegrees = 2.0f, // Within limit
            isStairs = false,
            surfaceQuality = 0.8f, // 20% surface imperfection
            obstacleCost = 0.0f,
            dataUncertainty = 0.1f
        )

        val cost = calculator.calculateEdgeCost(validEdge)
        // Cost = (1.0 * 20.0) + (3.0 * 2.0) + (2.0 * (1.0 - 0.8)) + (5.0 * 0.0) + (2.0 * 0.1)
        // Cost = 20.0 + 6.0 + 0.4 + 0.0 + 0.2 = 26.6
        assertEquals(26.6f, cost, 0.001f)
    }
}