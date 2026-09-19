package com.example.tunify.presentation.feed.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SegmentedScrubber(
    currentMs: Long,
    totalMs: Long = 30000L,
    segments: Int = 10
) {
    val msPerSegment = totalMs / segments
    val activeIndex = (currentMs / msPerSegment).toInt().coerceIn(0, segments - 1)
    val activeSegmentProgress = ((currentMs % msPerSegment).toFloat() / msPerSegment.toFloat()).coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp), // Tall container so the massive glow never touches the edges
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until segments) {
                val isPlayed = i < activeIndex
                val isCurrent = i == activeIndex

                // ZOOM EFFECT
                val targetHeight = if (isCurrent) 14.dp else 10.dp
                val animatedHeight by animateDpAsState(targetValue = targetHeight, label = "zoom_effect_$i")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(animatedHeight),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // 1. The Empty Background Track
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                    )

                    // 2. The Liquid Fill & True Neon Glow
                    if (isPlayed || isCurrent) {
                        val fillFraction = if (isPlayed) 1f else activeSegmentProgress

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = fillFraction)
                                .fillMaxHeight()
                                .then(
                                    if (isCurrent) {
                                        // Stacked Canvas Glows (No clipping!)
                                        Modifier
                                            .neonGlow(Color.White, 18.dp) // Massive outer bleeding halo
                                            .neonGlow(Color.White, 6.dp)  // Intense inner core light
                                    } else {
                                        Modifier.neonGlow(Color.White.copy(alpha = 0.6f), 4.dp)
                                    }
                                )
                                .background(Color.White, RoundedCornerShape(50))
                        )
                    }
                }
            }
        }

        // Timestamps below the bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val currentSec = (currentMs / 1000).toInt()
            val secString = if (currentSec < 10) "0:0$currentSec" else "0:$currentSec"

            Text(
                text = secString,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "HOOK PREVIEW (0:30)",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.6.sp
            )
        }
    }
}

// THE PRO-LEVEL NEON GLOW MODIFIER (Bypasses Compose layout clipping)
fun Modifier.neonGlow(glowColor: Color, radius: Dp) = this.drawBehind {
    val frameworkPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.TRANSPARENT
        setShadowLayer(radius.toPx(), 0f, 0f, glowColor.toArgb())
    }
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawRoundRect(
            0f, 0f, size.width, size.height,
            size.height / 2, size.height / 2, // Perfect pill shape radius
            frameworkPaint
        )
    }
}