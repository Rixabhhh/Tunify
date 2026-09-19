package com.example.tunify.presentation.feed

import android.content.ComponentName
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.tunify.core.service.AudioService // Ensure this matches your actual service path
import com.example.tunify.domain.model.Track
import com.example.tunify.presentation.feed.components.ActionRail
import com.example.tunify.presentation.feed.components.SegmentedScrubber
import com.example.tunify.presentation.feed.components.VinylStage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun FeedScreen(tracks: List<Track>) {
    if (tracks.isEmpty()) return

    var currentTrackIndex by remember { mutableIntStateOf(0) }
    val currentTrack = tracks[currentTrackIndex]

    var isStashed by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var currentProgressMs by remember { mutableLongStateOf(0L) }

    val context = LocalContext.current
    var mediaController by remember { mutableStateOf<MediaController?>(null) }

    // --- 1. CONNECT TO THE BACKGROUND SERVICE ---
    DisposableEffect(Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, AudioService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            { mediaController = controllerFuture.get() },
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            MediaController.releaseFuture(controllerFuture)
            mediaController?.release()
        }
    }

// --- 2. LOAD TRACK WITH FULL METADATA FOR NOTIFICATIONS ---
    LaunchedEffect(currentTrack.previewUrl, mediaController) {
        mediaController?.let { controller ->
            if (currentTrack.previewUrl.isNotEmpty()) {

                // === NEW: SYNC CHECK ===
                // What is the background service already playing?
                val currentlyPlayingId = controller.currentMediaItem?.mediaId

                // Only override the audio engine if the UI is trying to play a DIFFERENT song
                if (currentlyPlayingId != currentTrack.title) {
                    val metadata = androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(currentTrack.title)
                        .setArtist(currentTrack.artist)
                        .setArtworkUri(android.net.Uri.parse(currentTrack.coverArtUrl))
                        .build()

                    val mediaItem = androidx.media3.common.MediaItem.Builder()
                        .setUri(currentTrack.previewUrl)
                        .setMediaId(currentTrack.title) // We use the title as the unique ID
                        .setMediaMetadata(metadata)
                        .build()

                    controller.setMediaItem(mediaItem)
                    controller.prepare()
                    controller.playWhenReady = true
                }
            }
        }
    }

    // When the user actively swipes away to a new song, stop the old one
    DisposableEffect(currentTrack.previewUrl) {
        onDispose {
            mediaController?.stop()
        }
    }

    // --- 3. THE 60FPS AUDIO TRACKER & CROSSFADE ---
    LaunchedEffect(currentTrack.previewUrl, mediaController) {
        currentProgressMs = 0L

        mediaController?.let { controller ->
            while (isActive) {
                if (controller.isPlaying) {
                    currentProgressMs = controller.currentPosition

                    val fadeDurationMs = 2000f
                    val currentVolume = when {
                        currentProgressMs < fadeDurationMs -> currentProgressMs / fadeDurationMs
                        currentProgressMs > (30_000f - fadeDurationMs) -> (30_000f - currentProgressMs) / fadeDurationMs
                        else -> 1.0f
                    }

                    controller.volume = currentVolume.coerceIn(0f, 1f)

                    if (currentProgressMs >= 30_000L) {
                        if (currentTrackIndex < tracks.size - 1) {
                            currentTrackIndex++
                            isStashed = false
                            isLiked = false
                            currentProgressMs = 0L
                        }
                    }
                }
                delay(16L)
            }
        }
    }

    // --- THE UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060709))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "AUTOPLAY 30s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "CRATE #09", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

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
                        if (currentTrackIndex < tracks.size - 1) {
                            currentTrackIndex++
                            isStashed = false
                            isLiked = false
                            currentProgressMs = 0L
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
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
                        currentMs = currentProgressMs,
                        totalMs = 30000L
                    )
                }

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