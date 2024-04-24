package com.example.critically.models

data class BookResponse(
    val items: List<Item>,
    val kind: String,
    val totalItems: Int
)