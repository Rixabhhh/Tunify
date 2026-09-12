package com.example.tunify.presentation.navigation

// A sealed class is like a highly restricted enum. It ensures we don't make typos when navigating.
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding_screen")
    object Main : Screen("main_screen") // This will hold the Bottom Nav Bar
    object HomeFeed : Screen("home_feed_screen") // Inside Main
    object SearchExplore : Screen("search_explore_screen") // Inside Main
}