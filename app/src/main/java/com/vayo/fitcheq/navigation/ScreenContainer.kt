package com.vayo.fitcheq.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ScreenContainer(
    navController: NavController,
    showBottomBar: Boolean = true,
    content: @Composable (paddingValues: androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigation(navController = navController)
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
} 