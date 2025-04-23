package com.vayo.fitcheq


import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("NavigationDebug", "MainActivity onCreate called")
        enableEdgeToEdge()
        setContent {
            FitCheqTheme {
                Log.d("NavigationDebug", "MainActivity setContent called")
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val maleViewModel: MaleHomeViewModel = viewModel()

                // Initialize SharedPreferences
                authViewModel.initializeSharedPreferences(applicationContext)

                val isLoggedIn by authViewModel.authState.collectAsStateWithLifecycle()
                val isProfileCompleted by authViewModel.isProfileCompleted.collectAsStateWithLifecycle()
                val userGender by authViewModel.userGender.collectAsStateWithLifecycle()
                val isCheckingProfile by authViewModel.isCheckingProfile.collectAsStateWithLifecycle()

                // Log current states
                LaunchedEffect(isLoggedIn, isProfileCompleted, userGender, isCheckingProfile) {
                    Log.d("NavigationDebug", """
                        MainActivity State Update:
                        isLoggedIn: $isLoggedIn
                        isProfileCompleted: $isProfileCompleted
                        userGender: $userGender
                        isCheckingProfile: $isCheckingProfile
                    """.trimIndent())
                }

                // Collect state changes directly
                LaunchedEffect(Unit) {
                    authViewModel.authState.collectLatest { loggedIn ->
                        Log.d("NavigationDebug", "MainActivity: Auth state changed to: $loggedIn")
                    }
                }

                LaunchedEffect(Unit) {
                    authViewModel.isProfileCompleted.collectLatest { completed ->
                        Log.d("NavigationDebug", "MainActivity: Profile completion state changed to: $completed")
                    }
                }

                LaunchedEffect(Unit) {
                    authViewModel.userGender.collectLatest { gender ->
                        Log.d("NavigationDebug", "MainActivity: User gender changed to: $gender")
                    }
                }

                LaunchedEffect(Unit) {
                    authViewModel.isCheckingProfile.collectLatest { checking ->
                        Log.d("NavigationDebug", "MainActivity: Profile checking state changed to: $checking")
                    }
                }

                // Show loading indicator or navigation based on profile check
                if (isCheckingProfile) {
                    Log.d("NavigationDebug", "MainActivity: Showing loading indicator")
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
                        Log.d("NavigationDebug", "MainActivity: Navigation check triggered")
                        
                        when {
                            !isLoggedIn -> {
                                Log.d("NavigationDebug", "MainActivity: Navigating to Login screen")
                                navController.navigate(AuthScreen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            isLoggedIn && isProfileCompleted == false -> {
                                Log.d("NavigationDebug", "MainActivity: Navigating to Profile screen")
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
                                    Log.d("NavigationDebug", "MainActivity: Navigating to $it")
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
