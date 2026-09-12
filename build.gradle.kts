plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Add these two!
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}