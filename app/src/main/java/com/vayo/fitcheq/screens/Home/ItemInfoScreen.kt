package com.vayo.fitcheq.screens.Home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.vayo.fitcheq.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import androidx.navigation.NavController
import com.vayo.fitcheq.data.model.OutfitData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import androidx.compose.ui.res.colorResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.Font
import com.vayo.fitcheq.data.model.brandMap
import java.text.NumberFormat
import java.util.Locale


//@Preview
@Composable
fun ItemInfoScreen(outfit: OutfitData){
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val showBg by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 80
        }
    }
    val imagesToUse = if (outfit.imageUrls.isNotEmpty()) outfit.imageUrls else listOf(outfit.imageUrl)
    val priceNumber = outfit.price.toLongOrNull() ?: 0L
    val formattedPrice = NumberFormat.getNumberInstance(Locale("en", "IN")).format(priceNumber)
    val fallbackText = "Please visit site for this info"
    val brandInfo = brandMap[outfit.website]
    val combinedPolicy = listOfNotNull(
        brandInfo?.returnPolicy?.takeIf { it.isNotBlank() },
        brandInfo?.exchangePolicy?.takeIf { it.isNotBlank() }
    ).joinToString("\n\n") // two line breaks between sections
    var isShippingExpanded by remember { mutableStateOf(false) }
    var isReturnsExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {

            BottomActionBar(
                outfitUrl = outfit.link,
                onWishlistClick = { /* handle */ },
                onShareClick = { /* handle */ }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
            ) {
                item {
                    ImageCarousel(imagesToUse)
                }
                item {
                    Column(modifier= Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = outfit.website, fontWeight = FontWeight.Bold, fontSize = 20.sp )
                        Text(text=outfit.title, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text= "₹ ${formattedPrice}", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .background(color = colorResource(id = R.color.mint_grey)),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(start = 18.dp, end = 5.dp)
                                .size(20.dp)
                        )

                        Text(text="The price may vary on original site", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Column(modifier= Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                    ) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(text = "SIZE", fontWeight = FontWeight.Bold, fontSize = 20.sp )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isShippingExpanded = !isShippingExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SHIPPING",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isShippingExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                                contentDescription = if (isShippingExpanded) "Collapse" else "Expand",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = isShippingExpanded,
                            enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                            exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = brandInfo?.shippingPolicy?.takeIf { it.isNotBlank() } ?: fallbackText,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))

                        // RETURN & EXCHANGE SECTION
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isReturnsExpanded = !isReturnsExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RETURN & EXCHANGE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isReturnsExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                                contentDescription = if (isReturnsExpanded) "Collapse" else "Expand",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = isReturnsExpanded,
                            enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                            exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (combinedPolicy.isNotBlank()) combinedPolicy else fallbackText,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(text = "PRODUCT DESCRIPTION", fontWeight = FontWeight.Bold, fontSize = 20.sp )
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "RELATED PRODUCTS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(38.dp))
                    }
                }

            }

            IconButton(
                onClick = {  },
                modifier = Modifier
                    .padding(top = 10.dp, start = 14.dp)
                    .background(
                        color = if (showBg) Color.White else Color.White.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .size(32.dp)
                    .align(Alignment.TopStart),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black.copy(alpha = 0.6f)
                )
            }

            IconButton( onClick = { },
                modifier = Modifier
                    .padding(top = 10.dp, end = 14.dp)
                    .background(
                        color = if (showBg) Color.White else Color.White.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .size(32.dp)
                    .align(Alignment.TopEnd),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarousel(images: List<String>) {
    if (images.isEmpty()) return

    if (images.size == 1) {
        // Just one image, no swiping
        AsyncImage(
            model = images[0],
            contentDescription = "Product Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        val pagerState = rememberPagerState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Product Image $page",
                    modifier = Modifier
                        .fillMaxSize(),
//                        .height(600.dp),
                    contentScale = ContentScale.Crop
                )
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun BottomActionBar(
    outfitUrl: String,
    onWishlistClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedButton(modifier = Modifier
                .width(100.dp)
                .height(50.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent,   contentColor = Color.Black),
                onClick = {
                   //
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favourite",
                    modifier = Modifier.size(22.dp)
                )

            }

            Button(modifier = Modifier
                .width(280.dp)
                .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black,   contentColor = Color.White),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(outfitUrl))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("BUY", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

