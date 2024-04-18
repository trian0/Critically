package com.example.critically.data.rules

object Validator {

    fun validateFirstName(firstName: String): ValidationResult {
        return ValidationResult(
            (!firstName.isNullOrEmpty() && firstName.length >= 6)
        )
    }

    fun validateLastName(lastName: String): ValidationResult {
        return ValidationResult(
            (!lastName.isNullOrEmpty() && lastName.length >= 4)
        )
    }

    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (!email.isNullOrEmpty())
        )
    }

    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            (!password.isNullOrEmpty() && password.length >= 4)
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