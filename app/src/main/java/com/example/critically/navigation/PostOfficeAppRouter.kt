package com.example.critically.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth

sealed class Screen() {
    data object LoginScreen : Screen()
    data object SignUpScreen : Screen()
    data object HomeScreen : Screen()
    data object BottomNavigation : Screen()
}

object PostOfficeAppRouter {
    private val auth = FirebaseAuth.getInstance()
    val currentScreen: MutableState<Screen> = if (auth.currentUser != null)
        mutableStateOf(Screen.BottomNavigation) else mutableStateOf(Screen.LoginScreen)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}