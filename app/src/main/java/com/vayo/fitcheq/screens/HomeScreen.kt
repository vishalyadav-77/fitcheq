package com.vayo.fitcheq.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vayo.fitcheq.MainActivity
import com.vayo.fitcheq.AuthViewModel

@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel= viewModel()) {
    val isLoggedIn by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            kotlinx.coroutines.delay(100) // ✅ Small delay to stabilize state
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Fit Cheq")
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            authViewModel.logout()
        }) {
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "isLoggedIn: $isLoggedIn")

    }
}