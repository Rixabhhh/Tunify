package com.example.tunify.core.common

/**
 * A generic wrapper class that represents the state of a data request.
 * The 'out T' means it is covariant (it can safely produce items of type T).
 */
sealed interface Resource<out T> {

    // Contains the actual payload (e.g., List<Track>) when successful
    data class Success<T>(val data: T) : Resource<T>

    // Contains an error message and optional exception for debugging
    data class Error(val message: String, val exception: Throwable? = null) : Resource<Nothing>

    // Represents the fetching state (useful for triggering circular progress indicators)
    data object Loading : Resource<Nothing>
}