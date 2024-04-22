package com.example.critically.data

import com.example.critically.models.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("movie/popular")
    suspend fun getMoviesList(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String,
    ): MoviesResponse

    @GET("search/movie")
    suspend fun getSearchMoviesList(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
    ): MoviesResponse

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }

}