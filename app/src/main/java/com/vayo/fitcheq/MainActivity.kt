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
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.material.snackbar.Snackbar
import com.vayo.fitcheq.ui.theme.FitCheqTheme
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel

class MainActivity : ComponentActivity() {
    // --- Added for in-app updates ---
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val UPDATE_REQUEST_CODE = 1001

    private val updateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showUpdateSnackbar()
        }
    }
    // --------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkForAppUpdate() // ✅ Check update early, before Compose loads

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
    // --- Added functions for in-app updates ---
    private fun checkForAppUpdate() {
        appUpdateManager.registerListener(updateListener)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    UPDATE_REQUEST_CODE
                )
            }
        }
    }

    private fun showUpdateSnackbar() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "New update downloaded",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Restart") {
            appUpdateManager.completeUpdate()
        }.show()
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateSnackbar()
            }
        }
    }

    override fun onDestroy() {
        appUpdateManager.unregisterListener(updateListener)
        super.onDestroy()
    }
    // -------------------------------------------
}
