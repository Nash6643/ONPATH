package com.pathvision.app.data.spatial

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.pathvision.app.data.routing.SegmentEdge
import java.io.File
import java.io.FileOutputStream

class SpatialDbHelper(private val context: Context) {

    private val dbName = "istanbul_pedestrian.sqlite"

    init {
        copyDatabaseIfNotExists()
    }

    private fun copyDatabaseIfNotExists() {
        val dbFile = File(context.getDatabasePath(dbName).path)
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            context.assets.open("maps/$dbName").use { inputStream ->
                FileOutputStream(dbFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    private fun getDatabase(): SQLiteDatabase {
        val dbPath = context.getDatabasePath(dbName).path
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
    }

    fun getRouteSegmentsForPath(startNodeId: Long, endNodeId: Long): List<SegmentEdge> {
        val db = getDatabase()
        val edges = mutableListOf<SegmentEdge>()
        
        // Query local pedestrian edges table
        val cursor = db.rawQuery(
            "SELECT id, start_node, end_node, distance_meters, slope_degrees, is_stairs, surface_quality FROM pedestrian_edges WHERE start_node >= ?",
            arrayOf(startNodeId.toString())
        )

        cursor.use {
            while (it.moveToNext()) {
                edges.add(
                    SegmentEdge(
                        id = it.getLong(0),
                        startNodeId = it.getLong(1),
                        endNodeId = it.getLong(2),
                        distanceMeters = it.getFloat(3),
                        slopeDegrees = it.getFloat(4),
                        isStairs = it.getInt(5) == 1,
                        surfaceQuality = it.getFloat(6)
                    )
                )
                if (edges.size >= 25) break // Load next immediate walking segment batch
            }
        }
        db.close()
        return edges
    }
}