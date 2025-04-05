package com.vayo.fitcheq.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Community : BottomNavItem(
        route = "community",
        title = "Community",
        icon = Icons.Default.Person
    )
    object SavedOutfit : BottomNavItem(
        route = "saved_outfit",
        title = "Saved",
        icon = Icons.Default.Favorite
    )
    object MyProfile : BottomNavItem(
        route = "my_profile",
        title = "Profile",
        icon = Icons.Default.AccountCircle
    )
} 