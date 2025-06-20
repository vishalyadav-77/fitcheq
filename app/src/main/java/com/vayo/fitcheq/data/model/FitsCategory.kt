package com.vayo.fitcheq.data.model

data class FitsCategory( val emoji: String? = null, val imageUrl: String? = null, val title: String)

val maleoccasionList = listOf(
    FitsCategory(title = "College", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/office_male.webp"),
    FitsCategory(title = "Date", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/date_male.webp"),
    FitsCategory(title = "Beach", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/beach_male.webp"),
    FitsCategory(title = "Gym", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/gym_male.webp"),
    FitsCategory(title = "Wedding", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/date_male.webp"),
    FitsCategory(title = "Office", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/office_male.webp"),
)
val malecategoryList = listOf(
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/tshirt_man.webp", title =  "Tshirt"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/shirt_male.webp", title = "Shirt"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/jeans2male.webp", title = "Jeans"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/trackpants.webp", title = "TrackPants"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/jacket_male.webp", title = "Jacket"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/tanktop_male.webp", title = "TankTops"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/male_accessories.webp", title = "Accessories")
)

val femaleoccasionList = listOf(
    FitsCategory(emoji = "📚", title = "College"),
    FitsCategory(emoji = "❤️", title =  "Date"),
    FitsCategory(emoji = "🤵", title = "Wedding"),
    FitsCategory(emoji = "💼", title =  "Office"),
    FitsCategory(emoji = "⛱️", title =  "Beach"),
    FitsCategory(emoji = "🏋️", title = "Gym")
)

val malefashionList = listOf(
    FitsCategory(emoji = "🕊️", title = "Minimalist"),
    FitsCategory(emoji = "💸", title = "Old Money"),
    FitsCategory(emoji = "🌟", title = "Starboy"),
    FitsCategory(emoji = "🧢", title = "Streetwear"),
    FitsCategory(emoji = "🧛", title = "Dark Academia"),
    FitsCategory(emoji = "💿", title = "Y2K"),
    FitsCategory(emoji = "🌻", title = "Soft Boy"),
)