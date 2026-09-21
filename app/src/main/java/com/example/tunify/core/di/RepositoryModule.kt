package com.example.tunify.core.di

import com.example.tunify.data.repository.AiRepositoryImpl
import com.example.tunify.data.repository.TrackRepositoryImpl
import com.example.tunify.domain.repository.AiRepository
import com.example.tunify.domain.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the TrackRepository interface to its concrete implementation.
     *
     * @param impl The implementation Hilt already knows how to build
     *             (because we added @Inject to TrackRepositoryImpl's constructor).
     * @return The interface that the rest of the app will request.
     */
    @Binds
    @Singleton
    abstract fun bindTrackRepository(
        impl: TrackRepositoryImpl
    ): TrackRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        aiRepositoryImpl: AiRepositoryImpl
    ): AiRepository

}