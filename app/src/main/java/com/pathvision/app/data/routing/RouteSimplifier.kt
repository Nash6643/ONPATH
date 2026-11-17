package com.pathvision.app.data.routing

object RouteSimplifier {
    fun smoothCoordinates(coordinates: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
        if (coordinates.size <= 2) return coordinates
        // Simple sliding average interpolation for smoother AR rendering
        val smoothed = mutableListOf<Pair<Double, Double>>()
        for (i in coordinates.indices) {
            val prev = coordinates[maxOf(0, i - 1)]
            val curr = coordinates[i]
            val next = coordinates[minOf(coordinates.size - 1, i + 1)]
            smoothed.add(Pair((prev.first + curr.first + next.first) / 3.0, (prev.second + curr.second + next.second) / 3.0))
        }
        return smoothed
    }
}