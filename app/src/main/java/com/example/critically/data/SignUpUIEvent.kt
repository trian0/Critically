package com.example.critically.data

sealed class SignUpUIEvent {

    data class NameChanged(val name: String) : SignUpUIEvent()
    data class UsernameChanged(val username: String) : SignUpUIEvent()
    data class EmailChanged(val email: String) : SignUpUIEvent()
    data class PasswordChanged(val password: String) : SignUpUIEvent()
    data class PrivacyPolicyCheckboxClicked(val status: Boolean): SignUpUIEvent()

    data object RegisterButtonClicked : SignUpUIEvent()

}