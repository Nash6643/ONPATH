package com.pathvision.app.data

import android.content.Context
import java.io.File
import java.io.FileWriter

class TelemetryCsvExporter(private val context: Context) {

    private val logFile: File
        get() = File(context.getExternalFilesDir(null), "pathvision_pilot_logs.csv")

    fun logEvent(
        timestampMs: Long,
        eventType: String,
        edgeId: Long,
        metricValue: String
    ) {
        try {
            val fileExists = logFile.exists()
            FileWriter(logFile, true).use { writer ->
                if (!fileExists) {
                    writer.append("timestamp_ms,event_type,edge_id,metric_value\n")
                }
                writer.append("$timestampMs,$eventType,$edgeId,$metricValue\n")
            }
        } catch (e: Exception) {
            AppLogger.e("Failed to write CSV telemetry", e)
        }
    }
}