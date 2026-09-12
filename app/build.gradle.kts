plugins {
    alias(libs.plugins.android.application)
    kotlin("android") // MUST be included for KAPT to work
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")    // Correct native KTS syntax for KAPT
    id("com.google.dagger.hilt.android")
   // id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tunify"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.tunify"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Your Existing Catalog Dependencies ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- Tunify Required Dependencies ---

    // Extended Icons & Navigation
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // AndroidX Media3 (ExoPlayer Audio Engine)
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    implementation("androidx.media3:media3-session:1.5.0")
    implementation("androidx.media3:media3-ui:1.5.0")

    // Dagger Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.60.1")
    kapt("com.google.dagger:hilt-compiler:2.60.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // Retrofit & Networking (REST API & Previews)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Room Database (Local Persistence)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // WorkManager (Background Sync)
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Firebase (BOM for Cloud Persistence, Auth & FCM)
    //implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
   // implementation("com.google.firebase:firebase-auth")
  //  implementation("com.google.firebase:firebase-firestore")
  //  implementation("com.google.firebase:firebase-messaging")

    // Coil (Image Loading for Album Covers)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // AndroidX Media3 (ExoPlayer) for Audio Streaming
    implementation("androidx.media3:media3-exoplayer:1.2.1")

    //compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // --- Testing ---
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}