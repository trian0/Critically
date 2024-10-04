package com.example.critically.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    fun onEvent(event: ProfileUIEvent) {
        when(event) {
            is ProfileUIEvent.LogoutButtonClicked -> {
                logout()
            }
        }
    }
    private fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
    }
}