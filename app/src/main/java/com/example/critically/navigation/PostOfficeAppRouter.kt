package com.example.critically.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen() {
    data object LoginScreen : Screen()
    data object SignUpScreen : Screen()
}

object PostOfficeAppRouter {
    val currentScreen : MutableState<Screen> = mutableStateOf(Screen.LoginScreen)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}