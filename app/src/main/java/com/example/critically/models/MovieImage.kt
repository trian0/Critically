package com.example.critically.models

data class MovieImage(
    val backdrops: ArrayList<Backdrop>,
)

data class Backdrop (
    val aspect_ratio: Double,
    val height: Int,
    val iso_639_1: String?,
    val file_path: String,
    val vote_average: Double,
    val vote_count: Int,
    val width: Int
)

