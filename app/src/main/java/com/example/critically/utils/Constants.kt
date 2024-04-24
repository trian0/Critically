package com.example.critically.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import com.example.critically.models.BottomNavItem

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = "home"
        ),
        BottomNavItem(
            label = "Search",
            icon = Icons.Filled.Search,
            route = "search"
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = "profile"
        )
    )

    const val TMDB_API_KEY = "57aa94fa2db004387caf24757eaccb93"
    const val GOOGLE_BOOKS_API_KEY = "AIzaSyAaJPCcsoOTs2aBPB1cbh2caHAx5Z0--K4"
}