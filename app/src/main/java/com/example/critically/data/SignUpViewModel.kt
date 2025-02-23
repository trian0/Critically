package com.example.critically.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.critically.data.rules.ValidationResult
import com.example.critically.data.rules.Validator
import com.example.critically.firebase.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SignUpViewModel : ViewModel() {

    var registrationUIState = mutableStateOf(RegistrationUiState())

    var allValidationsPassed = mutableStateOf(false)

    var signUpInProgress = mutableStateOf(false)

    var showErrorAlertDialog = mutableStateOf(false)

    var showErrorUserAlertDialog = mutableStateOf(false)

    private var nameResult = ValidationResult()
    private var usernameResult = ValidationResult()
    private var emailResult = ValidationResult()
    private var passwordResult = ValidationResult()
    private var privacyPolicyResult = ValidationResult()
    private val TAG = "SignUpViewModel"

    fun onEvent(event: SignUpUIEvent) {
        Log.d(TAG, "onEvent")
        showErrorAlertDialog.value = false
        showErrorUserAlertDialog.value = false
        when (event) {
            is SignUpUIEvent.NameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    name = event.name
                )
                validateName()
            }

            is SignUpUIEvent.UsernameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    username = "@${event.username}"
                )
                validateUsername()
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
        }
        validateDataWithRules()
    }

    private fun signUp() {
        Log.d(TAG, "signUp")
        showErrorAlertDialog.value = false
        createUserInFirebase(
            email = registrationUIState.value.email,
            password = registrationUIState.value.password,
            name = registrationUIState.value.name,
            username = registrationUIState.value.username
        )
    }

    private fun validateName() {
        Log.d(TAG, "validateFirstName")
        nameResult = Validator.validateName(
            name = registrationUIState.value.name
        )

        registrationUIState.value = registrationUIState.value.copy(
            nameError = nameResult.status
        )
    }

    private fun validateUsername() {
        Log.d(TAG, "validateLastName")
        usernameResult = Validator.validateUsername(
            username = registrationUIState.value.username
        )

        registrationUIState.value = registrationUIState.value.copy(
            usernameError = usernameResult.status
        )
    }

    private fun validateEmail() {
        Log.d(TAG, "validateEmail")
        emailResult = Validator.validateEmail(
            email = registrationUIState.value.email
        )

        registrationUIState.value = registrationUIState.value.copy(
            emailError = emailResult.status
        )
    }

    private fun validatePassword() {
        Log.d(TAG, "validatePassword")
        passwordResult = Validator.validatePassword(
            password = registrationUIState.value.password
        )

        registrationUIState.value = registrationUIState.value.copy(
            passwordError = passwordResult.status
        )
    }

    private fun validatePrivacyPolicy() {
        Log.d(TAG, "validatePrivacyPolicy")
        privacyPolicyResult = Validator.validatePrivacyPolicyAcceptance(
            statusValue = registrationUIState.value.privacyPolicyAccepted
        )

        registrationUIState.value = registrationUIState.value.copy(
            privacyPolicyError = privacyPolicyResult.status
        )
    }

    private fun validateDataWithRules() {
        Log.d(TAG, "validateDataWithRules")
        allValidationsPassed.value = nameResult.status && usernameResult.status
                && emailResult.status && passwordResult.status && privacyPolicyResult.status
    }

    private fun createUserInFirebase(email: String, password: String, name: String, username: String) {
        Log.d(TAG, "createUserInFirebase")
        signUpInProgress.value = true
        showErrorAlertDialog.value = false

        try {
            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        checkIfUserExistsAndCreateUser(user?.uid, username, name, email)
                    } else {
                        Log.e("Auth", "Erro ao criar usuário: ${task.exception?.message}")
                        signUpInProgress.value = false
                        showErrorUserAlertDialog.value = true
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "createUserInFirebase: ${it.message}")
                    signUpInProgress.value = false
                    showErrorUserAlertDialog.value = true
                }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().sendUnsentReports()
            e.printStackTrace()
            signUpInProgress.value = false
            showErrorAlertDialog.value = true
        }
    }

    private fun checkIfUserExistsAndCreateUser(uid: String?, username: String, name: String, email: String) {
        if (FirebaseUtils.checkIfUserExists(username)) {
            Log.e("Firestore", "Username já existe!")
            signUpInProgress.value = false
            showErrorAlertDialog.value = true
        } else {
            signUpInProgress.value = false
            FirebaseUtils.createUserInFirestore(uid, username, name, email)
        }
    }

}