package com.vayo.fitcheq.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.navigation.NavController

@Composable
fun SettingsPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("About Us", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("FAQ", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("LOG OUT", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}
