package com.pathvision.app.perception

import kotlin.math.roundToInt

object DepthMapSampler {

    fun estimateBoxDistanceMeters(
        depthMeters: FloatArray,
        width: Int,
        height: Int,
        normalizedBox: FloatArray,
        fallbackMeters: Float = 2.5f
    ): Float {
        if (depthMeters.isEmpty() || width <= 0 || height <= 0 || normalizedBox.size < 4) {
            return fallbackMeters
        }

        val left = ((normalizedBox[1] + 0.25f * (normalizedBox[3] - normalizedBox[1])) * width)
            .roundToInt().coerceIn(0, width - 1)
        val right = ((normalizedBox[3] - 0.25f * (normalizedBox[3] - normalizedBox[1])) * width)
            .roundToInt().coerceIn(left, width - 1)
        val top = ((normalizedBox[0] + 0.25f * (normalizedBox[2] - normalizedBox[0])) * height)
            .roundToInt().coerceIn(0, height - 1)
        val bottom = ((normalizedBox[2] - 0.25f * (normalizedBox[2] - normalizedBox[0])) * height)
            .roundToInt().coerceIn(top, height - 1)

        val samples = mutableListOf<Float>()
        for (y in top..bottom) {
            val rowOffset = y * width
            for (x in left..right) {
                val d = depthMeters[rowOffset + x]
                if (d > 0.15f && d < 25.0f) {
                    samples.add(d)
                }
            }
        }

        if (samples.isEmpty()) return fallbackMeters
        samples.sort()
        return samples[samples.size / 2]
    }
}