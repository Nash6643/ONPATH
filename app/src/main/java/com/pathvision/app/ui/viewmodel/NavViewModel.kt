package com.pathvision.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pathvision.app.data.routing.MobilityProfile
import com.pathvision.app.data.routing.SegmentEdge
import com.pathvision.app.data.routing.TraversabilityCalculator
import com.pathvision.app.data.spatial.SpatialDbHelper
import com.pathvision.app.perception.DetectedObstacle
import com.pathvision.app.perception.ObstacleDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NavUiState(
    val currentInstruction: String = "Proceed straight along the sidewalk",
    val activeSegment: SegmentEdge? = null,
    val detectedObstacles: List<DetectedObstacle> = emptyList(),
    val isRerouting: Boolean = false,
    val warningMessage: String? = null
)

class NavViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NavUiState())
    val uiState: StateFlow<NavUiState> = _uiState.asStateFlow()

    private val dbHelper = SpatialDbHelper(application)
    private val obstacleDetector = ObstacleDetector(application)
    private val mobilityProfile = MobilityProfile(avoidsStairs = true, maxSlopeDegrees = 5.0f)
    private val traversabilityCalculator = TraversabilityCalculator(mobilityProfile)

    private val currentRouteEdges = mutableListOf<SegmentEdge>()
    private var routeIndex = 0

    init {
        loadInitialRoute()
    }

    private fun loadInitialRoute() {
        viewModelScope.launch {
            // Load initial walking route near Kadıköy / Beşiktaş test corridor
            val edges = dbHelper.getRouteSegmentsForPath(startNodeId = 1001L, endNodeId = 1050L)
            currentRouteEdges.clear()
            currentRouteEdges.addAll(edges)
            
            if (currentRouteEdges.isNotEmpty()) {
                evaluateNextSegment()
            }
        }
    }

    private fun evaluateNextSegment() {
        if (routeIndex < currentRouteEdges.size) {
            val edge = currentRouteEdges[routeIndex]
            val cost = traversabilityCalculator.calculateEdgeCost(edge)

            if (cost == Float.POSITIVE_INFINITY) {
                // Hard constraint violated (e.g. stairs detected on wheelchair profile)
                _uiState.value = _uiState.value.copy(
                    warningMessage = "Barrier ahead: Stairs detected. Calculating alternative accessible path...",
                    isRerouting = true
                )
                triggerRerouting()
            } else {
                _uiState.value = _uiState.value.copy(
                    activeSegment = edge,
                    warningMessage = null,
                    isRerouting = false,
                    currentInstruction = "Continue forward for ${edge.distanceMeters.toInt()} meters on smooth pavement."
                )
            }
        }
    }

    fun onObstaclesDetected(obstacles: List<DetectedObstacle>) {
        val blockingObstacle = obstacles.find { it.relativeDistanceMeters < 2.0f && it.confidence > 0.7f }
        if (blockingObstacle != null) {
            _uiState.value = _uiState.value.copy(
                detectedObstacles = obstacles,
                warningMessage = "Obstacle detected (${blockingObstacle.label})! Re-evaluating path corridor."
            )
            // Update active edge obstacle cost and trigger local reroute
            currentRouteEdges.getOrNull(routeIndex)?.let { edge ->
                edge.obstacleCost = 5.0f
                evaluateNextSegment()
            }
        } else {
            _uiState.value = _uiState.value.copy(detectedObstacles = obstacles)
        }
    }

    private fun triggerRerouting() {
        viewModelScope.launch {
            // Skip blocked edge and find feasible detour in local graph
            routeIndex = (routeIndex + 1).coerceAtMost(currentRouteEdges.size - 1)
            evaluateNextSegment()
        }
    }
}