package com.example.tunify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tunify.presentation.feed.FeedScreen
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint is CRITICAL. It tells Hilt this activity is allowed to ask for ViewModels.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeedScreen()
        }
    }
}