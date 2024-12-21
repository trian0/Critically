package com.example.critically.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critically.data.repos.BooksRepository
import com.example.critically.data.repos.MoviesRepository
import com.example.critically.models.Item
import com.example.critically.models.Movies
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val moviesRepository: MoviesRepository,
    private val booksRepository: BooksRepository,
): ViewModel() {

    private val _movies = MutableStateFlow<List<Movies>>(emptyList())
    val movies = _movies.asStateFlow()

    private val _books = MutableStateFlow<List<Item>>(emptyList())
    val books = _books.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun searchMovie(text: String) {
        if (text.isNotEmpty()) {
            _searchText.value = text
            _isSearching.value = true

            viewModelScope.launch {
                moviesRepository.getSearchedMoviesList(searchText).collectLatest { result ->
                    when(result) {
                        is Result.Error -> {
                            _showErrorToastChannel.send(true)
                            _isSearching.value = false
                        }
                        is Result.Success -> {
                            result.data?.let { movies ->
                                _movies.update { movies }
                                _isSearching.value = false
                            }
                        }
                    }
                }
            }
            return
        }
        _movies.value = emptyList()
        _isSearching.value = false
    }

    fun searchBook(text: String) {
        if (text.isNotEmpty()) {
            _searchText.value = text
            _isSearching.value = true

            viewModelScope.launch {
                booksRepository.getSearchedBooksList(searchText).collectLatest { result ->
                    when(result) {
                        is Result.Error -> {
                            _showErrorToastChannel.send(true)
                            _isSearching.value = false
                        }
                        is Result.Success -> {
                            result.data?.let { books ->
                                _books.update { books }
                                _isSearching.value = false
                            }
                        }
                    }
                }
            }
            return
        }
        _books.value = emptyList()
        _isSearching.value = false
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }
}