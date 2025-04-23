package com.vayo.fitcheq.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.data.model.femaleoccasionList
import com.vayo.fitcheq.data.model.maleoccasionList
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.navigation.BottomNavigation
import com.vayo.fitcheq.navigation.ScreenContainer


@Composable
fun FemaleHomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    val firestore = remember { FirebaseFirestore.getInstance() }
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userName by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val isLoggedIn by authViewModel.authState.collectAsStateWithLifecycle()

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
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with App Title
            Text(
                text = "Fit Cheq",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Row for Profile and Weather Widget Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /* TODO: Handle Weather Click */ },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Weather Widget")
                }

                Button(
                    onClick = { navController.navigate("my_profile") },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Female Profile")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .clickable { /* TODO: Handle Search Click */ },
                contentAlignment = Alignment.Center
            ) {
                Text("Search for outfits...", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            /// Occasion Fits Section
            Text(
                text = "Fits According to Occasion",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(femaleoccasionList) { occasion ->
                    val onCardClick = {
                        when (occasion.title) {
                            "College" -> {
                                navController.navigate(AuthScreen.OutfitDetails.createRoute("female", "college"))
                                {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }

                    }
                    Card(
                        modifier = Modifier
                            .width(100.dp)
                            .height(120.dp)
                            .clickable(onClick = onCardClick),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = occasion.emoji,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = occasion.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fashion Style Section
            Text(
                text = "Fits According to Fashion Style",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Old Money • Starboy • Streetwear • Casual")

            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder for More Sections if Needed
            Text(
                text = "More sections coming soon...",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun PreviewFemaleHomeScreen() {
//    MaleHomeScreen()
}