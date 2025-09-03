package com.vayo.fitcheq.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.vayo.fitcheq.R
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.rounded.FavoriteBorder


sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: IconType
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = IconType.PainterRes(R.drawable.home_simple_door)
    )
    object Community : BottomNavItem(
        route = "community",
        title = "Guide",
        icon = IconType.PainterRes(R.drawable.explore_hanger)
    )
    object SavedOutfit : BottomNavItem(
        route = "saved_outfit",
        title = "Wishlist",
        icon = IconType.PainterRes(R.drawable.ps_heart)
    )
    object MyProfile : BottomNavItem(
        route = "my_profile",
        title = "Profile",
        icon = IconType.PainterRes(R.drawable.a_profile_circle)
    )
}
sealed class IconType {
    data class Vector(val imageVector: ImageVector) : IconType()
    data class PainterRes(@DrawableRes val resId: Int) : IconType()
}
