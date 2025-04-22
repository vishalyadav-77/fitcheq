package com.vayo.fitcheq.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel

class MainNavGraph {
    sealed class NavScreen(val route: String) {
        object OutfitData : NavScreen("outfit_data")
    }
}

@Composable
fun MainNavGraph(navController: NavController, navViewModel: MaleHomeViewModel){

}