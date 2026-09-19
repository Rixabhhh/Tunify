package com.example.tunify

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TunifyTheme {
                val hasCompletedOnboarding by userPreferences.hasCompletedOnboarding.collectAsState(initial = null)

                // --- NOTIFICATION PERMISSION (ANDROID 13+) ---
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { /* Silent handling, Android automatically manages the denial state */ }
                    )

                    // Only launch the prompt IF they have finished onboarding
                    LaunchedEffect(hasCompletedOnboarding) {
                        if (hasCompletedOnboarding == true) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                if (hasCompletedOnboarding != null) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF09090B)
                    ) {
                        val rootNavController = rememberNavController()

                        NavHost(
                            navController = rootNavController,
                            startDestination = if (hasCompletedOnboarding == true) Screen.Main.route else Screen.Onboarding.route
                        ) {
                            composable(Screen.Onboarding.route) {
                                val viewModel: OnboardingViewModel = hiltViewModel()
                                OnboardingScreen(
                                    viewModel = viewModel,
                                    onNavigateToFeed = {
                                        rootNavController.navigate(Screen.Main.route) {
                                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

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