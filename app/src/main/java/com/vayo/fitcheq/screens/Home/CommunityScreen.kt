package com.vayo.fitcheq.screens.Home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.R
import com.vayo.fitcheq.data.model.FitGuide
import com.vayo.fitcheq.data.model.FitGuideCategory
import com.vayo.fitcheq.data.model.FitGuideWrapper
import com.vayo.fitcheq.data.model.UserProfile
import com.vayo.fitcheq.data.model.maleoccasionList
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.navigation.ScreenContainer
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel
import kotlinx.serialization.json.Json

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunityScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var fitGuide by remember { mutableStateOf<FitGuide?>(null) }
    var selectedCategory by remember { mutableStateOf<FitGuideCategory?>(null) }

    LaunchedEffect(Unit) { userProfile = authViewModel.loadUserProfileFromSharedPreferences(context) }

    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            val wrapper = loadFitGuide(context)
            fitGuide = wrapper.guides.find {
                it.gender.equals(userProfile!!.gender, true) &&
                        it.height == userProfile!!.height &&
                        it.bodyType == userProfile!!.bodyType
            }
        }
    }

    val gender = userProfile?.gender
    val height = userProfile?.height
    val bodyType = userProfile?.bodyType


    ScreenContainer(navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //TITLE
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Fit Guide",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
            }
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "YOUR PERSONAL FIT GUIDE",
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "GENERAL GUIDE",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            // Content
            when {
                fitGuide == null -> {
                    Text("Loading your fit guide…")
                }

                fitGuide!!.categories.isEmpty() -> {
                    Text("No categories available for your profile")
                }

                else -> {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fitGuide!!.chips.forEach { chip ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colorResource(id = R.color.light_grey),
                                tonalElevation = 2.dp,
                                shadowElevation = 2.dp
                            ) {
                                Text(
                                    text = chip,
                                    modifier = Modifier.padding(
                                        vertical = 8.dp,
                                        horizontal = 12.dp
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
                    ) {
                        items(fitGuide!!.categories, key = { it.id }) { category ->
                            val onCardClick = {
                                selectedCategory = category
                            }
                            Card(
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(150.dp)
                                    .clickable(onClick = onCardClick),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // If it's a URL use AsyncImage, else fallback to drawable
                                    if (category.image.startsWith("http")) {
                                        AsyncImage(
                                            model = category.image,
                                            contentDescription = category.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        // do nothing for now
                                    }

                                    Text(
                                        text = category.title,
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
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    // Show dialog only if a category is selected
                    selectedCategory?.let { category ->
                        Dialog(onDismissRequest = { selectedCategory = null }) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.White)
                                    .padding(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        category.title,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(Modifier.height(8.dp))

                                    Text(
                                        category.summary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.height(12.dp))

                                    if (category.image.startsWith("http")) {
                                        AsyncImage(
                                            model = category.image,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxWidth(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Text(
                                        "Details:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    category.details.forEach { detail ->
                                        Text("• $detail", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Spacer(Modifier.height(16.dp))

                                    Button(
                                        onClick = { selectedCategory = null },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Close")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }
        }
    }
}
fun loadFitGuide(context: Context): FitGuideWrapper {
    val jsonString = context.assets.open("fit_guide.json")
        .bufferedReader().use { it.readText() }
    return Json.decodeFromString(jsonString)
}


