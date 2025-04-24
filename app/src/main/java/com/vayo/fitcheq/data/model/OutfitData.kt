package com.vayo.fitcheq.data.model

data class OutfitData(
    val id: String = "",
    val link: String ="",
    val imageUrl: String = "",
    val title: String = "",
    val price: String = "",
    val website: String = "",
    val gender: String = "",
    val tags: List<String> = emptyList()
)