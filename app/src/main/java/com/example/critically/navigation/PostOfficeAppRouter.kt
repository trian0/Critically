package com.example.critically.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.critically.models.Movies
import com.google.firebase.auth.FirebaseAuth

sealed class Screen() {
    data object LoginScreen : Screen()
    data object SignUpScreen : Screen()
    data object BottomNavigation : Screen()
    data object Searchscreen : Screen()
    data class MovieDetailScreen(val movie: Movies) : Screen()
}

object PostOfficeAppRouter {
    private val auth = FirebaseAuth.getInstance()

    val currentScreen: MutableState<Screen> = if (auth.currentUser != null)
        mutableStateOf(Screen.BottomNavigation) else mutableStateOf(Screen.LoginScreen)

    private val screenHistory: MutableList<Screen> = mutableListOf(currentScreen.value)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
        screenHistory.add(destination)
    }
    fun navigateBack() {
        if (screenHistory.size > 1) {
            screenHistory.removeAt(screenHistory.size - 1)
            currentScreen.value = screenHistory.last()
        }
    }
}