package com.example.critically.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.critically.R
import com.example.critically.RetrofitInstance
import com.example.critically.data.MovieDetailViewModel
import com.example.critically.data.repos.MoviesRepositoryImpl
import com.example.critically.models.Movies
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.example.critically.ui.theme.BgCardColor
import com.example.critically.ui.theme.Primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(modifier: Modifier = Modifier, movie: Movies) {
    val movieDetailViewModel: MovieDetailViewModel =
        viewModel(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieDetailViewModel(
                    MoviesRepositoryImpl(RetrofitInstance.apiMovie)
                ) as T
            }
        })

    val images = movieDetailViewModel.images.collectAsState().value
    val cast = movieDetailViewModel.cast.collectAsState().value
    val duration = movieDetailViewModel.duration.collectAsState().value
    val isSearching = movieDetailViewModel.isSearching.collectAsState().value
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            movieDetailViewModel.getMovieInfo(movie.id)
        }
    }

    LaunchedEffect(key1 = movieDetailViewModel.showErrorToastChannel) {
        movieDetailViewModel.showErrorToastChannel.collectLatest { show ->
            if (show) {
                Toast.makeText(
                    context, context.resources.getText(R.string.error), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    BackHandler {
        PostOfficeAppRouter.navigateTo(Screen.Searchscreen)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .zIndex(1f)
                .border(2.dp, Color.White, CircleShape),
            onClick = { PostOfficeAppRouter.navigateTo(Screen.Searchscreen) }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "",
                tint = Color.White
            )
        }

        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .background(Color.White), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            val url =
                if (images.isNotEmpty() && images.size >= 5) "https://image.tmdb.org/t/p/original${images[5].file_path}"
                else "https://image.tmdb.org/t/p/original${movie.poster_path}"
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f),
                    contentScale = ContentScale.Crop,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = "",
                    placeholder = painterResource(id = R.drawable.placeholder),
                    error = painterResource(id = R.drawable.error_image_generic)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(bottom = 30.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-20).dp)
                            .padding(start = 5.dp, end = 5.dp, bottom = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 60.dp)
                                .width(200.dp),
                            text = movie.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .offset(y = (-20).dp)
                            .zIndex(1f)
                            .border(2.dp, Color.White, CircleShape),
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BgCardColor)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White, CircleShape)
                                        .border(
                                            BorderStroke(2.dp, Primary),
                                            CircleShape
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.AccessTime,
                                        contentDescription = "",
                                        tint = Primary,
                                        modifier = Modifier.background(Color.White, CircleShape)
                                    )
                                }
                                Spacer(Modifier.width(5.dp))
                                Column {
                                    Text(
                                        text = stringResource(
                                            R.string.duration_text,
                                            duration.toString()
                                        ),
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = stringResource(R.string.duration),
                                        color = Color.Gray,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White, CircleShape)
                                        .border(
                                            BorderStroke(2.dp, Primary),
                                            CircleShape
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Star,
                                        contentDescription = "",
                                        tint = Primary,
                                        modifier = Modifier.background(Color.White, CircleShape)
                                    )
                                }
                                Spacer(Modifier.width(5.dp))
                                Column {
                                    Text(
                                        text = String.format("%.1f", movie.vote_average),
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = stringResource(R.string.rating),
                                        color = Color.Gray,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White, CircleShape)
                                        .border(
                                            BorderStroke(2.dp, Primary),
                                            CircleShape
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.RocketLaunch,
                                        contentDescription = "",
                                        tint = Primary,
                                        modifier = Modifier.background(Color.White, CircleShape)
                                    )
                                }
                                Spacer(Modifier.width(5.dp))
                                Column {
                                    Text(
                                        text = movie.release_date.split("-")[0],
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = stringResource(R.string.release),
                                        color = Color.Gray,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = stringResource(R.string.about),
                            color = Primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Justify
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = movie.overview,
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Justify
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 20.dp, bottom = 10.dp, end = 20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.cast),
                            color = Primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Justify
                        )
                        Spacer(Modifier.height(15.dp))
                        LazyRow {
                            items(cast) { cast ->
                                val imageUrl =
                                    "https://image.tmdb.org/t/p/original${cast.profile_path}"
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = "",
                                        placeholder = painterResource(id = R.drawable.placeholder),
                                        error = painterResource(id = R.drawable.error_image_generic)
                                    )

                                    val formattedName = cast.name.replace(" ", "\n")

                                    Text(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        text = formattedName,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(80.dp)
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                .padding(horizontal = 10.dp)
                .zIndex(1f)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)) {
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(end = 4.dp)
                        .heightIn(48.dp),
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(Primary),
                    enabled = true
                ) {
                    Box(
                        modifier = Modifier
                            .heightIn(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.criticize),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.white)
                        )
                    }
                }

                IconButton(
                    modifier = Modifier
                        .weight(0.1f)
                        .border(2.dp, Primary, CircleShape),
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "",
                        tint = Primary
                    )
                }
            }
        }
    }
}