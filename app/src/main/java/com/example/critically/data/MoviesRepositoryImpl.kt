package com.example.critically.data

import com.example.critically.models.Movies
import com.example.critically.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class MoviesRepositoryImpl(
    private val api: Api
): MoviesRepository {
    override suspend fun getMoviesList(): Flow<Result<List<Movies>>> {
        return flow {
            val moviesFromApi = try {
                api.getMoviesList(1, Constants.TMDB_API_KEY)
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
}