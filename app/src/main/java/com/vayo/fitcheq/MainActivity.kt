package com.vayo.fitcheq


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.vayo.fitcheq.ui.theme.FitCheqTheme
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitCheqTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val maleViewModel: MaleHomeViewModel = viewModel()

                // Initialize SharedPreferences
                authViewModel.initializeSharedPreferences(applicationContext)

                val isLoggedIn by authViewModel.authState.collectAsStateWithLifecycle()
                val isProfileCompleted by authViewModel.isProfileCompleted.collectAsStateWithLifecycle()
                val userGender by authViewModel.userGender.collectAsStateWithLifecycle()
                val isCheckingProfile by authViewModel.isCheckingProfile.collectAsStateWithLifecycle()

                // Show loading indicator or navigation based on profile check
                if (isCheckingProfile) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Base navigation with shared ViewModel
                    AuthNavGraph(navController = navController, authViewModel = authViewModel, maleViewModel = maleViewModel)

                    // Navigation logic
                    LaunchedEffect(isLoggedIn, isProfileCompleted, userGender) {
                        
                        when {
                            !isLoggedIn -> {
                                navController.navigate(AuthScreen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            isLoggedIn && isProfileCompleted == false -> {
                                navController.navigate(AuthScreen.UserProfile.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            isLoggedIn && isProfileCompleted == true && userGender != null -> {
                                val route = when (userGender) {
                                    "Male" -> AuthScreen.MaleHome.route
                                    "Female" -> AuthScreen.FemaleHome.route
                                    else -> null
                                }
                                route?.let {
                                    navController.navigate(it) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
