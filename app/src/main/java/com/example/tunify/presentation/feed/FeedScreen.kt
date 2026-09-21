package com.example.tunify.presentation.feed

import android.content.ComponentName
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.tunify.core.service.AudioService
import com.example.tunify.domain.model.Track
import com.example.tunify.presentation.feed.components.ActionRail
import com.example.tunify.presentation.feed.components.SegmentedScrubber
import com.example.tunify.presentation.feed.components.StashBottomSheet
import com.example.tunify.presentation.feed.components.VibeBottomSheet
import com.example.tunify.presentation.feed.components.VinylStage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    tracks: List<Track>,
    isLoading: Boolean,
    errorMessage: String?
) {
    if (isLoading && tracks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF060709)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFD8B4FE))
        }
        return
    }

    if (errorMessage != null && tracks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF060709)), contentAlignment = Alignment.Center) {
            Text(errorMessage, color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(32.dp))
        }
        return
    }

    if (tracks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF060709)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFD8B4FE))
        }
        return
    }

    // --- 1. USE VIEWMODEL STATE INSTEAD OF LOCAL STATE ---
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
    val crateTitle by viewModel.currentCrateTitle.collectAsState()
    val currentTrack = tracks[currentTrackIndex]
    val vibeState by viewModel.vibeState.collectAsState()
    var showStashSheet by remember { mutableStateOf(false) }
    val customVaults by viewModel.customVaults.collectAsState()
    val activeVaultIds by viewModel.activeTrackVaultIds.collectAsState()

    LaunchedEffect(currentTrack.id) {
        viewModel.checkVaultsForTrack(currentTrack.id)
    }

    var isLiked by remember { mutableStateOf(false) }
    var currentProgressMs by remember { mutableLongStateOf(0L) }

    val context = LocalContext.current
    var mediaController by remember { mutableStateOf<MediaController?>(null) }

    DisposableEffect(Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, AudioService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({ mediaController = controllerFuture.get() }, ContextCompat.getMainExecutor(context))
        onDispose {
            MediaController.releaseFuture(controllerFuture)
            mediaController?.release()
        }
    }

    LaunchedEffect(currentTrack.id, mediaController) {
        mediaController?.let { controller ->
            if (currentTrack.previewUrl.isNotEmpty()) {
                val metadata = MediaMetadata.Builder()
                    .setTitle(currentTrack.title)
                    .setArtist(currentTrack.artist)
                    .setArtworkUri(Uri.parse(currentTrack.coverArtUrl))
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri(currentTrack.previewUrl)
                    .setMediaId(currentTrack.id) // Bind strictly to the exact ID
                    .setMediaMetadata(metadata)
                    .build()

                // 1. Force the player to stop the ghost track in the background
                controller.stop()

                // 2. Load the exact item you clicked
                controller.setMediaItem(mediaItem)
                controller.prepare()

                // 3. Command immediate playback
                controller.play()
                controller.playWhenReady = true
            }
        }
    }

    // --- 2. WE REMOVED THE DISPOSABLE EFFECT THAT WAS KILLING THE AUDIO HERE ---

    LaunchedEffect(currentTrack.previewUrl, mediaController) {
        currentProgressMs = 0L
        mediaController?.let { controller ->
            while (isActive) {
                if (controller.isPlaying) {
                    currentProgressMs = controller.currentPosition
                    val fadeDurationMs = 2000f
                    controller.volume = when {
                        currentProgressMs < fadeDurationMs -> (currentProgressMs / fadeDurationMs).coerceIn(0f, 1f)
                        currentProgressMs > (30_000f - fadeDurationMs) -> ((30_000f - currentProgressMs) / fadeDurationMs).coerceIn(0f, 1f)
                        else -> 1.0f
                    }
                    if (currentProgressMs >= 30_000L) {
                        viewModel.nextTrack() // Safely update global state
                        isLiked = false
                        currentProgressMs = 0L
                    }
                }
                delay(16L)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF060709)).padding(horizontal = 20.dp, vertical = 24.dp)) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("AUTOPLAY 30s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                // --- 3. DYNAMIC CRATE TITLE ---
                Text(crateTitle, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                VinylStage(
                    coverArtUrl = currentTrack.coverArtUrl,
                    obscurityScore = currentTrack.obscurityScore,
                    isPlaying = true,
                    isStashed = activeVaultIds.isNotEmpty(),
                    onSleeveClick = {
                        viewModel.nextTrack() // Safely update global state
                        isLiked = false
                        currentProgressMs = 0L
                    }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text("GRADE A • ${currentTrack.obscurityScore}/100", color = Color(0xFFD8B4FE), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(currentTrack.title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(currentTrack.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))
                    SegmentedScrubber(currentMs = currentProgressMs, totalMs = 30000L)
                }
                ActionRail(
                    isStashed = activeVaultIds.isNotEmpty(),
                    isLiked = isLiked,
                    onStashClick = { showStashSheet = true },
                    onLikeClick = { viewModel.onLikeToggled(currentTrack, isLiked); isLiked = !isLiked },
                    onSpotifyClick = { },
                    onShareClick = { },
                    onVibeCheckClick = { viewModel.analyzeCurrentTrack(currentTrack) } // Trigger the AI Analysis
                )
            }
        }
    }

    VibeBottomSheet(
        vibeState = vibeState,
        onDismiss = { viewModel.clearVibeState() }
    )

    if (showStashSheet) {
        StashBottomSheet(
            vaults = customVaults, activeVaultIds = activeVaultIds, isCurrentlyLiked = isLiked,
            onDismiss = { showStashSheet = false },
            onToggleAffinity = { viewModel.onLikeToggled(currentTrack, isLiked); isLiked = !isLiked },
            onToggleVault = { vaultId, isSaved -> viewModel.onVaultToggled(currentTrack, vaultId, isSaved) },
            onCreateNewVault = { name -> viewModel.createVault(name) }
        )
    }
}