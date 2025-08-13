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

val outfitSizeMap = mapOf(
    "tshirt" to listOf("S", "M", "L", "XL"),
    "shirt" to listOf("S", "M", "L", "XL"),
    "jeans" to listOf("28", "30", "32", "34", "36"),
    "shoes" to listOf("6", "7", "8", "9", "10"),
    "jacket" to listOf("S", "M", "L")
)
