package com.example.critically.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.critically.data.rules.ValidationResult
import com.example.critically.data.rules.Validator
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class SignUpViewModel : ViewModel() {

    var registrationUIState = mutableStateOf(RegistrationUiState())

    var allValidationsPassed = mutableStateOf(false)

    var signUpInProgress = mutableStateOf(false)

    private var firstNameResult = ValidationResult()
    private var lastNameResult = ValidationResult()
    private var emailResult = ValidationResult()
    private var passwordResult = ValidationResult()
    private var privacyPolicyResult = ValidationResult()

    fun onEvent(event: SignUpUIEvent) {
        when (event) {
            is SignUpUIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    firstName = event.firstName
                )
                validateFirstName()
            }

            is SignUpUIEvent.LastNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    lastName = event.lastName
                )
                validateLastName()
            }

            is SignUpUIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
                validateEmail()
            }

            is SignUpUIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                validatePassword()
            }

            is SignUpUIEvent.RegisterButtonClicked -> {
                signUp()
            }

            is SignUpUIEvent.PrivacyPolicyCheckboxClicked -> {
                registrationUIState.value = registrationUIState.value.copy(
                    privacyPolicyAccepted = event.status
                )
                validatePrivacyPolicy()
            }

            else -> {}
        }
        validateDataWithRules()
    }

    private fun signUp() {
        createUserInFirebase(
            email = registrationUIState.value.email,
            password = registrationUIState.value.password,
        )
    }

    private fun validateFirstName() {
        firstNameResult = Validator.validateFirstName(
            firstName = registrationUIState.value.firstName
        )

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = firstNameResult.status
        )
    }

    private fun validateLastName() {
        lastNameResult = Validator.validateLastName(
            lastName = registrationUIState.value.lastName
        )

        registrationUIState.value = registrationUIState.value.copy(
            lastNameError = lastNameResult.status
        )
    }

    private fun validateEmail() {
        emailResult = Validator.validateEmail(
            email = registrationUIState.value.email
        )

        registrationUIState.value = registrationUIState.value.copy(
            emailError = emailResult.status
        )
    }

    private fun validatePassword() {
        passwordResult = Validator.validatePassword(
            password = registrationUIState.value.password
        )

        registrationUIState.value = registrationUIState.value.copy(
            passwordError = passwordResult.status
        )
    }

    private fun validatePrivacyPolicy() {
        privacyPolicyResult = Validator.validatePrivacyPolicyAcceptance(
            statusValue = registrationUIState.value.privacyPolicyAccepted
        )

        registrationUIState.value = registrationUIState.value.copy(
            privacyPolicyError = privacyPolicyResult.status
        )
    }

    private fun validateDataWithRules() {
        allValidationsPassed.value = firstNameResult.status && lastNameResult.status
                && emailResult.status && passwordResult.status && privacyPolicyResult.status
    }

    private fun createUserInFirebase(email: String, password: String) {
        signUpInProgress.value = true
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                signUpInProgress.value = false
                if (it.isSuccessful) {
                    PostOfficeAppRouter.navigateTo(Screen.BottomNavigation)
                }
            }
            .addOnFailureListener {
                signUpInProgress.value = false
            }
    }

    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        val authStateListener = AuthStateListener {
            if (it.currentUser == null) {
                PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
    }

}