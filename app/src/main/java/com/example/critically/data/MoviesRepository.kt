package com.example.critically.data

import com.example.critically.models.Movies
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun getMoviesList(): Flow<Result<List<Movies>>>
}