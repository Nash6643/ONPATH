package com.pathvision.app.data.spatial

import android.content.Context

data class AssetReadinessReport(
    val hasMapDb: Boolean,
    val hasDetectorModel: Boolean
) {
    val isReadyForOfflineNav: Boolean
        get() = hasMapDb && hasDetectorModel
}

class OfflineAssetVerifier(private val context: Context) {

    fun verify(): AssetReadinessReport {
        val assetList = context.assets.list("").orEmpty().toSet()
        val hasMapDb = assetExists("maps/istanbul_pedestrian.sqlite", assetList)
        val hasDetectorModel = assetExists("models/detector.tflite", assetList)

        return AssetReadinessReport(
            hasMapDb = hasMapDb,
            hasDetectorModel = hasDetectorModel
        )
    }

    private fun assetExists(path: String, rootEntries: Set<String>): Boolean {
        if (rootEntries.contains(path)) return true
        return try {
            context.assets.open(path).use { stream ->
                stream.available() > 0 || stream.read() != -1
            }
        } catch (_: Exception) {
            false
        }
    }
}