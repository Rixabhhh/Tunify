package com.example.tunify.presentation
import com.example.tunify.data.mapper.toTrack
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tunify.data.mapper.toTrack
import com.example.tunify.presentation.feed.FeedScreen
import com.example.tunify.presentation.feed.FeedViewModel
import com.example.tunify.presentation.library.LibraryScreen
import com.example.tunify.presentation.library.VaultDetailsScreen
import com.example.tunify.presentation.search.SearchExploreScreen

@Composable
fun TunifyNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // HOIST THE VIEWMODEL: This ensures Feed and Search share the exact same data and state
    val sharedFeedViewModel: FeedViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            if (currentRoute == "feed" || currentRoute == "library" || currentRoute == "search") {
                NavigationBar(
                    containerColor = Color(0xFF060709),
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Feed") },
                        label = { Text("Feed") },
                        selected = currentRoute == "feed",
                        onClick = {
                            navController.navigate("feed") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD8B4FE),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFFD8B4FE),
                            indicatorColor = Color(0xFFD8B4FE).copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        selected = currentRoute == "search",
                        onClick = {
                            navController.navigate("search") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD8B4FE),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFFD8B4FE),
                            indicatorColor = Color(0xFFD8B4FE).copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                        label = { Text("Library") },
                        selected = currentRoute == "library",
                        onClick = {
                            navController.navigate("library") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD8B4FE),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFFD8B4FE),
                            indicatorColor = Color(0xFFD8B4FE).copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "feed",
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFF060709))
        ) {

            // 1. The Swipe Feed
            composable("feed") {
                // Collect states from the shared ViewModel

                val tracks by sharedFeedViewModel.tracks.collectAsState()
                val isLoading by sharedFeedViewModel.isLoading.collectAsState()
                val errorMessage by sharedFeedViewModel.errorMessage.collectAsState()

                FeedScreen(
                    viewModel = sharedFeedViewModel, // <--- ADD THIS EXACT LINE
                    tracks = tracks,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
// 2. The Search Screen
            composable("search") {
                SearchExploreScreen(
                    onGenreClick = { genre ->
                        sharedFeedViewModel.fetchNewCrate(genre)
                        navController.navigate("feed") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    // --- NEW: THIS FIXES THE COMPILE ERROR ---
// --- APPLY STEP 2 HERE IN TUNIFYNAVIGATION.KT ---
                    onTrackClick = { clickedTrack, fullSearchResults ->
                        // FIX: Compare exact IDs so the random obscurity score doesn't break the search
                        val startIndex = fullSearchResults.indexOfFirst { it.id == clickedTrack.id }.coerceAtLeast(0)

                        // Push the entire search list into the player
                        sharedFeedViewModel.playVaultTracks(
                            vaultName = "SEARCH RESULTS",
                            vaultTracks = fullSearchResults,
                            startIndex = startIndex
                        )

                        // Snap back to the feed
                        navController.navigate("feed") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 3. The Root Library
            composable("library") {
                com.example.tunify.presentation.library.LibraryScreen(
                    onNavigateToAffinity = {
                        // THIS TRIGGERS THE AFFINITY VAULT
                        navController.navigate("vault_details/-1")
                    },
                    onNavigateToVault = { vaultId ->
                        navController.navigate("vault_details/$vaultId")
                    }
                )
            }

            // 4. The Custom Vault Details Screen
// 4. The Custom Vault Details Screen
            composable(
                route = "vault_details/{vaultId}",
                arguments = listOf(navArgument("vaultId") { type = NavType.IntType })
            ) {
                // We need to fetch the VaultViewModel to get the tracks
                val vaultViewModel: com.example.tunify.presentation.library.VaultDetailsViewModel = hiltViewModel()
                val vaultDetails by vaultViewModel.vaultDetails.collectAsState()

                VaultDetailsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTrackClick = { clickedTrackEntity ->
                        vaultDetails?.let { details ->

                            // 1. Map all local DB entities to playable domain tracks
                            val domainTracks = details.tracks.map { it.toTrack() }

                            // 2. Find the exact tapped song by comparing IDs directly
                            val startIndex = domainTracks.indexOfFirst { it.id == clickedTrackEntity.id }.coerceAtLeast(0)

                            // 3. Push the playlist into your FeedViewModel
                            sharedFeedViewModel.playVaultTracks(
                                vaultName = details.vault.name,
                                vaultTracks = domainTracks,
                                startIndex = startIndex
                            )

                            // 4. Snap back to the Feed screen to watch it play
                            navController.navigate("feed") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}