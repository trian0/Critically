package com.example.critically

import com.example.critically.data.api.ApiBooks
import com.example.critically.data.api.ApiMovie
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val apiMovie: ApiMovie = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(ApiMovie.BASE_URL)
        .client(client)
        .build()
        .create(ApiMovie::class.java)

    val apiBook: ApiBooks = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(ApiBooks.BASE_URL)
        .client(client)
        .build()
        .create(ApiBooks::class.java)
}