package com.example.tunify.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    // 1. WE HOISTED THE VIEWMODEL HERE! Now it survives tab switches.
    feedViewModel: com.example.tunify.presentation.feed.FeedViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF09090B),
                contentColor = Color.White
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

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
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color(0xFFD8B4FE),
                        indicatorColor = Color(0xFFD8B4FE),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
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
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color(0xFFD8B4FE),
                        indicatorColor = Color(0xFFD8B4FE),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
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
                val tracks by feedViewModel.tracks.collectAsState()
                val isLoading by feedViewModel.isLoading.collectAsState()

                if (isLoading) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFD8B4FE))
                    }
                } else if (tracks.isNotEmpty()) {
                    com.example.tunify.presentation.feed.FeedScreen(tracks = tracks)
                } else {
                    // NEW: The Fallback UI to prevent the White Screen!
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF09090B)),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
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

                // Define our known grid cards so we can tell them apart from live search artists
                val knownGenres = listOf("Pop", "Indie", "Rock", "R&B", "Phonk", "Synthwave", "Lo-Fi")

                com.example.tunify.presentation.search.SearchExploreScreen(
                    onGenreClick = { selection ->

                        // THE SMART ROUTER
                        if (selection == "Discover Mix") {
                            // 1. They clicked the master reset card
                            feedViewModel.fetchNewCrate("top hits")
                        } else if (knownGenres.contains(selection)) {
                            // 2. They clicked a colorful genre card. Add "hits" to get better API results.
                            feedViewModel.fetchNewCrate("$selection hits")
                        } else {
                            // 3. They clicked a live search result! Search the raw Artist Name to create an "Artist Radio".
                            feedViewModel.fetchNewCrate(selection)
                        }

                        navController.navigate(Screen.HomeFeed.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}