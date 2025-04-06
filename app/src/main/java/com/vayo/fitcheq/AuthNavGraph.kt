package com.vayo.fitcheq

//This will define the navigation routes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vayo.fitcheq.screens.auth.LoginScreen
import com.vayo.fitcheq.screens.auth.SignUpScreen
import com.vayo.fitcheq.screens.Home.FemaleHomeScreen
import com.vayo.fitcheq.screens.Home.MaleHomeScreen
import com.vayo.fitcheq.screens.auth.ProfileScreen
import com.vayo.fitcheq.screens.Home.CommunityScreen
import com.vayo.fitcheq.screens.Home.MyProfileScreen
import com.vayo.fitcheq.screens.Home.SavedOutfitScreen

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object SignUp : AuthScreen("signup")
    object UserProfile : AuthScreen("user_profile")
    object MaleHome : AuthScreen("male_home")
    object FemaleHome : AuthScreen("female_home")
    object Community : AuthScreen("community")
    object SavedOutfit : AuthScreen("saved_outfit")
    object MyProfile : AuthScreen("my_profile")
}

@Composable
fun AuthNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = AuthScreen.Login.route) {
        composable(AuthScreen.Login.route) { LoginScreen(navController, authViewModel) }
        composable(AuthScreen.SignUp.route) { SignUpScreen(navController, authViewModel) }
        composable(AuthScreen.UserProfile.route) { ProfileScreen(navController, authViewModel) }
        composable(AuthScreen.MaleHome.route) { MaleHomeScreen(navController, authViewModel) }
        composable(AuthScreen.FemaleHome.route) { FemaleHomeScreen(navController, authViewModel) }
        composable(AuthScreen.Community.route) { CommunityScreen(navController, authViewModel) }
        composable(AuthScreen.SavedOutfit.route) { SavedOutfitScreen(navController, authViewModel) }
        composable(AuthScreen.MyProfile.route) { MyProfileScreen(navController, authViewModel) }
    }
}