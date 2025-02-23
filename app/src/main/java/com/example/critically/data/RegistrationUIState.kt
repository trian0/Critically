package com.example.critically.data

data class RegistrationUiState (
    var name: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var privacyPolicyAccepted: Boolean = false,

    var nameError: Boolean = true,
    var usernameError: Boolean = true,
    var emailError: Boolean = true,
    var passwordError: Boolean = true,
    var privacyPolicyError: Boolean = true,
)