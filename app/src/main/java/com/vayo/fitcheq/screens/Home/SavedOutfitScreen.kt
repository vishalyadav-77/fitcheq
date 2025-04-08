package com.vayo.fitcheq.screens.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.navigation.ScreenContainer
import com.vayo.fitcheq.R

//@Preview(showBackground = true)
//@Composable
//fun SavedOutfitScreenPreview() {
//    // Provide a fake NavController (won't actually navigate in preview)
//    val navController = rememberNavController()
//
//    SavedOutfitScreen(navController)
//}

@Composable
fun SavedOutfitScreen(navController: NavController, authViewModel: AuthViewModel) {
//fun SavedOutfitScreen(navController: NavController) {
    ScreenContainer(navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wishlist",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )
        }
        LazyColumn( modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                //IF CONDITION FOR INVISIBLE AND VISIBLE VARIABLE STATE
                Box(
                    modifier = Modifier
                        .fillParentMaxSize(), // fills entire LazyColumn height
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.drobe_image),
                            contentDescription = "Wardrobe",
                            modifier = Modifier
                                .size(320.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "You haven’t saved anything\nStart building your wardrobe",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

