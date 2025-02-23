package com.example.critically.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.critically.components.ButtonComponent
import com.example.critically.components.CheckboxComponent
import com.example.critically.components.ClickableLoginTextComponent
import com.example.critically.components.DividerTextComponent
import com.example.critically.components.LogoImageCenter
import com.example.critically.components.MyTextFieldComponent
import com.example.critically.components.NormalTextComponent
import com.example.critically.components.PasswordTextFieldComponent
import com.example.critically.data.SignUpUIEvent
import com.example.critically.data.SignUpViewModel
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.example.critically.ui.theme.Primary
import com.example.critically.R
import com.example.critically.components.ErrorAlertDialog

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel = viewModel(),
) {
    val shouldShowUsernameExistDialog = remember { mutableStateOf(false) }
    val shouldShowUserExistDialog = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 88.dp, bottom = 28.dp, start = 28.dp, end = 28.dp)
        ) {
            LogoImageCenter(100.dp)
            Spacer(modifier = Modifier.height(20.dp))

            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.nick_name),
                painterResource(id = R.drawable.profile),
                onTextSelected = {
                    signUpViewModel.onEvent(SignUpUIEvent.NameChanged(it))
                },
                errorStatus = signUpViewModel.registrationUIState.value.nameError,
                textError = stringResource(id = R.string.first_name_error_message)
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.user_name),
                painterResource(id = R.drawable.arroba),
                onTextSelected = {
                    signUpViewModel.onEvent(SignUpUIEvent.UsernameChanged(it))
                },
                errorStatus = signUpViewModel.registrationUIState.value.usernameError,
                textError = stringResource(id = R.string.last_name_error_message)
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                painterResource(id = R.drawable.mail),
                onTextSelected = {
                    signUpViewModel.onEvent(SignUpUIEvent.EmailChanged(it))
                },
                errorStatus = signUpViewModel.registrationUIState.value.emailError,
                textError = stringResource(id = R.string.email_error_message)
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource(id = R.drawable.lock),
                onTextSelected = {
                    signUpViewModel.onEvent(SignUpUIEvent.PasswordChanged(it))
                },
                errorStatus = signUpViewModel.registrationUIState.value.passwordError
            )
            Spacer(modifier = Modifier.height(10.dp))
            NormalTextComponent(
                value = stringResource(id = R.string.password_error_message),
                weight = FontWeight.Normal,
                size = 10.sp
            )
            CheckboxComponent(
                onTextSelected = {
                    //PostOfficeAppRouter.navigateTo(Screen.TermsAndConditionsScreen)
                },
                onCheckedChange = {
                    signUpViewModel.onEvent(SignUpUIEvent.PrivacyPolicyCheckboxClicked(it))
                }
            )
            Spacer(modifier = Modifier.height(40.dp))
            ButtonComponent(
                value = stringResource(id = R.string.register),
                onButtonClicked = {
                    signUpViewModel.onEvent(SignUpUIEvent.RegisterButtonClicked)
                },
                isEnabled = signUpViewModel.allValidationsPassed.value
            )
            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
            })
        }

        if (signUpViewModel.signUpInProgress.value) {
            CircularProgressIndicator(color = Primary)
        }

        if (signUpViewModel.showErrorAlertDialog.value) {
            shouldShowUsernameExistDialog.value = true
        }

        if (signUpViewModel.showErrorUserAlertDialog.value) {
            shouldShowUserExistDialog.value = true
        }

        ErrorAlertDialog(
            text = stringResource(id = R.string.error_register_message),
            visibility = shouldShowUsernameExistDialog
        )

        ErrorAlertDialog(
            text = stringResource(id = R.string.error_user_already_exist_message),
            visibility = shouldShowUserExistDialog
        )
    }
}

@Preview
@Composable
fun DefaultPreviewOfSignUpScreen() {
    SignUpScreen()
}