package com.pathvision.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pathvision.app.ui.viewmodel.NavUiState
import com.pathvision.app.ui.viewmodel.NavViewModel

@Composable
fun CameraNavScreen(viewModel: NavViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // TODO: Insert ARCore / CameraX surface preview container here

        // Top Navigation Status Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            uiState.warningMessage?.let { warning ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = warning,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xCC111111)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.currentInstruction,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.isRerouting) "Status: Rerouting offline..." else "Status: Ground-Aligned AR Active",
                        color = if (uiState.isRerouting) Color.Yellow else Color.Green,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Bottom Segment Details Overlay
        uiState.activeSegment?.let { segment ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color(0xDD1E1E1E), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Segment ID: ${segment.id}", color = Color.LightGray)
                    Text(text = "Distance: ${segment.distanceMeters.toInt()}m", color = Color.White)
                    Text(text = "Slope: ${segment.slopeDegrees}°", color = Color.LightGray)
                }
            }
        }
    }
}