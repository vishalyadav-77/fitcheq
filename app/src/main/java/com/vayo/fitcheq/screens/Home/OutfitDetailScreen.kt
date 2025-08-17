package com.vayo.fitcheq.screens.Home


import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import com.vayo.fitcheq.AuthScreen
import java.text.NumberFormat
import java.util.Locale

//@Preview
@Composable
fun OutfitDetailsScreen(gender: String, fieldName: String,fieldValue: String, viewModel: MaleHomeViewModel,navController: NavController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    
    // Local loading state to show immediately
    var isInitialLoading by remember { mutableStateOf(true) }

    // Clear previous data and start loading immediately
    LaunchedEffect(gender, fieldName, fieldValue) {
        isInitialLoading = true
        // Clear previous outfits to prevent showing old content
        viewModel.clearOutfits()
        viewModel.fetchOutfitsByFieldAndGender(fieldName, fieldValue, gender)
        isInitialLoading = false
    }

    // Load favorites when screen loads
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userId ->
            viewModel.loadFavorites(userId)
        }
    }

    val outfits = viewModel.outfits.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 6.dp)
    ) {
        // Title section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${fieldValue.replaceFirstChar { it.uppercaseChar() }} Collection",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )
        }

        // Content section
        when {
            isInitialLoading || isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            outfits.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No outfits found",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(span = { GridItemSpan(2) }) {Spacer(modifier = Modifier.height(8.dp))}
                    items(outfits) { outfit ->
                        val favoriteMap by viewModel.favoriteMap.collectAsState()
                        val isFavorite = favoriteMap[outfit.id] ?: false
                        val priceNumber = outfit.price.toLongOrNull() ?: 0L
                        val formattedPrice = NumberFormat.getNumberInstance(Locale("en", "IN")).format(priceNumber)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                                .clickable {
                                    navController.navigate(AuthScreen.ItemInfo.passOutfit(outfit))
                                },
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(4f / 5f)
                                ) {
                                    AsyncImage(
                                        model = outfit.imageUrl,
                                        contentDescription = outfit.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) Color.Red else Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .size(24.dp)
                                            .clickable {
                                                viewModel.toggleFavorite(outfit)
                                                Toast.makeText(
                                                    context,
                                                    if (isFavorite) "Removed from wishlist" else "Added to wishlist",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = outfit.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee()
                                            .focusable()
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = "₹${formattedPrice}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = outfit.website,
                                            fontSize = 12.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}