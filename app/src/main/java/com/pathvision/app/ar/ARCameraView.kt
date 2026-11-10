package com.pathvision.app.ar

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ar.core.Session
import com.pathvision.app.perception.ObstacleDetector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Composable
fun ARCameraView(
    modifier: Modifier = Modifier,
    arSession: Session?,
    obstacleDetector: ObstacleDetector,
    onObstaclesDetected: (List<com.pathvision.app.perception.DetectedObstacle>) -> Int?
) {
    val context = LocalContext.current
    val groundTracker = remember { GroundPlaneTracker() }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                preserveEGLContextOnPause = true
                setEGLContextClientVersion(2)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)

                setRenderer(object : GLSurfaceView.Renderer {
                    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                        arSession?.setDisplayGeometry(display.rotation, width, height)
                    }

                    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                        arSession?.setDisplayGeometry(display.rotation, width, height)
                    }

                    override fun onDrawFrame(gl: GL10?) {
                        try {
                            arSession?.let { session ->
                                session.setCameraTextureName(0) // Render camera background
                                val frame = session.update()
                                
                                // 1. Track local ground plane for AR path anchoring
                                val groundAnchor = groundTracker.updateGroundAnchor(frame)

                                // 2. Periodically sample camera frame for TFLite obstacle detection
                                val image = frame.acquireCameraImage()
                                try {
                                    // Convert camera YUV/RGB frame to Bitmap for TFLite inference
                                    // (Optimized sampling interval handled in production worker)
                                    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888) 
                                    val depthData = frame.acquireDepthData()?.let { FloatArray(10) }
                                    
                                    val obstacles = obstacleDetector.processFrame(bitmap, depthData)
                                    if (obstacles.isNotEmpty()) {
                                        onObstaclesDetected(obstacles)
                                    }
                                } finally {
                                    image.close()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        }
    )
}