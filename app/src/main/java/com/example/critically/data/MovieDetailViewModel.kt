package com.example.critically.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critically.data.repos.MoviesRepository
import com.example.critically.models.Backdrop
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val moviesRepository: MoviesRepository,
): ViewModel() {

    private val _images = MutableStateFlow<ArrayList<Backdrop>>(ArrayList())
    val images = _images.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun getMovieImage(movieId: Int) {
        if (movieId > 0) {
            _isSearching.value = true

            viewModelScope.launch {
                moviesRepository.getMovieImages(movieId).collectLatest { result ->
                    when(result) {
                        is Result.Error -> {
                            _showErrorToastChannel.send(true)
                            _isSearching.value = false
                        }

                        is Result.Success -> {
                            result.data?.let { images ->
                                _images.update { images }
                                _isSearching.value = false
                            }
                        }
                    }
                }
            }
            return
        }
        _images.value = ArrayList()
        _isSearching.value = false
    }
}