package com.example.critically.data.rules

object Validator {

    fun validateName(name: String): ValidationResult {
        return ValidationResult(
            (name.isNotEmpty() && name.length >= 3)
        )
    }

    fun validateUsername(username: String): ValidationResult {
        return ValidationResult(
            (username.isNotEmpty() && username.length >= 3)
        )
    }

    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        )
    }

    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            (password.isNotEmpty() && password.length >= 8)
        )
    }

    fun validatePrivacyPolicyAcceptance(statusValue: Boolean): ValidationResult {
        return ValidationResult(
            statusValue
        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)