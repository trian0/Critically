package com.example.critically.data

data class RegistrationUiState (
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var privacyPolicyAccepted: Boolean = false,

    var firstNameError: Boolean = true,
    var lastNameError: Boolean = true,
    var emailError: Boolean = true,
    var passwordError: Boolean = true,
    var privacyPolicyError: Boolean = true,
)