package com.vayo.fitcheq.screens.Home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vayo.fitcheq.navigation.ScreenContainer
import com.vayo.fitcheq.R
import com.vayo.fitcheq.viewmodels.MaleHomeViewModel
import android.widget.Toast


@Composable
fun SavedOutfitScreen(navController: NavController, viewModel: MaleHomeViewModel) {
    val context = LocalContext.current
    val savedOutfits by viewModel.savedOutfits.collectAsState() // Changed from outfits to savedOutfits

    LaunchedEffect(Unit) {
        viewModel.fetchSavedOutfits()
    }

    ScreenContainer(navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Important to respect system bars
        ) {
            //TITLE
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
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
            // BODY OF THE SCREEN
            if (savedOutfits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.drobe_image),
                            contentDescription = "Empty Wishlist",
                            modifier = Modifier
                                .size(300.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Your wishlist is empty",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedOutfits) { outfit ->  // Changed from outfits to savedOutfits
                        val favoriteMap by viewModel.favoriteMap.collectAsState()
                        val isFavorite = favoriteMap[outfit.id] ?: false

                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .padding(2.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(outfit.link))
                                    context.startActivity(intent)
                                },
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                ) {
                                    AsyncImage(
                                        model = outfit.imageUrl,
                                        contentDescription = outfit.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
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
                                            .weight(1f)
                                            .basicMarquee()
                                            .focusable()
                                    )

                                    Text(
                                        text = "₹${outfit.price}",
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
                }
            }
        }
    }
}

