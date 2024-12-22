package com.example.critically

import com.example.critically.data.api.ApiBooks
import com.example.critically.data.api.ApiMovie
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1N2FhOTRmYTJkYjAwNDM4N2NhZjI0NzU3ZWFjY2I5MyIsIm5iZiI6MTYzMjg3ODY1Ny4wMTcsInN1YiI6IjYxNTNjMDQxNjdkY2M5MDA0M2UyODJmOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Mcazu5yH25ltzl1GviZP-HZMuPC4q0j_XCsdUHmd5Tc"
            )
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }

    private val clientMovie: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(headerInterceptor)
        .build()

    private val clientBook: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val apiMovie: ApiMovie = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(ApiMovie.BASE_URL)
        .client(clientMovie)
        .build()
        .create(ApiMovie::class.java)

    val apiBook: ApiBooks = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(ApiBooks.BASE_URL)
        .client(clientBook)
        .build()
        .create(ApiBooks::class.java)
}