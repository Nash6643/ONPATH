package com.pathvision.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PathVisionDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),       // Accessible Green AR Path Indicator
    onPrimary = Color.Black,
    error = Color(0xFFE57373),         // Barrier / Hazard Alert Red
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

@Composable
fun PathVisionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PathVisionDarkColorScheme,
        content = content
    )
}