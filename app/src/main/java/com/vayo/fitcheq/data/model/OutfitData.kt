package com.vayo.fitcheq.data.model

data class OutfitData(
    val imageUrl: String = "",
    val title: String = "",
    val price: String = "",
    val gender: String,
    val tags: List<String> = emptyList()
)