package com.example.critically.data

sealed class ProfileUIEvent {

    data object LogoutButtonClicked : ProfileUIEvent()
}