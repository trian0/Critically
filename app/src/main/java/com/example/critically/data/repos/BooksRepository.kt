package com.example.critically.data.repos

import com.example.critically.data.Result
import com.example.critically.models.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BooksRepository {
    suspend fun getSearchedBooksList(bookName: StateFlow<String>): Flow<Result<List<Item>>>
}