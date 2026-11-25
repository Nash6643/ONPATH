package com.pathvision.app.perception

data class SegmentationSummary(
    val walkableRatio: Float,
    val hazardClassDetected: Boolean
)

object SceneSegmentationHelper {

    // Class IDs: 0 = walkable pavement/sidewalk, 1 = stairs/curb hazard, 2 = non-walkable
    fun summarizeMask(classMask: IntArray): SegmentationSummary {
        if (classMask.isEmpty()) {
            return SegmentationSummary(walkableRatio = 0f, hazardClassDetected = false)
        }

        var walkableCount = 0
        var hazardCount = 0

        for (cls in classMask) {
            when (cls) {
                0 -> walkableCount++
                1 -> hazardCount++
            }
        }

        val total = classMask.size.toFloat()
        return SegmentationSummary(
            walkableRatio = walkableCount / total,
            hazardClassDetected = hazardCount > (total * 0.03f)
        )
    }
}