package com.example.tunify.presentation.feed.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmoothNeonProgressBar(
    currentMs: Long,
    totalMs: Long = 30000L
) {
    // Convert milliseconds to a percentage (0.0f to 1.0f)
    val progress = (currentMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

    // Add a micro-animation state so the bar glides seamlessly between state updates
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 16, easing = LinearEasing),
        label = "neon_progress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
        ) {
            // 1. THE NEON GLOW LAYER (Blurred)
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .fillMaxHeight()
                    .blur(8.dp) // This creates the premium light emission effect
                    .background(Color.White, RoundedCornerShape(50))
            )

            // 2. THE SOLID CORE LAYER
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(50))
            )
        }

        // Timestamps below the bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Convert milliseconds back to a cleanly formatted 0:XX string
            val currentSec = (currentMs / 1000).toInt()
            val secString = if (currentSec < 10) "0:0$currentSec" else "0:$currentSec"

            Text(
                text = secString,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "HOOK PREVIEW (0:30)",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}