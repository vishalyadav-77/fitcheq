package com.vayo.fitcheq.data.model

@kotlinx.serialization.Serializable
data class OutfitData(
    val id: String = "",
    val link: String ="",
    val imageUrl: String = "",
    val imageUrls: List<String> = emptyList(),
    val title: String = "",
    val price: String = "",
    val website: String = "",
    val gender: String = "",
    val tags: List<String> = emptyList(),
    var isFavorite: Boolean = false,
    val category: String = "",
    val type: String = "",
    val color: String = "",
    val style: List<String> = emptyList(),
    val occasion: List<String> = emptyList(),
    val season: List<String> = emptyList(),
    val fit: String = "",
    val material: String = "",

    val sizes: List<String> = emptyList()
)

data class Filters(
    val categories: Set<String> = emptySet(),
    val websites: Set<String> = emptySet(),
    val colors: Set<String> = emptySet(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..10000f,
    val type: String? = null,
    val fits: Set<String> = emptySet(),
)

// All possible filters fetched from Firestore
data class AvailableFilters(
    var categories: Set<String> = emptySet(),
    var brands: Set<String> = emptySet(),
    var colors: Set<String> = emptySet(),
    var fits: Set<String> = emptySet(),
    var types: Set<String> = emptySet(),
)

// What the user has currently selected
data class AppliedFilters(
    val categories: Set<String> = emptySet(),
    val brands: Set<String> = emptySet(),
    val colors: Set<String> = emptySet(),
    val fits: Set<String> = emptySet(),
    val type: String? = null,
    val priceRange: ClosedFloatingPointRange<Float> = 0f..10000f
)

val outfitSizeMap = mapOf(
    "tshirt" to listOf("S", "M", "L", "XL"),
    "shirt" to listOf("S", "M", "L", "XL"),
    "jeans" to listOf("28", "30", "32", "34", "36"),
    "trackpants" to listOf("S", "M", "L", "XL"),
    "tanktops" to listOf("S", "M", "L", "XL"),
    "shoes" to listOf("6", "7", "8", "9", "10"),
    "jacket" to listOf("XS","S", "M", "L", "XL"),
    "top" to listOf("S", "M", "L", "XL"),
    "saree" to listOf("S", "M", "L", "XL"),
    "dress" to listOf("S", "M", "L", "XL"),
    "skirt" to listOf("S", "M", "L", "XL"),
    "kurti" to listOf("S", "M", "L", "XL"),
    "trousers" to listOf("S", "M", "L", "XL"),
)
