package com.example.tunify.data.repository

import com.example.tunify.core.common.Resource
import com.example.tunify.data.mapper.toTrack
import com.example.tunify.data.remote.dto.ItunesApiService
import com.example.tunify.domain.model.Track
import com.example.tunify.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val apiService: ItunesApiService
) : TrackRepository {

    override fun searchTracks(query: String): Flow<Resource<List<Track>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.searchTracks(query = query)
            val tracks = response.results
                .filter { !it.previewUrl.isNullOrBlank() }
                .map { it.toTrack() }

            emit(Resource.Success(tracks))
        } catch (e: HttpException) {
            emit(Resource.Error("Server error occurred.", e))
        } catch (e: IOException) {
            emit(Resource.Error("Check your internet connection.", e))
        } catch (e: Exception) {
            emit(Resource.Error("An unknown error occurred.", e))
        }
    }
}