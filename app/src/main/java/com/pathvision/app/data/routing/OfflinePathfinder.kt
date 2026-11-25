package com.pathvision.app.data.routing

import java.util.PriorityQueue

data class RoutePlanResult(
    val edges: List<SegmentEdge>,
    val totalCost: Float,
    val totalDistanceMeters: Float
)

class OfflinePathfinder(private val costCalculator: TraversabilityCalculator) {

    fun findShortestPath(
        startNodeId: Long,
        goalNodeId: Long,
        allEdges: List<SegmentEdge>
    ): RoutePlanResult? {
        if (startNodeId == goalNodeId) {
            return RoutePlanResult(emptyList(), 0f, 0f)
        }

        val adjacency = allEdges.groupBy { it.startNodeId }
        val dist = mutableMapOf<Long, Float>().withDefault { Float.POSITIVE_INFINITY }
        val prevEdge = mutableMapOf<Long, SegmentEdge>()
        val pq = PriorityQueue<Pair<Long, Float>>(compareBy { it.second })

        dist[startNodeId] = 0f
        pq.add(startNodeId to 0f)

        while (pq.isNotEmpty()) {
            val (currentNode, currentCost) = pq.poll()
            if (currentCost > dist.getValue(currentNode)) continue
            if (currentNode == goalNodeId) break

            val outgoing = adjacency[currentNode].orEmpty()
            for (edge in outgoing) {
                val edgeCost = costCalculator.calculateEdgeCost(edge)
                if (edgeCost.isInfinite()) continue

                val nextCost = currentCost + edgeCost
                if (nextCost < dist.getValue(edge.endNodeId)) {
                    dist[edge.endNodeId] = nextCost
                    prevEdge[edge.endNodeId] = edge
                    pq.add(edge.endNodeId to nextCost)
                }
            }
        }

        val finalCost = dist.getValue(goalNodeId)
        if (finalCost.isInfinite()) return null

        val pathEdges = mutableListOf<SegmentEdge>()
        var traceNode = goalNodeId
        while (traceNode != startNodeId) {
            val step = prevEdge[traceNode] ?: return null
            pathEdges.add(step)
            traceNode = step.startNodeId
        }
        pathEdges.reverse()

        return RoutePlanResult(
            edges = pathEdges,
            totalCost = finalCost,
            totalDistanceMeters = pathEdges.sumOf { it.distanceMeters.toDouble() }.toFloat()
        )
    }
}