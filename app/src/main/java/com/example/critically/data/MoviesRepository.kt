package com.example.critically.data

import com.example.critically.models.Movies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MoviesRepository {
    suspend fun getMoviesList(): Flow<Result<List<Movies>>>
    suspend fun getSearchedMoviesList(movieName: StateFlow<String>): Flow<Result<List<Movies>>>
}