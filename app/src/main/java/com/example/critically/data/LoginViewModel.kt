package com.example.critically.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.critically.data.rules.ValidationResult
import com.example.critically.data.rules.Validator
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    var loginUIState = mutableStateOf(LoginUIState())

    var allValidationsPassed = mutableStateOf(false)

    var loginInProgress = mutableStateOf(false)

    var showErrorAlertDialog = mutableStateOf(false)

    private var emailResult = ValidationResult()
    private var passwordResult = ValidationResult()

    fun onEvent(event: LoginUIEvent) {
        when(event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email
                )
                validateEmail()
            }

            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
                validatePassword()
            }

            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
        }
        validateLoginUIDataWithRules()
    }

    private fun validateEmail() {
        emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )

        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status
        )
    }

    private fun validatePassword() {
        passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )

        loginUIState.value = loginUIState.value.copy(
            passwordError = passwordResult.status
        )
    }

    private fun validateLoginUIDataWithRules() {
        allValidationsPassed.value = emailResult.status && passwordResult.status
    }

    private fun login() {
        loginInProgress.value = true

        val email = loginUIState.value.email
        val password = loginUIState.value.password

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    loginInProgress.value = false
                    PostOfficeAppRouter.navigateTo(Screen.BottomNavigation)
                }
            }
            .addOnFailureListener {
                loginInProgress.value = false
                showErrorAlertDialog.value = true
            }
    }

}