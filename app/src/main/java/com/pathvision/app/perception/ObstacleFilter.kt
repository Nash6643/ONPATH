package com.pathvision.app.perception

class ObstacleFilter(private val requiredConsecutiveHits: Int = 3) {
    private val detectionCounts = mutableMapOf<String, Int>()

    fun evaluate(detectedLabels: List<String>): List<String> {
        val confirmed = mutableListOf<String>()
        val currentFrameSet = detectedLabels.toSet()

        for (label in currentFrameSet) {
            val count = (detectionCounts[label] ?: 0) + 1
            detectionCounts[label] = count
            if (count >= requiredConsecutiveHits) {
                confirmed.add(label)
            }
        }
        
        // Decay missing observations
        detectionCounts.keys.retainAll(currentFrameSet)
        return confirmed
    }
}