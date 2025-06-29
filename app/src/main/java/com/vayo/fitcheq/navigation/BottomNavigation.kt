package com.vayo.fitcheq.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp) // or 64.dp if you want slightly taller
    ) {
        NavigationBar(containerColor = Color.White,
            tonalElevation = 6.dp) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                val selected = when (item) {
                    BottomNavItem.Home -> currentRoute == AuthScreen.MaleHome.route || currentRoute == AuthScreen.FemaleHome.route
                    else -> currentRoute == item.route
                }

                NavigationBarItem(
                    icon = { Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(20.dp)
                    ) },
                    label = { Text(text = item.title) },
                    selected = selected,
                    onClick = {
                        when (item) {
                            BottomNavItem.Home -> {
                                // Get the appropriate home route based on current route
                                val homeRoute = if (currentRoute?.contains("female") == true ||
                                    (currentRoute != AuthScreen.MaleHome.route && navController.previousBackStackEntry?.destination?.route?.contains(
                                        "female"
                                    ) == true)
                                ) {
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
                                        val homeRoute =
                                            if (currentRoute?.contains("female") == true ||
                                                (currentRoute != AuthScreen.MaleHome.route && navController.previousBackStackEntry?.destination?.route?.contains(
                                                    "female"
                                                ) == true)
                                            ) {
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
} 