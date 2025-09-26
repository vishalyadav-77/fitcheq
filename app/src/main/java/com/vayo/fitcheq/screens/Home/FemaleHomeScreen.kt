package com.vayo.fitcheq.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.R
import com.vayo.fitcheq.data.model.femalecategoryList
import com.vayo.fitcheq.data.model.femaleoccasionList
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.navigation.ScreenContainer
import com.vayo.fitcheq.ui.theme.modernShimmer
import kotlin.collections.chunked
import kotlin.collections.forEach
import kotlin.text.isNotEmpty

@Composable
fun FemaleHomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    val isLoggedIn by authViewModel.authState.collectAsStateWithLifecycle()
    val userId by authViewModel.currentUserId.collectAsState()
    val myTitleFont = FontFamily(
        Font(R.font.title_syarifa)
    )
    val myHeadingFont = FontFamily(
        Font(R.font.headings_kugile)
    )
    val imageRouteMap = mapOf(
        "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/female/grungestyle.webp" to "grunge",
        "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/female/barbie.webp" to "barbie"
    )

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login") {
                popUpTo("female_home") { inclusive = true }
            }
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
                Spacer(modifier = Modifier.height(6.dp))
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
                                        "female",
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

                //  BANNER
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(180.dp)
                        .clickable {
                            navController.navigate(
                                AuthScreen.OutfitDetails.createRoute(
                                    "female",
                                    "style",
                                    "baddie"
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
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var isBannerLoading by remember { mutableStateOf(true) }
                        AsyncImage(
                            model = "https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/banners/female/baddie1.webp",
                            contentDescription = "baddie Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onLoading = { isBannerLoading = true },
                            onSuccess = { isBannerLoading = false },
                            onError = { isBannerLoading = false }
                        )
                        if (isBannerLoading) {
                            // shimmer overlays until image is ready
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
                    val chunkedCategories = femalecategoryList.chunked(2) // two rows per column

                    chunkedCategories.forEach { itemsInColumn ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.width(100.dp) // each column fixed width
                        ) {
                            itemsInColumn.forEach { category ->
                                val onCardClick = {
                                    val route = when (category.title) {
                                        "Tops" -> "top"
                                        "Dresses" -> "dress"
                                        "Jeans" -> "jeans"
                                        "Trousers" -> "trousers"
                                        "Kurti" -> "kurti"
                                        "Saree" -> "saree"
                                        "Skirts" -> "skirts"
                                        "Accessories" -> "accessories"
                                        "TankTops" -> "tanktops"
                                        "Jacket" -> "jacket"
                                        else -> ""
                                    }
                                    if (route.isNotEmpty()) {
                                        navController.navigate(
                                            AuthScreen.OutfitDetails.createRoute(
                                                "female",
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
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        var isLoading by remember { mutableStateOf(true) }
                                        AsyncImage(
                                            model = category.imageUrl,
                                            contentDescription = category.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            onLoading = { isLoading = true },
                                            onSuccess = { isLoading = false },
                                            onError = { isLoading = false }
                                        )
                                        if (isLoading) {
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
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
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
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
                ) {
                    items(femaleoccasionList, key = { it.title }) { occasion ->
                        val onCardClick = {
                            val route = when (occasion.title) {
                                "College" -> "college"
                                "Date" -> "date"
                                "Office" -> "office"
                                "Gym" -> "gym"
                                else -> ""
                            }
                            if (route.isNotEmpty()) {
                                navController.navigate(
                                    AuthScreen.OutfitDetails.createRoute(
                                        "female",
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
                                .height(200.dp)
                                .clickable(onClick = onCardClick),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                var isLoading by remember { mutableStateOf(true) }
                                AsyncImage(
                                    model = occasion.imageUrl,
                                    contentDescription = occasion.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    onLoading = { isLoading = true },
                                    onSuccess = { isLoading = false },
                                    onError = { isLoading = false }
                                )

                                // Only show text when image is loaded
                                if (isLoading) {
                                    // shimmer overlays until image is ready
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
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}