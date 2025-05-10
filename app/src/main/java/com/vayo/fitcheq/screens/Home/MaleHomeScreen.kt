package com.vayo.fitcheq.screens.Home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.foundation.lazy.items
import androidx.navigation.compose.rememberNavController
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.data.model.malefashionList
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel


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


    // Observe the userId changes to load or clear favorites
    LaunchedEffect(userId) {
        userId?.let {
            homeViewModel.observeUser(authViewModel.currentUserId)  // Load favorites when user is logged in
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
                .await() // Use await() instead of callbacks

            // 3. Handle document data
            if (document.exists()) {
                userName = document.getString("name") ?: "Name not set"
            } else {
                Toast.makeText(context, "Profile data missing", Toast.LENGTH_SHORT).show()
//                navController.navigate("profile") // Redirect to profile creation
            }
        } catch (e: Exception) {
            // 4. Handle errors
            Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            userName = "Error loading name"
        } finally {
            // 5. Update loading state
            isLoading = false
        }
    }

    ScreenContainer(navController = navController) { paddingValues ->
//    ScreenContainer(navController = navController) { paddingValues ->
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

            // Welcome Message
            Text(
                text = "Welcome, $userName! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
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
                    Text("Male Profile")
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

            // Occasion Fits Section
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
                items(maleoccasionList) { occasion ->
                    val onCardClick = {
                        when (occasion.title) {
                            "College" -> {
                                navController.navigate(AuthScreen.OutfitDetails.createRoute("male", "college"))
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

            // Seasonal Fits Section
            Text(
                text = "Fits According to Season",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Summer • Winter • Monsoon • Spring")

            Spacer(modifier = Modifier.height(24.dp))

            // Fashion Style Section
            Text(
                text = "Fits According to Fashion Style",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(malefashionList) { occasion ->
                    val onCardClick = {
                        when (occasion.title) {
                            "Starboy" -> {
                                navController.navigate(AuthScreen.OutfitDetails.createRoute("male", "starboy"))
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

            // Logout Button
            Button(
                onClick = {
                    authViewModel.logout()
                          },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}