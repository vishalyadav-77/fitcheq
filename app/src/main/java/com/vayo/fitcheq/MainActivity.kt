package com.vayo.fitcheq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.vayo.fitcheq.screens.HomeScreen
import com.vayo.fitcheq.ui.theme.FitCheqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitCheqTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                val isLoggedIn by authViewModel.authState.collectAsStateWithLifecycle()

                // ✅ Prevent crash on restart
                LaunchedEffect(isLoggedIn) {
                    if (!isLoggedIn) {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }

                if (isLoggedIn) {
                    HomeScreen(navController,authViewModel) // Show HomeScreen if user is logged in
                } else {
                    AuthNavGraph(navController = navController) // Show Auth Screens
                }
            }
        }
    }
}
