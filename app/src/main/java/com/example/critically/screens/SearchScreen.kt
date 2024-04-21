package com.example.critically.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.critically.RetrofitInstance
import com.example.critically.data.MoviesRepositoryImpl
import com.example.critically.data.MoviesViewModel
import com.example.critically.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SearchScreen() {

    val viewModel: MoviesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MoviesViewModel(MoviesRepositoryImpl(RetrofitInstance.api)) as T
        }
    })

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val moviesList = viewModel.movies.collectAsState().value
        val context = LocalContext.current

        LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
            viewModel.showErrorToastChannel.collectLatest { show ->
                if (show) {
                    Toast.makeText(
                        context, "Error", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        if (moviesList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn {
                items(moviesList) { movie ->
                    val url = "https://image.tmdb.org/t/p/original${movie.backdrop_path}"
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(vertical = 5.dp, horizontal = 60.dp)
                                .height(265.dp)
                                .fillMaxWidth(), colors = CardColors(
                                containerColor = Primary,
                                contentColor = Color.White,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "",
                                            modifier = Modifier
                                                .width(200.dp)
                                                .padding(top = 10.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )

                                        Text(
                                            text = movie.title,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .padding(top = 20.dp)
                                            .fillMaxWidth()
                                            .padding(horizontal = 60.dp)
                                    ) {
                                        val string = movie.release_date
                                        val date =
                                            LocalDate.parse(string, DateTimeFormatter.ISO_DATE)

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Icon(Icons.Filled.Schedule, contentDescription = "")
                                            Text(
                                                text = date.year.toString(),
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(start = 5.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(20.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Filled.StarRate,
                                                contentDescription = "",
                                                tint = Color.Yellow
                                            )
                                            Text(
                                                text = movie.vote_average.format(1),
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(start = 5.dp),
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {  }, colors = ButtonColors(
                                            containerColor = Color.White,
                                            contentColor = Primary,
                                            disabledContentColor = Color.Black,
                                            disabledContainerColor = Color.Gray,
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 50.dp, vertical = 20.dp)
                                            .heightIn(48.dp)
                                    ) {
                                        Text(text = "Ver Mais")
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