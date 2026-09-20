package com.example.tunify.di

import android.content.Context
import androidx.room.Room
import com.example.tunify.data.local.TunifyDatabase
import com.example.tunify.data.local.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTunifyDatabase(@ApplicationContext context: Context): TunifyDatabase {
        return Room.databaseBuilder(
            context,
            TunifyDatabase::class.java,
            "tunify_db"
        ).build()
    }
    @Provides
    @Singleton
    fun provideVaultDao(database: TunifyDatabase): com.example.tunify.data.local.dao.VaultDao {
        return database.vaultDao
    }
    @Provides
    @Singleton
    fun provideTrackDao(database: TunifyDatabase): TrackDao {
        return database.trackDao
    }
}