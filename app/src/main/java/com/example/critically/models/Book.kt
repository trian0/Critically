package com.example.critically.models

data class Book(
    val authors: List<String>,
    val averageRating: Int,
    val contentVersion: String,
    val description: String,
    val imageLinks: ImageLinks,
    val infoLink: String,
    val language: String,
    val maturityRating: String,
    val pageCount: Int,
    val previewLink: String,
    val printType: String,
    val publishedDate: String,
    val publisher: String,
    val ratingsCount: Int,
    val subtitle: String,
    val title: String
)