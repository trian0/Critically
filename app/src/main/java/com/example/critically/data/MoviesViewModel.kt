package com.example.critically.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critically.models.Movies
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val moviesRepository: MoviesRepository
): ViewModel() {

    private val _movies = MutableStateFlow<List<Movies>>(emptyList())
    val movies = _movies.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            moviesRepository.getMoviesList().collectLatest { result ->
                when(result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { movies ->
                            _movies.update { movies }
                        }
                    }
                }
            }
        }
    }

}