package com.vayo.fitcheq.data.model

data class UserProfile(
    val uId: String = "",
    val name: String = "",
    val gender: String = "",
    val occupation: String = "",
    val ageGroup: AgeGroup = AgeGroup.UNSPECIFIED, // Default value
    val preferPlatform: PreferPlatform = PreferPlatform.moderate,
    val profileCompleted: Boolean = false,
    val height: HeightGroup = HeightGroup.average,
    val bodyType: BodyType = BodyType.average
)

enum class AgeGroup(val displayName: String) {
    BELOW_18("<18"),
    AGE_18_25("18-25"),
    AGE_25_30("25-30"),
    AGE_30_PLUS("30+"),
    UNSPECIFIED("Unspecified") // For cases where the user hasn't selected an age
}
enum class PreferPlatform(val displayName: String) {
    cheap("Meesho Flipkart Amazon "),
    moderate("Myntra AJIO FREAKINS Shein"),
    expensive("Zara H&M ONLY Snitch ")
}

enum class HeightGroup(val displayName: String){
    short("Below 5'4\""),
    average("5'4\" - 5'7\""),
    tall("5'8\" - 5'11\""),
    very_tall("6'0\" and above")
}
enum class BodyType(val displayName: String){
    slim("Slim"),
    athletic("Athletic"),
    average("Average"),
    muscular("Muscular"),
    plus_size("Plus-Size")
}
