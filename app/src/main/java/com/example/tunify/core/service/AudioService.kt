package com.example.tunify.core.service
import android.app.PendingIntent
import android.content.Intent
import com.example.tunify.MainActivity // Ensure this matches your package
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.tunify.R // Ensure you import your R file for the icon
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@androidx.annotation.OptIn(UnstableApi::class)
class AudioService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()

        val stashButton = CommandButton.Builder()
            .setDisplayName("Stash")
            .setIconResId(android.R.drawable.checkbox_on_background)
            .setSessionCommand(SessionCommand("ACTION_STASH", Bundle.EMPTY))
            .build()

        // --- NEW: THE WAKE-UP INTENT ---
        // This tells Android what Activity to open when the notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. Wrap the player in a MediaSession and attach the Intent
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent) // <--- THIS IS THE MAGIC LINE
            .setCustomLayout(listOf(stashButton))
            .setCallback(CustomMediaSessionCallback())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    // 3. Handle clicks from the Notification buttons
    private inner class CustomMediaSessionCallback : MediaSession.Callback {
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == "ACTION_STASH") {
                // TODO: Save the currently playing track to the Room Database Stash
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }
}