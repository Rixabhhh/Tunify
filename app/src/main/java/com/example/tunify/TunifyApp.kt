package com.example.tunify

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The entry point of the Tunify application.
 *
 * @HiltAndroidApp triggers Dagger's code generation, creating a base
 * dependency injection container that is attached to the app's lifecycle.
 */
@HiltAndroidApp
class TunifyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // As the app grows, any global libraries that need to be initialized
        // the exact millisecond the app launches will go here.
        // For example: Timber logging, Firebase crashlytics initialization, etc.
    }
}