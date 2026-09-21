# Tunify 🎵

A modern, state-driven Android music discovery platform designed for seamless audio streaming, curated playlist management, and AI-powered mood analysis. Built entirely with Kotlin and Jetpack Compose, Tunify strictly adheres to Clean Architecture principles to deliver a highly responsive, premium user experience.

## ✨ Core Features

* **Infinite Discovery Feed:** A debounced live-search engine that queries the iTunes API using Coroutines and Retrofit, delivering dynamic track feeds without UI freezing or network flooding.
* **The "Affinity" & Custom Vaults:** A robust local library system powered by Room Database. Users can instantly stash tracks into custom categorized vaults or hit "Like" to route them directly to the Affinity Vault.
* **Bulletproof Background Audio:** Integrated with AndroidX Media3 (ExoPlayer), supporting seamless background streaming, synchronized mini-player UI updates, and instant playlist switching.
* **AI Vibe Checker (Powered by Gemini):** Contextual, AI-driven track analysis that determines the mood, genre mix, and "vibe" of a song using the Gemini API, enforced with Responsible AI guardrails for safe content processing.
* **Premium UI/UX:** A distraction-free, reactive interface built in Jetpack Compose, featuring custom typography (Harmond Display) and smooth segmented scrubbers for an editorial, high-end aesthetic.

## 🛠 Tech Stack & Architecture

Tunify is engineered with scalability and maintainability in mind, utilizing the recommended modern Android development stack.

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose, Material Design 3
* **Architecture:** Clean Architecture (Domain, Data, Presentation layers), MVVM (Model-View-ViewModel)
* **Dependency Injection:** Dagger Hilt
* **Audio Engine:** AndroidX Media3 (ExoPlayer), MediaSession Service
* **Networking:** Retrofit2, OkHttp, Gson (iTunes API)
* **Local Persistence:** Room Database, Android DataStore
* **Concurrency:** Kotlin Coroutines, Flow / StateFlow
* **AI Integration:** Google Generative AI SDK (Gemini API)

## 🧩 Technical Challenges Overcome

Building a media player with a reactive UI involves navigating complex asynchronous states. Some of the major engineering hurdles solved in this project include:

1. **Media3 Lifecycle & Compose State Sync:** Managing the disconnect between ExoPlayer's asynchronous buffering states and Jetpack Compose's recomposition. Solved by implementing a strict ID-bound `LaunchedEffect` and a `playTrigger` nonce to force absolute UI-to-audio synchronization during rapid playlist swaps.
2. **ViewModel Scoping in Navigation Graphs:** Preventing Hilt from spinning up isolated, empty ViewModel instances across different routes. Solved by properly hoisting a shared `FeedViewModel` state, ensuring continuity between the Library, Search, and Feed screens.
3. **Debounced API Querying:** Preventing network exhaustion and rate-limiting from the iTunes API during live keystroke searches by engineering a highly efficient Coroutine debouncer.

## 🚀 Future Scope

* **Offline Caching:** Implement local caching of audio streams using ExoPlayer's `CacheDataSource` to allow playback of Vault tracks in airplane mode.
* **Spotify / Apple Music Handoff:** OAuth integration to allow users to instantly export their Tunify Vaults directly to their primary Spotify or Apple Music accounts.
* **Enhanced AI Recommendations:** Expand the Gemini integration from single-track "Vibe Checking" to generating complete, dynamic playlists based on natural language prompts (e.g., *"Make me a playlist for a rainy midnight drive"*).

