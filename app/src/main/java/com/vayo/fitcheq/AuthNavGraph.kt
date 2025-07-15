package com.vayo.fitcheq

//This will define the navigation routes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.vayo.fitcheq.screens.auth.LoginScreen
import com.vayo.fitcheq.screens.auth.SignUpScreen
import com.vayo.fitcheq.screens.Home.FemaleHomeScreen
import com.vayo.fitcheq.screens.Home.MaleHomeScreen
import com.vayo.fitcheq.screens.auth.ProfileScreen
import com.vayo.fitcheq.screens.Home.CommunityScreen
import com.vayo.fitcheq.screens.Home.MyProfileScreen
import com.vayo.fitcheq.screens.Home.SavedOutfitScreen
import com.vayo.fitcheq.screens.Home.OutfitDetailsScreen
import com.vayo.fitcheq.screens.Home.SettingsPage
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object SignUp : AuthScreen("signup")
    object UserProfile : AuthScreen("user_profile")
    object MaleHome : AuthScreen("male_home")
    object FemaleHome : AuthScreen("female_home")
    object Community : AuthScreen("community")
    object SavedOutfit : AuthScreen("saved_outfit")
    object MyProfile : AuthScreen("my_profile")
    object OutfitDetails : AuthScreen("outfit_details/{gender}/{tag}") {
        fun createRoute(gender: String, tag: String): String = "outfit_details/$gender/$tag"
    }
    object SettingsPage : AuthScreen("settings_page")
}

@Composable
fun AuthNavGraph(
    navController: NavHostController, 
    authViewModel: AuthViewModel,
    maleViewModel: MaleHomeViewModel
) {
    NavHost(navController = navController, startDestination = AuthScreen.Login.route) {
        composable(
            route = AuthScreen.Login.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { LoginScreen(navController, authViewModel) }
        
        composable(
            route = AuthScreen.SignUp.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { SignUpScreen(navController, authViewModel) }
        
        composable(
            route = AuthScreen.UserProfile.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { ProfileScreen(navController, authViewModel) }
        
        composable(
            route = AuthScreen.MaleHome.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { MaleHomeScreen(navController, authViewModel) }
        
        composable(
            route = AuthScreen.FemaleHome.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { FemaleHomeScreen(navController, authViewModel) }
        
        composable(
            route = AuthScreen.Community.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { CommunityScreen(navController, authViewModel) }
        
        composable(
            route = AuthScreen.SavedOutfit.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { SavedOutfitScreen(navController, maleViewModel) }
        
        composable(
            route = AuthScreen.MyProfile.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { MyProfileScreen(navController, authViewModel) }
        
        composable(
            route = AuthScreen.OutfitDetails.route,
            arguments = listOf(
                navArgument("gender") { type = NavType.StringType },
                navArgument("tag") { type = NavType.StringType }
            ),
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val gender = backStackEntry.arguments?.getString("gender") ?: "Male"
            val tag = backStackEntry.arguments?.getString("tag") ?: "default"

            OutfitDetailsScreen(
                gender = gender,
                tag = tag,
                viewModel = maleViewModel
            ) }
        composable(
            route = AuthScreen.SettingsPage.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, // Slide from right
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, // Slide to left
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, // Reverse when coming back
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, // Reverse when going back
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) {
            SettingsPage(navController)
        }

    }
}