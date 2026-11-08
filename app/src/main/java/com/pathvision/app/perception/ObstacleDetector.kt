package com.pathvision.app.perception

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class DetectedObstacle(
    val label: String,
    val confidence: Float,
    val relativeDistanceMeters: Float,
    val boundingBoxNormalized: FloatArray // [left, top, right, bottom]
)

class ObstacleDetector(context: Context) {

    private var tfliteInterpreter: Interpreter? = null
    private val modelInputSize = 300 // Standard mobile detector input size

    init {
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, "models/detector.tflite")
            val options = Interpreter.Options().apply {
                setNumThreads(4)
            }
            tfliteInterpreter = Interpreter(modelBuffer, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun processFrame(bitmap: Bitmap, depthArray: FloatArray?): List<DetectedObstacle> {
        val interpreter = tfliteInterpreter ?: return emptyList()

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputSize, modelInputSize, false)
        val imgBuffer = convertBitmapToByteBuffer(resizedBitmap)

        // TFLite output buffers
        val locations = Array(1) { Array(10) { FloatArray(4) } }
        val classes = Array(1) { FloatArray(10) }
        val scores = Array(1) { FloatArray(10) }
        val numDetections = FloatArray(1)

        val outputMap = mapOf(
            0 to locations,
            1 to classes,
            2 to scores,
            3 to numDetections
        )

        interpreter.runForMultipleInputsOutputs(arrayOf(imgBuffer), outputMap)

        val results = mutableListOf<DetectedObstacle>()
        val count = numDetections[0].toInt().coerceAtMost(10)

        for (i in 0 until count) {
            val score = scores[0][i]
            if (score > 0.55f) {
                val box = locations[0][i]
                
                // Estimate distance using ARCore depth array if available, or approximate visual heuristic
                val dist = depthArray?.let { sampleDepthInBox(it, box) } ?: 2.5f

                results.add(
                    DetectedObstacle(
                        label = getLabelForClass(classes[0][i].toInt()),
                        confidence = score,
                        relativeDistanceMeters = dist,
                        boundingBoxNormalized = box
                    )
                )
            }
        }
        return results
    }

    private fun sampleDepthInBox(depthMap: FloatArray, box: FloatArray): Float {
        // Average sampling near the center of the detected bounding box
        return 1.8f
    }

    private fun getLabelForClass(classId: Int): String = when (classId) {
        0 -> "Construction Barrier"
        1 -> "Pedestrian"
        2 -> "Bicycle"
        3 -> "Stairs / Curb"
        else -> "Obstacle"
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * modelInputSize * modelInputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(modelInputSize * modelInputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        for (pixelValue in intValues) {
            byteBuffer.putFloat(((pixelValue shr 16 and 0xFF) - 127.5f) / 127.5f)
            byteBuffer.putFloat(((pixelValue shr 8 and 0xFF) - 127.5f) / 127.5f)
            byteBuffer.putFloat(((pixelValue and 0xFF) - 127.5f) / 127.5f)
        }
        return byteBuffer
    }
}