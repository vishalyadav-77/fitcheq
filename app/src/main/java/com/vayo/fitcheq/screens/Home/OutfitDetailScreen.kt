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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.FilterChip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import com.vayo.fitcheq.AuthScreen
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.lazy.items   // For LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.painterResource
import com.vayo.fitcheq.R
import com.vayo.fitcheq.data.model.Filters
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailsScreen(gender: String, fieldName: String,fieldValue: String, viewModel: MaleHomeViewModel,navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
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

    // 🔹 Extract unique categories from outfits
    val categories = remember(outfits) {
        outfits.mapNotNull { it.category }
            .distinct() }
    val brands = remember(outfits){
        outfits.mapNotNull { it.website }
            .distinct() }
    val colors = remember(outfits){
        outfits.mapNotNull { it.color }
            .distinct() }
    val types = remember(outfits){
        outfits.mapNotNull { it.type }
            .distinct() }

    // 🔹 State for selected category
    var filters by remember { mutableStateOf(Filters()) }

    // 🔹 Filter outfits if a category is selected
    val filteredOutfits = remember(outfits, filters) {
        outfits.filter { outfit ->
            val matchCategory = if (filters.categories.isNotEmpty()) {
                outfit.category in filters.categories
            } else true

            val matchBrand = if (filters.websites.isNotEmpty()) {
                outfit.website in filters.websites
            } else true

            val matchColor = if (filters.colors.isNotEmpty()) {
                outfit.color in filters.colors
            } else true
            val matchType = filters.type?.let { outfit.type == it } ?: true

            matchCategory && matchBrand && matchColor && matchType
        }
    }


    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // allows 60% expansion
    )
    var showSheet by remember { mutableStateOf(false) }

    val filterTypes = listOf("Category", "Brand", "Color", "Price")
    var selectedFilter by remember { mutableStateOf(filterTypes.first()) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        // Title section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text( modifier = Modifier.padding(bottom = 8.dp),
                text = "${fieldValue.replaceFirstChar { it.uppercaseChar() }} Collection",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold )

            // 🔹 Category chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(modifier = Modifier.padding(end = 6.dp),
                        onClick = { showSheet = true },
                        shape = FilterChipDefaults.shape,
                        color = Color.Black,
                        border = BorderStroke(0.3.dp, Color.Black),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.filter_icon),
                                contentDescription = "Filter icon",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp)) // small gap between icon and text
                            Text(
                                text = "Filter",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(start = 8.dp, end = 10.dp)
                    ) {
                        val listToShow = if (categories.isNotEmpty() && categories.size > 1) categories else types
                        val isCategoryList = categories.isNotEmpty() && categories.size > 1

                        items(listToShow) { item ->
                            val chipTitle = item.replaceFirstChar { it.uppercase() }
                            val isSelected = if (isCategoryList) filters.categories.contains(item)
                            else filters.type?.contains(item) == true // or however you store type filter

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    filters = if (isCategoryList) {
                                        filters.copy(
                                            categories = if (isSelected) filters.categories - item
                                            else filters.categories + item
                                        )
                                    } else {
                                        filters.copy(
                                            type = if (isSelected) null else item // assuming only one type can be selected
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = if (isSelected) "$chipTitle ✕" else chipTitle,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                },
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderWidth = 0.3.dp,
                                    selectedBorderWidth = 0.3.dp
                                ),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.Transparent,
                                    selectedContainerColor = Color.Black
                                )
                            )
                        }
                    }
                }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                color = Color.LightGray,
                thickness = 0.5.dp
            )

        }

        // Content section
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp))
        {
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
                        item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(8.dp)) }
                        items(filteredOutfits) { outfit ->
                            val favoriteMap by viewModel.favoriteMap.collectAsState()
                            val isFavorite = favoriteMap[outfit.id] ?: false
                            val priceNumber = outfit.price.toLongOrNull() ?: 0L
                            val formattedPrice = NumberFormat.getNumberInstance(Locale("en", "IN"))
                                .format(priceNumber)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .clickable {
                                        navController.navigate(AuthScreen.ItemInfo.passOutfit(outfit))
                                    },
                                elevation = CardDefaults.cardElevation(1.dp),
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
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null // hides drag icon
        ) {
            // Use Box to allow sticky bottom row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalConfiguration.current.screenHeightDp * 0.7).dp) // fixed height
            ) {

                Column(modifier = Modifier.fillMaxSize()) {
                    //TITLE
                    Text(
                        "Filters", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                    )
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.LightGray,
                        thickness = 0.3.dp
                    )

                    // Content
                    Row(modifier = Modifier.fillMaxWidth()
                        .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.7).dp - 135.dp)) {
                        // Left column: filter types
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFF5F5F5))
                        ) {
                            items(filterTypes) { type ->
                                Text(
                                    text = type,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedFilter = type }
                                        .background(color = if(selectedFilter == type) Color.White else Color(0xFFF5F5F5),)
                                        .padding(16.dp),
                                    color = if (selectedFilter == type) Color.Black else Color.Gray,
                                    fontWeight = if (selectedFilter == type) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                        // Right column: options for the selected filter
                        LazyColumn(
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxHeight()
                        ) {
                            when (selectedFilter) {
                                "Category" -> {
                                    items(categories) { category ->
                                        val categoryTitle = category.replaceFirstChar { it.uppercase() }
                                        CategoryRow(
                                            text = categoryTitle,
                                            isSelected = filters.categories.contains(category),
                                            onSelectedChange = { selected ->
                                                filters = filters.copy(
                                                    categories = if (selected) {
                                                        filters.categories + category
                                                    } else {
                                                        filters.categories - category
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                                "Brand" -> {
                                    items(brands) { brand ->
                                        CategoryRow(
                                            text = brand,
                                            isSelected = filters.websites.contains(brand),
                                            onSelectedChange = { selected ->
                                                filters = filters.copy(
                                                    websites = if (selected) {
                                                        filters.websites + brand
                                                    } else {
                                                        filters.websites - brand
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }

                            }
                        }
                    }
                }

                // Bottom buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .align(Alignment.BottomCenter)
                        .height(80.dp)
                ) {
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.LightGray,
                        thickness = 0.3.dp
                    )
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
                                filters = Filters()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Clear", fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Button(modifier = Modifier
                            .width(280.dp)
                            .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black,   contentColor = Color.White),
                            onClick = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Apply", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    text: String,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectedChange(!isSelected) }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp) // shrink overall checkbox footprint
                .padding(0.dp) // no extra padding
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectedChange(it) },
                modifier = Modifier.size(20.dp),
                colors = CheckboxDefaults.colors(checkedColor = Color.Black, uncheckedColor = Color.Black)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
        )
    }
}
