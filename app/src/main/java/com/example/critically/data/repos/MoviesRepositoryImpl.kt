package com.example.critically.data.repos

import com.example.critically.data.Result
import com.example.critically.data.api.ApiMovie
import com.example.critically.models.Movies
import com.example.critically.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class MoviesRepositoryImpl(
    private val apiMovie: ApiMovie
) : MoviesRepository {
    override suspend fun getMoviesList(): Flow<Result<List<Movies>>> {
        return flow {
            val moviesFromApi = try {
                apiMovie.getMoviesList(1, Constants.TMDB_API_KEY)
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading movies"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading movies"))
                return@flow
            }

            emit(Result.Success(moviesFromApi.results))
        }
    }

    override suspend fun getSearchedMoviesList(movieName: StateFlow<String>): Flow<Result<List<Movies>>> {
        return flow {
            val moviesSearched = try {
                apiMovie.getSearchMoviesList(movieName.value, Constants.TMDB_API_KEY)
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading movies"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading movies"))
                return@flow
            }

            emit(Result.Success(moviesSearched.results))
        }
    }
}