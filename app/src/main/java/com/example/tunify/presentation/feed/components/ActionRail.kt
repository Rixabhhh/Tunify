package com.example.tunify.presentation.feed.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionRail(
    isStashed: Boolean,
    isLiked: Boolean,
    onStashClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSpotifyClick: () -> Unit,
    onShareClick: () -> Unit,
    onVibeCheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        HardwareButton(
            icon = Icons.Rounded.Add,
            label = "STASH",
            isActive = isStashed,
            activeColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
            onClick = onStashClick
        )

        HardwareButton(
            icon = Icons.Filled.Favorite,
            label = "AFFINITY",
            isActive = isLiked,
            activeColors = listOf(Color(0xFFF43F5E), Color(0xFFE11D48)),
            onClick = onLikeClick
        )

        HardwareButton(
            icon = Icons.Filled.AutoAwesome,
            label = "VIBE",
            isActive = true,
            activeColors = listOf(Color(0xFFD8B4FE), Color(0xFFA855F7)),
            onClick = onVibeCheckClick
        )

        HardwareButton(
            icon = Icons.Filled.PlayArrow,
            label = "LISTEN",
            isActive = true,
            isCircular = true,
            activeColors = listOf(Color(0xFF1ED760), Color(0xFF15803D)),
            onClick = onSpotifyClick
        )

        HardwareButton(
            icon = Icons.Filled.Share,
            label = "SHARE",
            isActive = false,
            activeColors = listOf(Color.Transparent, Color.Transparent),
            onClick = onShareClick
        )
    }
}

@Composable
private fun HardwareButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColors: List<Color>,
    onClick: () -> Unit,
    isCircular: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "ButtonScale"
    )

    val topColor by animateColorAsState(
        targetValue = if (isActive) activeColors[0] else Color.White.copy(alpha = 0.18f),
        label = "TopColor"
    )
    val bottomColor by animateColorAsState(
        targetValue = if (isActive) activeColors[1] else Color.White.copy(alpha = 0.05f),
        label = "BottomColor"
    )

    val shape = if (isCircular) CircleShape else RoundedCornerShape(16.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale)
                .clip(shape)
                .background(Brush.linearGradient(listOf(topColor, bottomColor)))
                .border(
                    width = 1.dp,
                    color = if (isActive) activeColors[0].copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = Color.White.copy(alpha = 0.65f)
        )
    }
}