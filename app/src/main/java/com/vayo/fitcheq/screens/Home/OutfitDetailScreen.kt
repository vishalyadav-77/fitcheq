package com.vayo.fitcheq.screens.Home


import android.util.Log
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FilterChip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import com.vayo.fitcheq.AuthScreen
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.lazy.items   // For LazyRow
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.material.RangeSlider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.sharp.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.snapshotFlow
import coil.request.ImageRequest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.compose.material3.IconButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailsScreen(gender: String, fieldName: String,fieldValue: String, viewModel: MaleHomeViewModel,navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    // Local loading state to show immediately
    var isInitialLoading by remember { mutableStateOf(true) }
    // State for selected filters
    var filters by remember { mutableStateOf(Filters()) }
    // Track if we're applying filters to show loading
    var isApplyingFilters by remember { mutableStateOf(false) }

    // Clear previous data and start loading immediately
    LaunchedEffect(gender, fieldName, fieldValue) {
        Log.d("OutfitDetailsScreen", "🔄 Screen params changed: gender=$gender, field=$fieldName, value=$fieldValue")
        filters = Filters()
        isInitialLoading = true
        isApplyingFilters = false

        // Clear previous outfits to prevent showing old content
        viewModel.clearOutfits()

        // Fetch filters and initial outfits
        Log.d("OutfitDetailsScreen", "📡 Fetching available filters...")
        viewModel.fetchAvailableFilters(gender, fieldName, fieldValue)

        Log.d("OutfitDetailsScreen", "📡 Fetching initial outfits...")
        viewModel.fetchOutfitsByFieldAndGender(context, fieldName, fieldValue, gender)

        isInitialLoading = false
    }

    // Load favorites when screen loads
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userId ->
            Log.d("OutfitDetailsScreen", "❤️ Loading favorites for user: $userId")
            viewModel.loadFavorites(userId)
        }
    }

    val outfits = viewModel.outfits.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val availableFilters = viewModel.availableFilters.collectAsState().value

    val categories = availableFilters.categories.toList()
    val brands = availableFilters.brands.toList()
    val colors = availableFilters.colors.toList()
    val fits = availableFilters.fits.toList()
    val types = availableFilters.types.toList()

    Log.d("OutfitDetailsScreen", "🔍 Available filters - Categories: ${categories.size}, Brands: ${brands.size}, Colors: ${colors.size}, Types: ${types.size}, Fits: ${fits.size}")

    // Add this helper function to check if any filters are applied
    val hasActiveFilters = filters.categories.isNotEmpty() ||
            filters.websites.isNotEmpty() ||
            filters.colors.isNotEmpty() ||
            filters.fits.isNotEmpty() ||
            filters.type != null ||
            filters.priceRange != 0f..20000f // assuming default range

    Log.d("OutfitDetailsScreen", "🎯 Current filters - Categories: ${filters.categories}, Websites: ${filters.websites}, Colors: ${filters.colors}, Type: ${filters.type}, Fits: ${filters.fits}, HasActive: $hasActiveFilters")

    // Function to apply filters consistently
    val applyFilters = { newFilters: Filters ->
        Log.d("OutfitDetailsScreen", "🔧 Applying filters: $newFilters")
        filters = newFilters
        isApplyingFilters = true

        coroutineScope.launch {
            try {
                val hasNewActiveFilters = newFilters.categories.isNotEmpty() ||
                        newFilters.websites.isNotEmpty() ||
                        newFilters.colors.isNotEmpty() ||
                        newFilters.fits.isNotEmpty() ||
                        newFilters.type != null ||
                        newFilters.priceRange != 0f..20000f

                if (hasNewActiveFilters) {
                    Log.d("OutfitDetailsScreen", "📡 Fetching filtered outfits...")
                    viewModel.fetchFilteredOutfits(context, fieldName, fieldValue, gender, newFilters, reset = true)
                } else {
                    Log.d("OutfitDetailsScreen", "📡 No filters active, fetching all outfits...")
                    viewModel.clearOutfits()
                    viewModel.fetchOutfitsByFieldAndGender(context, fieldName, fieldValue, gender)
                }
            } finally {
                isApplyingFilters = false
            }
        }
    }

    // Pagination
    val gridState = rememberLazyGridState()
    LaunchedEffect(gridState, outfits.size, isLoading, hasActiveFilters) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == -1) return@collect
                val total = gridState.layoutInfo.totalItemsCount
                val prefetchThreshold = 4 // 2 rows * 2 columns

                if (total > 0 && lastVisibleIndex >= total - prefetchThreshold && !isLoading && !isApplyingFilters) {
                    Log.d("OutfitDetailsScreen", "📄 Loading more items... lastVisible: $lastVisibleIndex, total: $total, hasActiveFilters: $hasActiveFilters")

                    if (hasActiveFilters) {
                        Log.d("OutfitDetailsScreen", "📄 Loading more filtered items...")
                        viewModel.fetchFilteredOutfits(context, fieldName, fieldValue, gender, filters, reset = false)
                    } else {
                        Log.d("OutfitDetailsScreen", "📄 Loading more unfiltered items...")
                        viewModel.fetchOutfitsByFieldAndGender(context, fieldName, fieldValue, gender, reset = false)
                    }
                }
            }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // allows 60% expansion
    )
    var showSheet by remember { mutableStateOf(false) }

    val filterTypes by remember(categories) {
        mutableStateOf(
            if (categories.isNotEmpty() && categories.size > 1){
                listOf("Category", "Brand", "Color", "Price")}
            else if (categories.size == 1) {
                // Single category - hide Category filter, show Fit
                listOf("Brand", "Color", "Price", "Fit")
            }else{
                listOf("Category", "Brand", "Color", "Price", "Fit")}
        )
    }
    // Ensure selectedFilter stays valid if filterTypes changes
    var selectedFilter by remember(filterTypes) {
        mutableStateOf(filterTypes.first())
    }

    val categoryMaxMap = mapOf(
        "accessories" to 2000f,
        "tshirt" to 20000f,
        "shirt" to 10000f
    )
    val maxPriceLimit by remember(filters.categories) {
        mutableStateOf(
            filters.categories
                .mapNotNull { it.lowercase().let(categoryMaxMap::get) }
                .minOrNull() ?: 10000f
        )
    }
    LaunchedEffect(maxPriceLimit, filters.categories) {
        // Reset price range when categories change or when max limit changes
        val shouldResetPriceRange = filters.categories.isEmpty() ||
                filters.priceRange.endInclusive != maxPriceLimit

        if (shouldResetPriceRange) {
            val newRange = 0f..maxPriceLimit
            if (newRange != filters.priceRange) {
                Log.d("OutfitDetailsScreen", "💰 Resetting price range to: $newRange (maxLimit: $maxPriceLimit)")
                val newFilters = filters.copy(priceRange = newRange)
                filters = newFilters // Update local state without triggering new fetch
            }
        }
    }
    LaunchedEffect(maxPriceLimit) {
        val currentRange = filters.priceRange
        val newRange = currentRange.start..min(currentRange.endInclusive, maxPriceLimit)
        if (newRange != currentRange) {
            val newFilters = filters.copy(priceRange = newRange)
            applyFilters(newFilters)
        }
    }

    // CONTENT
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                // Back button on the left
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Sharp.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${fieldValue.replaceFirstChar { it.uppercaseChar() }} Collection",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

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

                    Log.d("OutfitDetailsScreen", "🎨 Showing chips - isCategoryList: $isCategoryList, listToShow: $listToShow")

                    items(listToShow) { item ->
                        val chipTitle = item.replaceFirstChar { it.uppercase() }
                        val isSelected = if (isCategoryList) filters.categories.contains(item)
                        else filters.type == item

                        Log.d("OutfitDetailsScreen", "🎯 Chip '$item' - selected: $isSelected, isCategoryList: $isCategoryList")

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                Log.d("OutfitDetailsScreen", "🎯 Chip clicked: $item, currentlySelected: $isSelected")

                                val newFilters = if (isCategoryList) {
                                    filters.copy(
                                        categories = if (isSelected) filters.categories - item
                                        else filters.categories + item
                                    )
                                } else {
                                    filters.copy(
                                        type = if (isSelected) null else item
                                    )
                                }

                                Log.d("OutfitDetailsScreen", "🎯 New filters after chip click: $newFilters")
                                applyFilters(newFilters)
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

            HorizontalDivider(
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
                isInitialLoading || (isLoading && outfits.isEmpty())-> {
                    Log.d("OutfitDetailsScreen", "⏳ Showing initial loading...")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Log.e("OutfitDetailsScreen", "❌ Error state: $error")
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
                hasActiveFilters && outfits.isEmpty() && !isLoading && !isApplyingFilters -> {
                    Log.d("OutfitDetailsScreen", "🚫 No filtered results")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No products found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                            Text(
                                text = "for selected filters",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            OutlinedButton(
                                onClick = {
                                    Log.d("OutfitDetailsScreen", "🔄 Clearing all filters")
                                    applyFilters(Filters())
                                },
                                modifier = Modifier.padding(top = 16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Clear Filters")
                            }
                        }
                    }
                }
                outfits.isEmpty() -> {
                    Log.d("OutfitDetailsScreen", "📭 No outfits found")
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
                    Log.d("OutfitDetailsScreen", "📱 Displaying ${outfits.size} outfits")

                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 0.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(8.dp)) }
                            items(outfits) { outfit ->
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
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(outfit.imageUrl)
                                                    .size(500)
                                                    .crossfade(200)
                                                    .build(),
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
                                            val formattedTitle = outfit.title
                                                .split(" ")
                                                .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
                                            Text(
                                                text = formattedTitle,
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
                                                    text = outfit.website.toUpperCase(),
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
                                if (isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }

                        // Show loading overlay when applying filters
                        if (isApplyingFilters) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier.padding(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Applying filters...")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //FILTER SHEET
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null // hides drag icon
        ) {
            // FILTER SHEET
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalConfiguration.current.screenHeightDp * 0.7).dp) // fixed height
            ) {
                //MAIN
                Column(modifier = Modifier.fillMaxSize()) {
                    //TITLE
                    Text(
                        "Filters", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                    )
                    HorizontalDivider(
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
                                    item {
                                        Text(
                                            text = "Category",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                    }
                                    items(categories) { category ->
                                        val categoryTitle = category.replaceFirstChar { it.uppercase() }
                                        CategoryRow(
                                            text = categoryTitle,
                                            isSelected = filters.categories.contains(category),
                                            onSelectedChange = { selected ->
                                                val newFilters = filters.copy(
                                                    categories = if (selected) {
                                                        filters.categories + category
                                                    } else {
                                                        filters.categories - category
                                                    }
                                                )
                                                Log.d("OutfitDetailsScreen", "🎯 Category filter changed: $category, selected: $selected, newFilters: $newFilters")
                                                filters = newFilters
                                            }
                                        )
                                    }
                                }
                                "Brand" -> {
                                    item {
                                        Text(
                                            text = "Brands",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                    }
                                    items(brands) { brand ->
                                        val brandTitle = brand.replaceFirstChar { it.uppercase() }
                                        CategoryRow(
                                            text = brandTitle,
                                            isSelected = filters.websites.contains(brand),
                                            onSelectedChange = { selected ->
                                                val newFilters = filters.copy(websites = if(selected) filters.websites + brand else filters.websites - brand)
                                                Log.d("OutfitDetailsScreen", "🎯 Brand filter changed: $brand, selected: $selected")
                                                filters = newFilters
                                            }
                                        )
                                    }
                                }
                                "Color" -> {
                                    item {
                                        Text(
                                            text = "Colors",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                    }
                                    items(colors) { color ->
                                        val colorTitle = color.replaceFirstChar { it.uppercase() }
                                        CategoryRow(
                                            text = colorTitle,
                                            isSelected = filters.colors.contains(color),
                                            onSelectedChange = { selected ->
                                                val newFilters = filters.copy(
                                                    colors = if (selected) {
                                                        filters.colors + color
                                                    } else {
                                                        filters.colors - color
                                                    }
                                                )
                                                Log.d("OutfitDetailsScreen", "🎯 Color filter changed: $color, selected: $selected")
                                                filters = newFilters
                                            }
                                        )
                                    }
                                }
                                "Price" -> {
                                    item {
                                        PriceRangeFilter(
                                            minPrice = 0f,
                                            maxPrice = maxPriceLimit,
                                            priceRange = filters.priceRange,
                                            onRangeChange = { selectedRange ->
                                                Log.d("OutfitDetailsScreen", "💰 Price range changed: $selectedRange")
                                                filters = filters.copy(priceRange = selectedRange)
                                            }
                                        )
                                    }
                                }
                                "Fit" -> {
                                    item {
                                        Text(
                                            text = "Fit",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                    }
                                    items(fits) { fit ->
                                        val fitTitle = fit.replaceFirstChar { it.uppercase() }
                                        CategoryRow(
                                            text = fitTitle,
                                            isSelected = filters.fits.contains(fit),
                                            onSelectedChange = { selected ->
                                                val newFilters = filters.copy(
                                                    fits = if (selected) {
                                                        filters.fits + fit
                                                    } else {
                                                        filters.fits - fit
                                                    }
                                                )
                                                Log.d("OutfitDetailsScreen", "🎯 Fit filter changed: $fit, selected: $selected")
                                                filters = newFilters
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
                    HorizontalDivider(
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
                                Log.d("OutfitDetailsScreen", "🔄 Clear button clicked")
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
                                Log.d("OutfitDetailsScreen", "✅ Apply button clicked with filters: $filters")
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                    applyFilters(filters)
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
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectedChange(!isSelected) }
                .padding(top = 8.dp),
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
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Black,
                        uncheckedColor = Color.Black
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PriceRangeFilter(
    minPrice: Float = 0f,
    maxPrice: Float = 5000f,
    priceRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = "Price",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        RangeSlider(
            value = priceRange,
            onValueChange = { range ->
                // Snap both ends to nearest 500
                val snappedStart = (range.start / 500).roundToInt() * 500
                val snappedEnd = (range.endInclusive / 500).roundToInt() * 500

                onRangeChange(snappedStart.toFloat()..snappedEnd.toFloat())
            },
            valueRange = minPrice..maxPrice,
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.Black,
                inactiveTrackColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )



        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "₹${priceRange.start.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "₹${priceRange.endInclusive.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

