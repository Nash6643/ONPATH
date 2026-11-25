package com.pathvision.app.data.spatial

data class MapPackageMetadata(
    val regionName: String = "Istanbul",
    val districtScope: String = "Kadikoy-Besiktas-Pilot",
    val schemaVersion: Int = DbConstants.DB_VERSION,
    val estimatedSizeMb: Int = 50
)