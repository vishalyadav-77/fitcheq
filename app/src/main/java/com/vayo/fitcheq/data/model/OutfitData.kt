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
    var isFavorite: Boolean = false
)