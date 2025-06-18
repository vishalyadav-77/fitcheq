package com.vayo.fitcheq.data.model

data class FitsCategory(val emoji: String, val title: String)

val maleoccasionList = listOf(
    FitsCategory("📚", "College"),
    FitsCategory("❤️", "Date"),
    FitsCategory("🤵", "Wedding"),
    FitsCategory("💼", "Office"),
    FitsCategory("⛱️", "Beach"),
    FitsCategory("🏋️", "Gym")
)
val malecategoryList = listOf(
    FitsCategory("👕", "Tshirt"),
    FitsCategory("👕", "Shirt"),
    FitsCategory("👖", "Jeans"),
    FitsCategory("💼", "TrackPants"),
    FitsCategory("🧥", "Jacket"),
    FitsCategory("🧥", "TankTops"),
    FitsCategory("💍️", "Accessories")
)

val femaleoccasionList = listOf(
    FitsCategory("📚", "College"),
    FitsCategory("❤️", "Date"),
    FitsCategory("🌃", "Wedding"),
    FitsCategory("💼", "Office"),
    FitsCategory("🏋️", "Gym")
)

val malefashionList = listOf(
    FitsCategory("🕊️", "Minimalist"),
    FitsCategory("💸", "Old Money"),
    FitsCategory("🌟", "Starboy"),
    FitsCategory("🧢", "Streetwear"),
    FitsCategory("🧛", "Dark Academia"),
    FitsCategory("💿", "Y2K"),
    FitsCategory("🌻", "Soft Boy"),
)