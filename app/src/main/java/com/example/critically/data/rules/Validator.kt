package com.example.critically.data.rules

object Validator {

    fun validateFirstName(firstName: String): ValidationResult {
        return ValidationResult(
            (firstName.isNotEmpty() && firstName.length >= 3)
        )
    }

    fun validateLastName(lastName: String): ValidationResult {
        return ValidationResult(
            (lastName.isNotEmpty() && lastName.length >= 3)
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