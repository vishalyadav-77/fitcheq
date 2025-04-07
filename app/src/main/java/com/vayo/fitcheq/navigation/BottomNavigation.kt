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
                            // Get the appropriate home route based on current route
                            val homeRoute = if (currentRoute?.contains("female") == true || 
                                (currentRoute != AuthScreen.MaleHome.route && navController.previousBackStackEntry?.destination?.route?.contains("female") == true)) {
                                AuthScreen.FemaleHome.route
                            } else {
                                AuthScreen.MaleHome.route
                            }
                            
                            if (currentRoute != homeRoute) {
                                navController.navigate(homeRoute) {
                                    popUpTo(homeRoute) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        else -> {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    // Keep the home screen as the parent
                                    val homeRoute = if (currentRoute?.contains("female") == true || 
                                        (currentRoute != AuthScreen.MaleHome.route && navController.previousBackStackEntry?.destination?.route?.contains("female") == true)) {
                                        AuthScreen.FemaleHome.route
                                    } else {
                                        AuthScreen.MaleHome.route
                                    }
                                    
                                    // Pop up to home screen but don't remove it
                                    popUpTo(homeRoute) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            )
        }
    }
} 