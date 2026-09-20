package com.example.tunify.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

// Critical import for converting DB entities to playable tracks
import com.example.tunify.data.mapper.toTrack

@Composable
fun MainScreen(
    feedViewModel: com.example.tunify.presentation.feed.FeedViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    // Collect global states for the Mini-Player
    val tracks by feedViewModel.tracks.collectAsState()
    val trackIndex by feedViewModel.currentTrackIndex.collectAsState()
    val activeTrack = tracks.getOrNull(trackIndex)

    Scaffold(
        bottomBar = {
            Column {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val currentRoute = currentDestination?.route

                // --- PERSISTENT MINI PLAYER ---
                if (currentRoute != Screen.HomeFeed.route && activeTrack != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF151020))
                            .clickable {
                                navController.navigate(Screen.HomeFeed.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = activeTrack.coverArtUrl,
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeTrack.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                maxLines = 1
                            )
                            Text(text = activeTrack.artist, color = Color(0xFFD8B4FE).copy(alpha = 0.8f), fontSize = 12.sp, maxLines = 1)
                        }
                    }
                }

                // --- NAVIGATION BAR ---
                NavigationBar(
                    containerColor = Color(0xFF09090B),
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.HomeFeed.route } == true,
                        onClick = {
                            navController.navigate(Screen.HomeFeed.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black, selectedTextColor = Color(0xFFD8B4FE),
                            indicatorColor = Color(0xFFD8B4FE), unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.SearchExplore.route } == true,
                        onClick = {
                            navController.navigate(Screen.SearchExplore.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black, selectedTextColor = Color(0xFFD8B4FE),
                            indicatorColor = Color(0xFFD8B4FE), unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                        label = { Text("Library") },
                        selected = currentRoute == "library" || currentRoute?.startsWith("vault_details") == true,
                        onClick = {
                            navController.navigate("library") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black, selectedTextColor = Color(0xFFD8B4FE),
                            indicatorColor = Color(0xFFD8B4FE), unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeFeed.route,
            modifier = Modifier.padding(innerPadding)
        ) {

// --- HOME TAB ---
            composable(Screen.HomeFeed.route) {
                val isLoading by feedViewModel.isLoading.collectAsState()
                val errorMessage by feedViewModel.errorMessage.collectAsState()

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFD8B4FE))
                    }
                } else if (tracks.isNotEmpty()) {
                    com.example.tunify.presentation.feed.FeedScreen(
                        viewModel = feedViewModel, // <-- ADD THIS EXACT LINE
                        tracks = tracks,
                        isLoading = isLoading,
                        errorMessage = errorMessage
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF09090B)), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No tracks found in the crate.\nTry a different frequency.",
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

// --- SEARCH TAB ---
            composable(Screen.SearchExplore.route) {
                val knownGenres = listOf("Pop", "Indie", "Rock", "R&B", "Phonk", "Synthwave", "Lo-Fi")
                com.example.tunify.presentation.search.SearchExploreScreen(
                    onGenreClick = { selection ->
                        if (selection == "Discover Mix") {
                            feedViewModel.fetchNewCrate("top hits")
                        } else if (knownGenres.contains(selection)) {
                            feedViewModel.fetchNewCrate("$selection hits")
                        } else {
                            feedViewModel.fetchNewCrate(selection)
                        }
                        navController.navigate(Screen.HomeFeed.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    // NEW: Handle specific track clicks from search results
                    onTrackClick = { clickedTrack, fullSearchResults ->
                        val startIndex = fullSearchResults.indexOf(clickedTrack).coerceAtLeast(0)

                        feedViewModel.playVaultTracks(
                            vaultName = "SEARCH RESULTS",
                            vaultTracks = fullSearchResults,
                            startIndex = startIndex
                        )

                        navController.navigate(Screen.HomeFeed.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // --- LIBRARY TAB ---
            composable("library") {
                com.example.tunify.presentation.library.LibraryScreen(
                    onNavigateToAffinity = { navController.navigate("vault_details/-1") },
                    onNavigateToVault = { vaultId -> navController.navigate("vault_details/$vaultId") }
                )
            }

            // --- VAULT DETAILS TAB ---
            composable(
                route = "vault_details/{vaultId}",
                arguments = listOf(androidx.navigation.navArgument("vaultId") { type = androidx.navigation.NavType.IntType })
            ) {
                val vaultViewModel: com.example.tunify.presentation.library.VaultDetailsViewModel = hiltViewModel()
                val vaultDetails by vaultViewModel.vaultDetails.collectAsState()

                com.example.tunify.presentation.library.VaultDetailsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTrackClick = { clickedTrackEntity ->
                        vaultDetails?.let { details ->
                            // 1. Map all local DB entities to domain tracks safely
                            val domainTracks = details.tracks.map { it.toTrack() }

                            // 2. Find the exact tapped song by comparing IDs directly
                            val startIndex = domainTracks.indexOfFirst { it.id == clickedTrackEntity.id }.coerceAtLeast(0)

                            // 3. Push the playlist into your FeedViewModel
                            feedViewModel.playVaultTracks(
                                vaultName = details.vault.name,
                                vaultTracks = domainTracks,
                                startIndex = startIndex
                            )

                            // 4. Snap back to the Feed screen to watch it play
                            navController.navigate(Screen.HomeFeed.route) {
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