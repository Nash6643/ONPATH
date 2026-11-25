package com.pathvision.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pathvision.app.perception.DetectedObstacle

@Composable
fun ObstacleOverlayCard(
    obstacles: List<DetectedObstacle>,
    modifier: Modifier = Modifier
) {
    if (obstacles.isEmpty()) return

    val nearest = obstacles.minByOrNull { it.relativeDistanceMeters } ?: return

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xDD3E1F1F)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ahead: ${nearest.label}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "~${"%.1f".format(nearest.relativeDistanceMeters)}m",
                color = Color(0xFFFFCDD2),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}