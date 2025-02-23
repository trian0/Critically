package com.example.critically.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.critically.BuildConfig
import com.example.critically.R
import com.example.critically.components.ButtonComponent
import com.example.critically.components.ButtonExtensionsLogin
import com.example.critically.components.ClickableLoginTextComponent
import com.example.critically.components.DividerTextComponent
import com.example.critically.components.ErrorAlertDialog
import com.example.critically.components.LogoImageCenter
import com.example.critically.components.MyTextFieldComponent
import com.example.critically.components.PasswordTextFieldComponent
import com.example.critically.components.UnderLinedTextComponent
import com.example.critically.data.LoginUIEvent
import com.example.critically.data.LoginViewModel
import com.example.critically.firebase.FirebaseUtils
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.example.critically.ui.theme.Primary
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    val shouldShowDialog = remember { mutableStateOf(false) }

    val auth = Firebase.auth
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val clientApi = BuildConfig.CLIENT_API

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 60.dp)
                .background(Color.White)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImageCenter(100.dp)
            Spacer(modifier = Modifier.height(20.dp))
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                painterResource(id = R.drawable.mail),
                onTextSelected = {
                    loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                },
                errorStatus = loginViewModel.loginUIState.value.emailError,
                textError = stringResource(id = R.string.email_error_message)
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource(id = R.drawable.lock),
                onTextSelected = {
                    loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                },
                errorStatus = loginViewModel.loginUIState.value.passwordError
            )
            Spacer(modifier = Modifier.height(40.dp))
            UnderLinedTextComponent(value = stringResource(id = R.string.forgot_password))
            Spacer(modifier = Modifier.height(40.dp))
            ButtonComponent(
                value = stringResource(id = R.string.login),
                onButtonClicked = {
                    loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                },
                isEnabled = loginViewModel.allValidationsPassed.value
            )
            Spacer(modifier = Modifier.height(20.dp))
            ButtonExtensionsLogin(
                onButtonClicked = {
                    scope.launch {
                        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(clientApi)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        try {
                            val result = credentialManager.getCredential(
                                context = context,
                                request = request
                            )

                            val credential = result.credential
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            val googleIdToken = googleIdTokenCredential.idToken

                            val firebaseCredential =
                                GoogleAuthProvider.getCredential(googleIdToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        FirebaseUtils.createGoogleUserInFirestore(Firebase.auth.currentUser)
                                        PostOfficeAppRouter.navigateTo(Screen.BottomNavigation)
                                    }
                                }
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                            FirebaseCrashlytics.getInstance().sendUnsentReports()
                            Toast.makeText(
                                context,
                                "Error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }
                    }
                },
                painterResource(R.drawable.google),
                stringResource(R.string.btn_login_google)
            )
            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {
                PostOfficeAppRouter.navigateTo(Screen.SignUpScreen)
            })
        }

        if (loginViewModel.loginInProgress.value) {
            CircularProgressIndicator(color = Primary)
        }

        if (loginViewModel.showErrorAlertDialog.value) {
            shouldShowDialog.value = true
        }

        ErrorAlertDialog(
            text = stringResource(id = R.string.error_login_message),
            visibility = shouldShowDialog
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}