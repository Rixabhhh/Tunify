package com.example.tunify.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tunify.domain.model.Track
import com.example.tunify.presentation.feed.components.ActionRail
import com.example.tunify.presentation.feed.components.SegmentedScrubber
import com.example.tunify.presentation.feed.components.VinylStage

@Composable
fun FeedScreen(tracks: List<Track>) {
    // Safety check: if MainScreen passes an empty list, don't try to draw the screen
    if (tracks.isEmpty()) return

    // Keep track of which song the user is currently viewing
    var currentTrackIndex by remember { mutableIntStateOf(0) }
    val currentTrack = tracks[currentTrackIndex]

    // Local UI state
    var isStashed by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }

    // --- THE AUDIO ENGINE (ExoPlayer) ---
    val context = androidx.compose.ui.platform.LocalContext.current
    val exoPlayer = remember { androidx.media3.exoplayer.ExoPlayer.Builder(context).build() }

    // Every time 'currentTrack.previewUrl' changes, this block re-runs
    androidx.compose.runtime.DisposableEffect(currentTrack.previewUrl) {
        if (currentTrack.previewUrl.isNotEmpty()) {
            val mediaItem = androidx.media3.common.MediaItem.fromUri(currentTrack.previewUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true // Auto-play when loaded
        }

        // When the URL changes (user swipes to next song), stop the current playback
        onDispose {
            exoPlayer.stop()
        }
    }

    // Safely destroy the entire player if the user closes the app completely
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    // ------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060709)) // Deep ambient black from your design
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- 1. Top Navigation / Status Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "AUTOPLAY 30s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "CRATE #09", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // --- 2. Center Stage (Vinyl Animation) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                VinylStage(
                    coverArtUrl = currentTrack.coverArtUrl,
                    obscurityScore = currentTrack.obscurityScore,
                    isPlaying = true,
                    isStashed = isStashed,
                    onSleeveClick = {
                        // Swipe to next track logic
                        if (currentTrackIndex < tracks.size - 1) {
                            currentTrackIndex++
                            isStashed = false
                            isLiked = false
                        }
                    }
                )
            }

            // --- 3. Bottom Deck (Metadata + Scrubber + Actions) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left Side: Track Info & Scrubber
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = "GRADE A • ${currentTrack.obscurityScore}/100",
                        color = Color(0xFFD8B4FE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentTrack.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack.artist,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SegmentedScrubber(
                        progressSeconds = 0, // Hardcoded for now, we will sync this to the audio later
                        totalSeconds = 30
                    )
                }

                // Right Side: Hardware Buttons
                ActionRail(
                    isStashed = isStashed,
                    isLiked = isLiked,
                    onStashClick = { isStashed = !isStashed },
                    onLikeClick = { isLiked = !isLiked },
                    onSpotifyClick = { /* TODO */ },
                    onShareClick = { /* TODO */ }
                )
            }
        }
    }
}