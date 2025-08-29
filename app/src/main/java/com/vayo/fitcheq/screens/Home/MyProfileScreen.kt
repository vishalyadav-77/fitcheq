package com.vayo.fitcheq.screens.Home

import android.graphics.Paint
import android.graphics.fonts.FontStyle
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.data.model.UserProfile
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.navigation.ScreenContainer
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun MyProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    val firestore = remember { FirebaseFirestore.getInstance() }
    var userName by remember { mutableStateOf("Loading...") }
    val context = LocalContext.current
    

    LaunchedEffect(Unit) {
       userProfile = authViewModel.loadUserProfileFromSharedPreferences(context)
    }

    val username = userProfile?.name ?: "..."

    ScreenContainer(navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = { navController.navigate(AuthScreen.SettingsPage.route) },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 26.dp),
                color = Color.LightGray,
                thickness = 0.5.dp
            )

            // CONTENT
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //USER pfp and Name
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(78.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .padding(8.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = username,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 36.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "EDIT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.Underline
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    Column {
                        userProfile?.let { profile ->
                            ProfileRow(label = "Gender", value = profile.gender)
                            ProfileRow(label = "Height Group", value = profile.height.displayName)
                            ProfileRow(label = "Age Group", value = profile.ageGroup.displayName)
                            ProfileRow(label = "Occupation", value = profile.occupation)
                            ProfileRow(label = "Body Type", value = profile.bodyType.displayName)
                            ProfileRow(
                                label = "Preferred Platform",
                                value = profile.preferPlatform.displayName
                            )
                        } ?: run {
                            Text("Loading...")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Logout Button
                Button(
                    onClick = {
                        authViewModel.logout()
                    },
                    modifier = Modifier.width(250.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("LOGOUT",)
                }

            }

        }
    }
}
@Composable
fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp,horizontal = 12.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = value, fontSize = 14.sp, color = Color.DarkGray)
    }
}

