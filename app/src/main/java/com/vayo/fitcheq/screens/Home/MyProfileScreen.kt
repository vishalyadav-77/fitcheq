package com.vayo.fitcheq.screens.Home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.vayo.fitcheq.data.model.UserProfile
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.navigation.ScreenContainer
import kotlinx.coroutines.tasks.await

@Composable
fun MyProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }

    val firestore = remember { FirebaseFirestore.getInstance() }
    var userName by remember { mutableStateOf("Loading...") }
    LaunchedEffect(Unit) {
       userProfile = authViewModel.loadProfileFromSharedPreferences()
    }
    ScreenContainer(navController = navController) { paddingValues ->
        // Title section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp,
                )
        ) {
            Text(
                text = "Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            userProfile?.let { profile ->
                ProfileRow(label = "Name", value = profile.name)
                ProfileRow(label = "Gender", value = profile.gender)
                ProfileRow(label = "Occupation", value = profile.occupation)
                ProfileRow(label = "Age Group", value = profile.ageGroup.displayName)
                ProfileRow(label = "Preferred Platform", value = profile.preferPlatform.displayName)
                ProfileRow(label = "Height Group", value = profile.height.displayName)
                ProfileRow(label = "Body Type", value = profile.bodyType.displayName)
            } ?: run {
                Text("Loading...", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
@Composable
fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp,horizontal = 12.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = value, fontSize = 15.sp, color = Color.DarkGray)
    }
}

