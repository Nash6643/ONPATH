package com.pathvision.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.pathvision.app.ui.CameraNavScreen
import com.pathvision.app.ui.viewmodel.NavViewModel

class MainActivity : ComponentActivity() {

    private val navViewModel: NavViewModel by viewModels()
    private var arSession: Session? = null
    private var arSessionInitialized = false

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            initializeArSession()
        } else {
            Toast.makeText(this, "Camera permission is required for AR navigation.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraNavScreen(viewModel = navViewModel)
        }

        checkCameraPermissionAndStart()
    }

    private fun checkCameraPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                initializeArSession()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun initializeArSession() {
        try {
            if (arSession == null) {
                when (Session.checkInstallStatus(this)) {
                    Session.InstallStatus.INSTALL_REQUESTED -> {
                        // ARCore not installed, request installation if needed
                    }
                    Session.InstallStatus.INSTALLED -> {}
                }

                arSession = Session(this).apply {
                    val arConfig = Config(this).apply {
                        // Enable Depth API for obstacle reasoning and ground plane occlusion
                        depthMode = Config.DepthMode.AUTOMATIC
                        updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                        focusMode = Config.FocusMode.AUTO
                    }
                    configure(arConfig)
                }
                arSessionInitialized = true
            }
        } catch (e: UnavailableArcoreNotInstalledException) {
            Toast.makeText(this, "Please install ARCore for offline AR guidance", Toast.LENGTH_LONG).show()
        } catch (e: UnavailableDeviceNotCompatibleException) {
            Toast.makeText(this, "This device does not support ARCore", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (arSessionInitialized) {
            try {
                arSession?.resume()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        arSession?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        arSession?.close()
        arSession = null
    }
}