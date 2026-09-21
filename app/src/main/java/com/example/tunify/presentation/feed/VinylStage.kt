package com.example.tunify.presentation.feed.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun VinylStage(
    coverArtUrl: String,
    obscurityScore: Int,
    isPlaying: Boolean,
    isStashed: Boolean,
    onSleeveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "VinylRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing)
        ),
        label = "RecordSpinAngle"
    )

    // Using strictly defined width/height prevents the parent UI from collapsing it
    Box(
        modifier = modifier
            .width(310.dp)
            .height(240.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // --- LAYER 1: THE ROTATING VINYL DISC ---
        VinylRecord(
            rotationAngle = if (isPlaying) rotationAngle else 0f,
            obscurityScore = obscurityScore,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 18.dp)
        )

        // --- LAYER 2: ALBUM SLEEVE ---
        Box(
            modifier = Modifier
                .fillMaxHeight()      // Ties the height to the parent's 240.dp
                .aspectRatio(1f)      // Forces width to match height (perfect square)
                .align(Alignment.CenterStart)
                .shadow(
                    elevation = 28.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(26.dp))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(26.dp)
                )
                .background(Color(0xFF14161B))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSleeveClick
                )
        ) {
            AsyncImage(
                model = coverArtUrl,
                contentDescription = "Album Cover Artwork",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            AnimatedVisibility(
                visible = isStashed,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFF34D399),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "VAULTED",
                        color = Color(0xFF34D399),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun VinylRecord(
    rotationAngle: Float,
    obscurityScore: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(230.dp) // Keeps the record slightly smaller than the 240dp sleeve
            .rotate(rotationAngle)
            .shadow(16.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFF0D0D0E))
            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
            .border(24.dp, Color(0xFF151619), CircleShape)
            .border(48.dp, Color(0xFF101114), CircleShape)
            .border(72.dp, Color(0xFF18191D), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF9333EA), Color(0xFF4F46E5))
                    )
                )
                .border(2.5.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "33⅓ RPM",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "$obscurityScore%",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            )
        }
    }
}