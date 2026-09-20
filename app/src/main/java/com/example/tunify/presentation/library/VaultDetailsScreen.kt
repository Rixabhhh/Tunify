package com.example.tunify.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.tunify.data.local.entity.TrackEntity

@Composable
fun VaultDetailsScreen(
    viewModel: VaultDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTrackClick: (TrackEntity) -> Unit
) {
    val vaultDetails by viewModel.vaultDetails.collectAsState()

    // Show nothing while the database query executes
    if (vaultDetails == null) return

    val vault = vaultDetails!!.vault
    val tracks = vaultDetails!!.tracks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060709))
    ) {
        // --- 1. CINEMATIC HERO HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E1432).copy(alpha = 0.8f), Color(0xFF060709))
                    )
                )
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .padding(top = 48.dp, start = 24.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            // Header Content aligned to bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "CUSTOM VAULT",
                    color = Color(0xFFD8B4FE),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = vault.name,
                    color = Color.White,
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${tracks.size} Tracks",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )

                    // Floating Play Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD8B4FE))
                            .clickable {
                                if (tracks.isNotEmpty()) onTrackClick(tracks.first())
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }

        // --- 2. TRACKLIST ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ) {
            itemsIndexed(tracks) { index, track ->
                TrackRowItem(
                    index = index + 1,
                    track = track,
                    onClick = { onTrackClick(track) }
                )
            }
            item { Spacer(modifier = Modifier.height(100.dp)) } // Padding for bottom nav/player
        }
    }
}

@Composable
private fun TrackRowItem(
    index: Int,
    track: TrackEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Index Number
        Text(
            text = index.toString(),
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
            modifier = Modifier.width(24.dp)
        )

        // Album Art
        AsyncImage(
            model = track.coverArtUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
        )

        // Metadata
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Options Menu (3 dots)
        IconButton(onClick = { /* TODO: Show un-stash options */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White.copy(alpha = 0.6f))
        }
    }
}