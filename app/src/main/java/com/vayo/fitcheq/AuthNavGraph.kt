package com.vayo.fitcheq

//This will define the navigation routes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vayo.fitcheq.screens.LoginScreen
import com.vayo.fitcheq.screens.SignUpScreen
import com.vayo.fitcheq.AuthViewModel
import com.vayo.fitcheq.screens.Home.FemaleHomeScreen
import com.vayo.fitcheq.screens.Home.MaleHomeScreen
import com.vayo.fitcheq.screens.HomeScreen
import com.vayo.fitcheq.screens.ProfileScreen

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object SignUp : AuthScreen("signup")
    object Home : AuthScreen("home")
    object UserProfile : AuthScreen("user_profile")
    object MaleHome : AuthScreen("male_home")
    object FemaleHome : AuthScreen("female_home")
}

@Composable
fun AuthNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AuthScreen.Login.route) {
        composable(AuthScreen.Login.route) { LoginScreen(navController) }
        composable(AuthScreen.SignUp.route) { SignUpScreen(navController) }
        composable(AuthScreen.Home.route) { HomeScreen(navController) }
        composable(AuthScreen.UserProfile.route) { ProfileScreen(navController) }
        composable(AuthScreen.MaleHome.route) { MaleHomeScreen(navController) }
        composable(AuthScreen.FemaleHome.route) { FemaleHomeScreen(navController) }
    }
}