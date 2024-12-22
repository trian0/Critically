package com.example.critically.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.critically.models.Movies

@Composable
fun MovieDetailScreen(modifier: Modifier = Modifier, movie: Movies) {
    Text(text = movie.title)
}