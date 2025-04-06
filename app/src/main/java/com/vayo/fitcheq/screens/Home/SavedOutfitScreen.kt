package com.vayo.fitcheq.screens.Home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vayo.fitcheq.AuthViewModel
import com.vayo.fitcheq.navigation.ScreenContainer

@Composable
fun SavedOutfitScreen(navController: NavController, authViewModel: AuthViewModel) {
    ScreenContainer(navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Saved Outfits",
                fontSize = 24.sp
            )
            
            // Add your saved outfits content here
        }
    }
}

