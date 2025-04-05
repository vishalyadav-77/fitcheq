package com.vayo.fitcheq.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vayo.fitcheq.AuthScreen

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Community,
        BottomNavItem.SavedOutfit,
        BottomNavItem.MyProfile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = when (item) {
                BottomNavItem.Home -> currentRoute == AuthScreen.MaleHome.route || currentRoute == AuthScreen.FemaleHome.route
                else -> currentRoute == item.route
            }

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = selected,
                onClick = {
                    when (item) {
                        BottomNavItem.Home -> {
                            // Don't navigate if already on home screen
                            if (currentRoute != AuthScreen.MaleHome.route && currentRoute != AuthScreen.FemaleHome.route) {
                                // Navigate back to the last home screen
                                navController.popBackStack(
                                    route = if (currentRoute?.contains("male") == true) AuthScreen.MaleHome.route else AuthScreen.FemaleHome.route,
                                    inclusive = false
                                )
                            }
                        }
                        else -> {
                            navController.navigate(item.route) {
                                // Preserve the home screen in back stack
                                popUpTo(if (currentRoute?.contains("male") == true) AuthScreen.MaleHome.route else AuthScreen.FemaleHome.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    }
} 