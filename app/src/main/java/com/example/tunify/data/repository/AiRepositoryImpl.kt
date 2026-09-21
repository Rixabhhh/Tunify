package com.example.tunify.data.repository

import com.example.tunify.core.common.Resource
import com.example.tunify.domain.model.Track
import com.example.tunify.domain.model.VibeAnalysis
import com.example.tunify.domain.repository.AiRepository
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : AiRepository {

    // 1. SIMPLE CACHE: Remembers vibes we've already generated this session
    private val vibeCache = mutableMapOf<String, VibeAnalysis>()

    override fun analyzeTrackVibe(track: Track): Flow<Resource<VibeAnalysis>> = flow {
        emit(Resource.Loading)

        // Check if we already analyzed this track. If yes, return it instantly!
        vibeCache[track.id]?.let { cachedVibe ->
            emit(Resource.Success(cachedVibe))
            return@flow
        }

        val prompt = """
            You are an elite music curator and cultural critic. 
            Analyze the following music track based on its title and artist:
            - Title: "${track.title}"
            - Artist: "${track.artist}"
            
            Return ONLY a valid, raw JSON object with no markdown fences, no backticks, and no introductory or explanatory text.
            The JSON object must have exactly these keys:
            {
              "mood": "2-3 vivid emotional adjectives (e.g., Ethereal, Melancholic, Euphoric)",
              "genreBlend": "A sharp, modern genre synthesis (e.g., Synthwave / Dark Ambient)",
              "aesthetic": "A visual/cinematic vibe (e.g., Neon-drenched Tokyo Alley at 3 AM)",
              "description": "A punchy, compelling 2-sentence breakdown capturing the sonic essence and mood of the track."
            }
        """.trimIndent()

        // 2. SILENT RETRY LOGIC: Try up to 3 times before giving up
        var attempt = 0
        val maxAttempts = 3

        while (attempt < maxAttempts) {
            try {
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text

                if (responseText.isNullOrBlank()) {
                    emit(Resource.Error("Content unavailable: Filtered by safety guardrails or empty response."))
                    return@flow
                }

                val cleanedJson = responseText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim()

                val jsonObject = JSONObject(cleanedJson)
                val vibe = VibeAnalysis(
                    mood = jsonObject.optString("mood", "Undefined Mood"),
                    genreBlend = jsonObject.optString("genreBlend", "Eclectic Mix"),
                    aesthetic = jsonObject.optString("aesthetic", "Atmospheric Soundscape"),
                    description = jsonObject.optString("description", "A unique auditory journey.")
                )

                // Save to cache for next time, then emit success
                vibeCache[track.id] = vibe
                emit(Resource.Success(vibe))
                return@flow // Exit the flow on success

            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: ""
                val isBusyError = errorMessage.contains("503") ||
                        errorMessage.contains("UNAVAILABLE") ||
                        errorMessage.contains("429") ||
                        errorMessage.contains("quota")

                attempt++

                if (isBusyError && attempt < maxAttempts) {
                    // Wait 1.5 seconds on the first retry, 3.0 seconds on the second
                    delay(1500L * attempt)
                    continue // Loop back up and try again!
                }

                // If we run out of attempts, or it's a completely different error, show the message
                val displayMessage = if (isBusyError) {
                    "The AI server is heavily congested right now. Please try again later."
                } else {
                    errorMessage.ifBlank { "Failed to generate track vibe analysis." }
                }

                emit(Resource.Error(displayMessage))
                return@flow
            }
        }
    }
}