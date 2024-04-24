package com.example.critically.data.api

import com.example.critically.models.BookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiBooks {

    @GET("volumes")
    suspend fun getBooksList(
        @Query("q") book: String,

    ): BookResponse

    companion object {
        const val BASE_URL = "https://www.googleapis.com/books/v1/"
    }
}