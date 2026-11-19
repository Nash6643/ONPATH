package com.pathvision.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavTopBar(titleText: String) {
    TopAppBar(
        title = { Text(titleText, color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xDD111111))
    )
}