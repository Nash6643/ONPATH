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

@Composable
fun RouteSummaryCard(
    totalDistanceMeters: Float,
    maxSlopeDegrees: Float,
    avoidsStairs: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xCC181818)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dist: ${totalDistanceMeters.toInt()}m",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Max Slope: ${maxSlopeDegrees}°",
                color = Color.LightGray,
                fontSize = 13.sp
            )
            Text(
                text = if (avoidsStairs) "No Stairs" else "Stairs OK",
                color = Color(0xFF81C784),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}