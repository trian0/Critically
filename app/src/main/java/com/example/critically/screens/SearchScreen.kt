package com.example.critically.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.example.critically.R
import com.example.critically.RetrofitInstance
import com.example.critically.data.SearchViewModel
import com.example.critically.data.repos.BooksRepositoryImpl
import com.example.critically.data.repos.MoviesRepositoryImpl
import com.example.critically.models.Item
import com.example.critically.models.Movies
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.example.critically.ui.theme.Primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {

    val searchViewModel: SearchViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(
                MoviesRepositoryImpl(RetrofitInstance.apiMovie),
                BooksRepositoryImpl(RetrofitInstance.apiBook)
            ) as T
        }
    })

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val debounceJob = remember { mutableStateOf<Job?>(null) }
        val moviesList = searchViewModel.movies.collectAsState().value
        val booksList = searchViewModel.books.collectAsState().value
        val searchText = searchViewModel.searchText.collectAsState().value
        val isSearching = searchViewModel.isSearching.collectAsState().value
        val context = LocalContext.current

        val language = stringResource(R.string.language)

        LaunchedEffect(key1 = searchViewModel.showErrorToastChannel) {
            searchViewModel.showErrorToastChannel.collectLatest { show ->
                if (show) {
                    Toast.makeText(
                        context, context.resources.getText(R.string.error), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        var filterMovies by remember {
            mutableStateOf(true)
        }
        var filterBooks by remember {
            mutableStateOf(false)
        }

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                Column {
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
                                    onValueChange = {
                                        searchViewModel.updateSearchText(it)
                                        debounceJob.value?.cancel()
                                        debounceJob.value =
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(300)
                                                if (filterMovies) {
                                                    searchViewModel.searchMovie(it, language)
                                                } else {
                                                    searchViewModel.searchBook(it)
                                                }
                                            }
                                    },
                                    Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(horizontal = 40.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        cursorColor = Primary,
                                        focusedTextColor = Primary,
                                        unfocusedTextColor = Primary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedPlaceholderColor = Primary,
                                        unfocusedPlaceholderColor = Primary,
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Search,
                                            contentDescription = "",
                                            tint = Primary
                                        )
                                    },
                                    textStyle = TextStyle.Default
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            modifier = Modifier.width(125.dp),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                filterMovies = true
                                filterBooks = false
                                searchViewModel.updateSearchText("")
                            },
                            colors = if (filterMovies) {
                                ButtonColors(
                                    containerColor = Primary,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.Gray,
                                )
                            } else {
                                ButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Primary,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.Gray,
                                )
                            },
                            border = BorderStroke(2.dp, Primary)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (filterMovies) {
                                    Icon(
                                        imageVector = Icons.Outlined.Movie,
                                        contentDescription = "",
                                        tint = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Movie,
                                        contentDescription = "",
                                        tint = Primary
                                    )
                                }

                                Spacer(Modifier.width(5.dp))

                                Text(text = stringResource(id = R.string.search_movies))
                            }
                        }

                        Button(
                            modifier = Modifier.width(125.dp),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                filterMovies = false
                                filterBooks = true
                                searchViewModel.updateSearchText("")
                            },
                            colors = if (filterBooks) {
                                ButtonColors(
                                    containerColor = Primary,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.Gray,
                                )
                            } else {
                                ButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Primary,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.Gray,
                                )
                            },
                            border = BorderStroke(2.dp, Primary),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (filterBooks) {
                                    Icon(
                                        imageVector = Icons.Outlined.Book,
                                        contentDescription = "",
                                        tint = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Book,
                                        contentDescription = "",
                                        tint = Primary
                                    )
                                }

                                Spacer(Modifier.width(5.dp))

                                Text(text = stringResource(id = R.string.search_books))
                            }

                        }
                    }
                }
            },
        ) { innerPadding ->
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if ((moviesList.isEmpty() && filterMovies) || (booksList.isEmpty() && filterBooks)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.empty_list_message),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Image(
                            modifier = Modifier
                                .size(300.dp)
                                .padding(top = 30.dp),
                            painter = painterResource(id = R.drawable.empty_bg),
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                if (filterMovies) {
                    ShowCarouselMovies(moviesList, innerPadding)
                }
                if (filterBooks) {
                    ShowCarouselBooks(booksList, innerPadding)
                }
            }
        }
    }
}

@Composable
fun ShowCarouselMovies(moviesList: List<Movies>, innerPadding: PaddingValues) {
    val pagerState = rememberPagerState(initialPage = 2, pageCount = { moviesList.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                enabled = pagerState.currentPage > 0,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowLeft,
                    contentDescription = "",
                    tint = Primary
                )
            }

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 50.dp),
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                val pageOffset =
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val movie = moviesList[page]

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .graphicsLayer {
                                lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }

                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                )
                            }
                            .aspectRatio(0.7f)
                            .fillMaxSize(),
                        onClick = {
                            PostOfficeAppRouter.navigateTo(Screen.MovieDetailScreen(movie))
                        }
                    ) {
                        val url = "https://image.tmdb.org/t/p/original${movie.poster_path}"
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .crossfade(true)
                                .scale(Scale.FILL)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.placeholder),
                            error = painterResource(id = R.drawable.error_image_generic)
                        )
                    }
                    Text(
                        text = movie.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .graphicsLayer {
                                lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }

                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                )
                            }
                    )
                }
            }

            IconButton(
                enabled = pagerState.currentPage < pagerState.pageCount - 1,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowRight,
                    contentDescription = "",
                    tint = Primary
                )
            }
        }
    }
}

@Composable
fun ShowCarouselBooks(booksList: List<Item>, innerPadding: PaddingValues) {
    val pagerState = rememberPagerState(initialPage = 2, pageCount = { booksList.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                enabled = pagerState.currentPage > 0,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowLeft,
                    contentDescription = "",
                    tint = Primary
                )
            }

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 80.dp),
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) { page ->
                val book = booksList[page].volumeInfo
                val imageLinks = book.imageLinks
                val pageOffset =
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .graphicsLayer {
                                lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }

                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                )
                            }
                            .fillMaxWidth(1f)
                            .aspectRatio(0.7f)
                    ) {
                        val url: StringBuilder = StringBuilder(imageLinks.thumbnail)
                        url.insert(4, "s")
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url.toString())
                                .crossfade(true)
                                .scale(Scale.FILL)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.placeholder),
                            error = painterResource(id = R.drawable.error_image_generic)
                        )
                    }
                    Text(
                        text = book.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .graphicsLayer {
                                lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }

                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - abs(pageOffset.coerceIn(-1f, 1f))
                                )
                            }
                    )
                }
            }

            IconButton(
                enabled = pagerState.currentPage < pagerState.pageCount - 1,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowRight,
                    contentDescription = "",
                    tint = Primary
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}