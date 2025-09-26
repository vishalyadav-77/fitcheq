package com.vayo.fitcheq.screens.Home

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vayo.fitcheq.viewmodels.AuthViewModel
import androidx.compose.ui.platform.LocalContext
import com.vayo.fitcheq.data.model.maleoccasionList
import kotlinx.coroutines.tasks.await
import com.vayo.fitcheq.navigation.ScreenContainer
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.data.model.malecategoryList
import com.vayo.fitcheq.data.model.malefashionList
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.Dp
import com.vayo.fitcheq.R
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vayo.fitcheq.data.model.maleSeasonList
import com.vayo.fitcheq.ui.theme.modernShimmer
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun MaleHomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    val firestore = remember { FirebaseFirestore.getInstance() }
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userName by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val isLoggedIn by authViewModel.authState.collectAsStateWithLifecycle()
    val homeViewModel: MaleHomeViewModel = viewModel()
    val userId by authViewModel.currentUserId.collectAsState()
    val myTitleFont = FontFamily(
        Font(R.font.title_syarifa)
    )
    val myHeadingFont = FontFamily(
        Font(R.font.headings_kugile)
    )
    val activity = context as ComponentActivity

    val imageRouteMap = mapOf(
        "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/oldmoney_new.webp" to "oldmoney",
        "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/streetwear.webp" to "streetwear",
        "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/starboy.webp" to "starboy"
    )
    // Sets system bar colors + icons
    SideEffect {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.White.toArgb(),
                darkScrim = Color.White.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.White.toArgb(),
                darkScrim = Color.Black.toArgb()
            )
        )
    }
    // Observe the userId changes to load or clear favorites
    LaunchedEffect(userId) {
        userId?.let {
            homeViewModel.observeUser(authViewModel.currentUserId)
        } ?: run {
            homeViewModel.clearFavorites()
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login") {
                popUpTo("male_home") { inclusive = true }
            }
        }
    }

    LaunchedEffect(authViewModel.toastMessage) {
        authViewModel.toastMessage.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                authViewModel.clearToastMessage()
            }
        }
    }

    // Fetch user data when screen loads
    LaunchedEffect(Unit) {
        try {
            // 1. Check authentication status
            val uid = currentUser?.uid ?: run {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
//                navController.navigate("login") { popUpTo(0) }
                return@LaunchedEffect
            }

            // 2. Fetch user data with coroutine
            val document = firestore.collection("users")
                .document(uid)
                .get()
                .await() // Using await() instead of callbacks

            // 3. Handle document data
            if (document.exists()) {
                userName = document.getString("name") ?: "Name not set"
            } else {
                Toast.makeText(context, "Profile data missing", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // 4. Handle errors
            Log.e("Exception babu", "Error loading data: ${e.message}", e)
            userName = "Error loading name"
        } finally {
            // 5. Update loading state
            isLoading = false
        }
    }

    ScreenContainer(navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // APP TITLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fit Cheq",
                    fontFamily = myTitleFont,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // CAROUSEL TOP
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.5f / 4f),
                    shape = RoundedCornerShape(0.dp),
                ) {
                    HomeImageCarousel(
                        images = imageRouteMap.keys.toList(),
                        onImageClick = { imageUrl ->
                            val route = imageRouteMap[imageUrl] ?: ""
                            if (route.isNotEmpty()) {
                                navController.navigate(
                                    AuthScreen.OutfitDetails.createRoute(
                                        "male",
                                        "style",
                                        route
                                    )
                                ) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))

                //  ESSENTIALS CATEGORY
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .aspectRatio(2.4f)
                        .clickable {
                            navController.navigate(
                                AuthScreen.OutfitDetails.createRoute(
                                    "male",
                                    "style",
                                    "essentials"
                                )
                            ) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    shape = RoundedCornerShape(0.dp),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var isBannerLoading by remember { mutableStateOf(true) }
                        AsyncImage(
                            model = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/banners/ess_banner_beta.webp",
                            contentDescription = "ESS Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onLoading = { isBannerLoading = true },
                            onSuccess = { isBannerLoading = false },
                            onError = { isBannerLoading = false }
                        )
                        if (isBannerLoading) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .modernShimmer(
                                        isLoading = true,
                                        cornerRadius = 0.dp
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))

                // Seasonal Fits Section
                Text(
                    text = "Fits By Season",
                    fontFamily = myHeadingFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        maleSeasonList.forEach { season ->
                            val onCardClick = {
                                val route = when (season.title) {
                                    "Summer" -> "summer"
                                    "Winter" -> "winter"
                                    else -> ""
                                }
                                if (route.isNotEmpty()) {
                                    navController.navigate(
                                        AuthScreen.OutfitDetails.createRoute(
                                            "male",
                                            "season",
                                            route
                                        )
                                    ) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(3f / 4f)
                                    .clickable(onClick = onCardClick),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    var isImageLoading by remember { mutableStateOf(true) }
                                    AsyncImage(
                                        model = season.imageUrl,
                                        contentDescription = season.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        onLoading = { isImageLoading = true },
                                        onSuccess = { isImageLoading = false },
                                        onError = { isImageLoading = false }
                                    )
                                    if (isImageLoading) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .modernShimmer(
                                                    isLoading = true,
                                                    cornerRadius = 0.dp
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))

                // SHOP BY CATEGORY
                Text(
                    text = "Shop By Category",
                    fontFamily = myHeadingFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Horizontal scrollable 2-row layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val chunkedCategories = malecategoryList.chunked(2)
                    chunkedCategories.forEach { itemsInColumn ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.width(100.dp)
                        ) {
                            itemsInColumn.forEach { category ->
                                val onCardClick = {
                                    val route = when (category.title) {
                                        "Tshirt" -> "tshirt"
                                        "Shirt" -> "shirt"
                                        "Jeans" -> "jeans"
                                        "TrackPants" -> "trackpants"
                                        "Jacket" -> "jacket"
                                        "TankTops" -> "tanktops"
                                        "Accessories" -> "accessories"
                                        else -> ""
                                    }
                                    if (route.isNotEmpty()) {
                                        navController.navigate(
                                            AuthScreen.OutfitDetails.createRoute(
                                                "male",
                                                "category",
                                                route
                                            )
                                        ) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .height(130.dp)
                                        .fillMaxWidth()
                                        .clickable(onClick = onCardClick),
                                    shape = RoundedCornerShape(0.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        var isImageLoading by remember { mutableStateOf(true) }
                                        AsyncImage(
                                            model = category.imageUrl,
                                            contentDescription = category.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            onLoading = { isImageLoading = true },
                                            onSuccess = { isImageLoading = false },
                                            onError = { isImageLoading = false }
                                        )
                                        if (isImageLoading) {
                                            Box(
                                                modifier = Modifier
                                                    .matchParentSize()
                                                    .modernShimmer(
                                                        isLoading = true,
                                                        cornerRadius = 0.dp
                                                    )
                                            )
                                        } else {
                                            Text(
                                                text = category.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(8.dp)
                                                    .background(
                                                        color = Color.Black.copy(alpha = 0.6f),
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(
                                                        horizontal = 6.dp,
                                                        vertical = 2.dp
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // FITS BY OCCASION
                Text(
                    text = "Fits By Occasion",
                    fontFamily = myHeadingFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    maleoccasionList.forEach { occasion ->
                        val onCardClick = {
                            val route = when (occasion.title) {
                                "College" -> "college"
                                "Date" -> "date"
                                "Beach" -> "beach"
                                "Wedding" -> "wedding"
                                "Office" -> "office"
                                "Gym" -> "gym"
                                else -> ""
                            }
                            if (route.isNotEmpty()) {
                                navController.navigate(
                                    AuthScreen.OutfitDetails.createRoute(
                                        "male",
                                        "occasion",
                                        route
                                    )
                                ) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .aspectRatio(130f / 150f) // maintains the original height proportion
                                .clickable(onClick = onCardClick),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                var isImageLoading by remember { mutableStateOf(true) }
                                AsyncImage(
                                    model = occasion.imageUrl,
                                    contentDescription = occasion.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    onLoading = { isImageLoading = true },
                                    onSuccess = { isImageLoading = false },
                                    onError = { isImageLoading = false }
                                )
                                if (isImageLoading) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .modernShimmer(
                                                isLoading = true,
                                                cornerRadius = 0.dp
                                            )
                                    )
                                }
                                else {
                                    Text(
                                        text = occasion.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                            .background(
                                                color = Color.Black.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // FITS BY FASHION
                Text(
                    text = "Fits By Fashion",
                    fontFamily = myHeadingFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    malefashionList.forEach { fashion ->
                        val onCardClick = {
                            val route = when (fashion.title) {
                                "Starboy" -> "starboy"
                                "Soft Boy" -> "softboy"
                                "Y2K" -> "y2k"
                                "Old Money" -> "oldmoney"
                                "Streetwear" -> "streetwear"
                                "Minimalist" -> "minimalist"
                                else -> ""
                            }
                            if (route.isNotEmpty()) {
                                navController.navigate(
                                    AuthScreen.OutfitDetails.createRoute(
                                        "male",
                                        "style",
                                        route
                                    )
                                ) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .width(180.dp)
                                .height(210.dp)
                                .clickable(onClick = onCardClick),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                                var isImageLoading by remember { mutableStateOf(true) }
                                AsyncImage(
                                    model = fashion.imageUrl,
                                    contentDescription = fashion.title,
                                    modifier = Modifier.fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop,
                                    onLoading = { isImageLoading = true },
                                    onSuccess = { isImageLoading = false },
                                    onError = { isImageLoading = false }
                                )
                                if (isImageLoading) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .modernShimmer(
                                                isLoading = true,
                                                cornerRadius = 0.dp
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier,
    autoScrollDelay: Long = 3000L,
    repeatCount: Int = 10,
    onImageClick: (String) -> Unit = {}
) {
    if (images.isEmpty()) return

    // Repeat images 10 times for "infinite" feel
    val block = images
    val blockSize = block.size
    val loopedImages = remember(images) { List(repeatCount) { images }.flatten() }

    // Start from the middle
    val startIndex = (loopedImages.size / 2).floorDiv(images.size) * images.size
    val pagerState = rememberPagerState(initialPage = startIndex)

    // Auto-scroll
    LaunchedEffect(pagerState) {
        while (isActive) {
            delay(autoScrollDelay)
            if (!pagerState.isScrollInProgress) {
                val nextPage = pagerState.currentPage + 1
                pagerState.animateScrollToPage(nextPage)
            }

            // Reset back to middle if near edges
            if (pagerState.currentPage >= loopedImages.size - images.size ||
                pagerState.currentPage <= images.size
            ) {
                val middleIndex = (loopedImages.size / 2).floorDiv(images.size) * images.size
                pagerState.scrollToPage(middleIndex) // instant jump
            }
        }
    }
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Image pager
        HorizontalPager(
            count = loopedImages.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { page ->
            val imageUrl = loopedImages[page]
            AsyncImage(
                model = imageUrl,
                contentDescription = "Carousel Image $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
                    .clickable{
                        onImageClick(imageUrl)
                    }
            )
        }

        // Correct, remapped indicator
        val realPage = (pagerState.currentPage % blockSize + blockSize) % blockSize
        PagerDots(
            current = realPage,
            total = blockSize,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
        )
    }
}

@Composable
private fun PagerDots(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.5f),
    size: Dp = 6.dp,
    activeSize: Dp = 6.dp,
    spacing: Dp = 4.dp
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(spacing)) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == current) activeSize else size)
                    .background(
                        color = if (i == current) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
        }
    }
}
