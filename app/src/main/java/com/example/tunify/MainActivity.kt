package com.example.tunify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tunify.data.local.UserPreferences
import com.example.tunify.presentation.navigation.MainScreen
import com.example.tunify.presentation.navigation.Screen
import com.example.tunify.presentation.onboarding.OnboardingScreen
import com.example.tunify.presentation.onboarding.OnboardingViewModel
import com.example.tunify.ui.theme.TunifyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint // Tells Hilt that this Activity needs dependencies injected
class MainActivity : ComponentActivity() {

    // Inject our DataStore preferences here
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TunifyTheme {
                // Collect the DataStore state. 'null' means it is still reading from the disk.
                val hasCompletedOnboarding by userPreferences.hasCompletedOnboarding.collectAsState(initial = null)

                // Only draw the screen once we know the user's status
                if (hasCompletedOnboarding != null) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF09090B)
                    ) {
                        val rootNavController = rememberNavController()

                        // The Root Navigation Graph
                        NavHost(
                            navController = rootNavController,
                            startDestination = if (hasCompletedOnboarding == true) Screen.Main.route else Screen.Onboarding.route
                        ) {

                            // 1. The Cold Start Route
                            composable(Screen.Onboarding.route) {
                                val viewModel: OnboardingViewModel = hiltViewModel()
                                OnboardingScreen(
                                    viewModel = viewModel,
                                    onNavigateToFeed = {
                                        // Once onboarding is done, go to Main and destroy Onboarding so they can't hit 'Back' to return to it
                                        rootNavController.navigate(Screen.Main.route) {
                                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // 2. The Everyday App Route (Bottom Nav Skeleton)
                            composable(Screen.Main.route) {
                                MainScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}