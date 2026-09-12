package com.example.tunify.presentation.feed.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SegmentedScrubber(
    progressSeconds: Int,
    totalSeconds: Int = 30,
    modifier: Modifier = Modifier
) {
    val totalSegments = 10

    // Calculates which of the 10 pills should be currently highlighted
    val activeSegmentIndex = ((progressSeconds.toFloat() / totalSeconds) * totalSegments)
        .toInt()
        .coerceIn(0, totalSegments - 1)

    Column(modifier = modifier.fillMaxWidth()) {

        // --- 1. The 10 Segmented Pills ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalSegments) {
                val isPlayed = i < activeSegmentIndex
                val isCurrent = i == activeSegmentIndex

                // Smoothly animate the height of the pill when it becomes active
                val pillHeight by animateDpAsState(
                    targetValue = if (isCurrent) 14.dp else 10.dp,
                    animationSpec = tween(durationMillis = 300),
                    label = "PillHeight"
                )

                // Smoothly crossfade colors (Dim -> Glowing White -> Played Blue)
                val pillColor by animateColorAsState(
                    targetValue = when {
                        isCurrent -> Color.White
                        isPlayed -> Color(0xFF38BDF8) // Brand light blue
                        else -> Color.White.copy(alpha = 0.15f)
                    },
                    animationSpec = tween(durationMillis = 300),
                    label = "PillColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(pillHeight)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(pillColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- 2. The Time Labels ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                // Formats the integer into a "0:XX" timestamp string
                text = "0:${progressSeconds.toString().padStart(2, '0')}",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "HOOK PREVIEW (0:30)",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.6.sp
            )
        }
    }
}