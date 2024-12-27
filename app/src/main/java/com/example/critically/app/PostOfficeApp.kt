package com.example.critically.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.example.critically.screens.BottomNavigation
import com.example.critically.screens.LoginScreen
import com.example.critically.screens.MovieDetailScreen
import com.example.critically.screens.SearchScreen
import com.example.critically.screens.SignUpScreen

@Composable
fun PostOfficeApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = PostOfficeAppRouter.currentScreen, label = "") { currentState ->
            when (currentState.value) {
                Screen.LoginScreen -> LoginScreen()
                Screen.SignUpScreen -> SignUpScreen()
                Screen.BottomNavigation -> BottomNavigation()
                is Screen.MovieDetailScreen -> {
                    val movie = (currentState.value as Screen.MovieDetailScreen).movie
                    MovieDetailScreen(movie = movie)
                }
                Screen.Searchscreen -> SearchScreen()
            }
        }
    }
}
