package com.vayo.fitcheq.data.model

data class FitsCategory( val emoji: String? = null, val imageUrl: String? = null, val title: String)

val maleoccasionList = listOf(
    FitsCategory(title = "College", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/college_c.webp"),
    FitsCategory(title = "Office", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/office_c.webp"),
    FitsCategory(title = "Date", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/date_c.webp"),
    FitsCategory(title = "Beach", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/beach_c.webp"),
    FitsCategory(title = "Gym", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/gym_c.webp"),
    FitsCategory(title = "Wedding", imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/occasion-images/party_c.webp"),
)
val malecategoryList = listOf(
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/tshirt_man.webp", title =  "Tshirt"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/gem_shirt.webp", title = "Shirt"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/gem_jeans2.webp", title = "Jeans"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/trackpants.webp", title = "TrackPants"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/gem_jacket2.webp", title = "Jacket"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/tanktop_male.webp", title = "TankTops"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/categories-assets/gem_acc2.webp", title = "Accessories")
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
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/minimalist_final.webp", title = "Minimalist"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/oldmoney_final.webp", title = "Old Money"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/streetwear_final.webp", title = "Streetwear"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/starboy_final.webp", title = "Starboy"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/softboy_final.webp", title = "Soft Boy"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/y2k_final.webp", title = "Y2K"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/byfashion/dark2_final.webp", title = "Dark Academia")
)

val maleSeasonList = listOf(
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/season/summer_c2.webp", title = "Summer"),
    FitsCategory(imageUrl = "https://cdn.jsdelivr.net/gh/vishalyadav-77/fitcheq-assests/season/winter_c2.webp", title = "Winter")
)