package com.vayo.fitcheq.screens.Home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FemaleHomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with App Title
        Text(
            text = "Fit Cheq",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Row for Profile and Weather Widget Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* TODO: Handle Weather Click */ },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Weather Widget")
            }

            Button(
                onClick = { /* TODO: Handle Profile Click */ },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(" Female Profile")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clickable { /* TODO: Handle Search Click */ },
            contentAlignment = Alignment.Center
        ) {
            Text("Search for outfits...", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Seasonal Fits Section
        Text(
            text = "Fits According to Season",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("• Summer • Winter • Monsoon • Spring")

        Spacer(modifier = Modifier.height(24.dp))

        // Fashion Style Section
        Text(
            text = "Fits According to Fashion Style",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("• Old Money • Starboy • Streetwear • Casual")

        Spacer(modifier = Modifier.height(24.dp))

        // Placeholder for More Sections if Needed
        Text(
            text = "More sections coming soon...",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

//@Preview(showBackground = true)
@Composable
fun PreviewFemaleHomeScreen() {
//    MaleHomeScreen()
}