package com.example.critically.data

data class LoginUIState (
    var email: String = "",
    var password: String = "",

    var emailError: Boolean = true,
    var passwordError: Boolean = true,
)