package com.example.critically.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.critically.RetrofitInstance
import com.example.critically.data.BooksViewModel
import com.example.critically.data.repos.MoviesRepositoryImpl
import com.example.critically.data.MoviesViewModel
import com.example.critically.data.repos.BooksRepositoryImpl
import com.example.critically.ui.theme.GrayColor
import com.example.critically.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {

    val movieViewModel: MoviesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MoviesViewModel(MoviesRepositoryImpl(RetrofitInstance.apiMovie)) as T
        }
    })

    val bookViewModel: BooksViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BooksViewModel(BooksRepositoryImpl(RetrofitInstance.apiBook)) as T
        }
    })

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val moviesList = movieViewModel.movies.collectAsState().value
        val booksList = bookViewModel.books.collectAsState().value
        val searchText = movieViewModel.searchText.collectAsState().value
        val isSearching = movieViewModel.isSearching.collectAsState().value
        val context = LocalContext.current

        bookViewModel.searchBook("vida")

        LaunchedEffect(key1 = movieViewModel.showErrorToastChannel) {
            movieViewModel.showErrorToastChannel.collectLatest { show ->
                if (show) {
                    Toast.makeText(
                        context, "Error", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary,
                        scrolledContainerColor = Primary,
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = searchText,
                                onValueChange = movieViewModel::searchMovie,
                                Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(horizontal = 40.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White,
                                    cursorColor = Primary,
                                    placeholderColor = Primary,
                                    textColor = Primary,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                                shape = RoundedCornerShape(15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "search icon",
                                        tint = Primary
                                    )
                                },
                                textStyle = TextStyle.Default
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (moviesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Nada encontrado.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(moviesList) { movie ->
                        if (!movie.poster_path.isNullOrBlank()) {
                            val url = "https://image.tmdb.org/t/p/original${movie.poster_path}"
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .height(265.dp)
                                        .fillMaxWidth(),
                                    colors = CardColors(
                                        containerColor = Color.White,
                                        contentColor = Color.White,
                                        disabledContainerColor = Color.Gray,
                                        disabledContentColor = Color.Black
                                    ),
                                    border = BorderStroke(5.dp, Primary)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(10.dp)
                                                    .fillMaxHeight()
                                            ) {
                                                AsyncImage(
                                                    model = url,
                                                    contentDescription = "",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(8.dp)),
                                                )
                                            }

                                            // Informações na direita da AsyncImage
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(8.dp)
                                                    .fillMaxSize(),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = movie.title,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Primary,
                                                )
                                                Spacer(modifier = Modifier.height(30.dp))
                                                // Informações sobre a data de lançamento
                                                Row {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            Icons.Filled.Schedule,
                                                            contentDescription = "",
                                                            tint = Primary
                                                        )
                                                        val releaseDateText =
                                                            if (movie.release_date.isNotEmpty() || movie.release_date.isNotBlank()) {
                                                                val string = movie.release_date
                                                                val date = LocalDate.parse(
                                                                    string,
                                                                    DateTimeFormatter.ISO_DATE
                                                                )
                                                                date.year.toString()
                                                            } else {
                                                                "Não informado"
                                                            }
                                                        Text(
                                                            text = releaseDateText,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Primary,
                                                            modifier = Modifier.padding(start = 5.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(30.dp))
                                                    // Informação sobre a avaliação do filme
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            Icons.Filled.StarRate,
                                                            contentDescription = "",
                                                            tint = Color.Yellow
                                                        )
                                                        Text(
                                                            text = movie.vote_average.format(1),
                                                            fontWeight = FontWeight.Bold,
                                                            color = Primary,
                                                            modifier = Modifier.padding(start = 5.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(30.dp))

                                                Row(
                                                    modifier = Modifier
                                                        .padding(bottom = 8.dp)
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Button(
                                                        onClick = { },
                                                        colors = ButtonColors(
                                                            containerColor = Primary,
                                                            contentColor = Color.White,
                                                            disabledContentColor = Color.Black,
                                                            disabledContainerColor = Color.Gray,
                                                        ),
                                                        border = BorderStroke(2.dp, Primary),
                                                    ) {
                                                        Text(text = "Ver Mais")
                                                    }

                                                    Icon(
                                                        imageVector = Icons.Filled.Flag,
                                                        contentDescription = "",
                                                        tint = GrayColor,
                                                        modifier = Modifier
                                                            .border(
                                                                BorderStroke(2.dp, GrayColor),
                                                                shape = RoundedCornerShape(50.dp)
                                                            )
                                                            .padding(8.dp),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)