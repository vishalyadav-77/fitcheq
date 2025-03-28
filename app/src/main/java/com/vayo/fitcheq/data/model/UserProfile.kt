package com.vayo.fitcheq.data.model

data class UserProfile(
    val uId: String = "",
    val name: String = "",
    val gender: String = "", // "Male" or "Female"
    val occupation: String = "", // "College Student", working professional, school student , other
    val ageGroup: AgeGroup = AgeGroup.UNSPECIFIED, // Default value
    val preferPlatform: PreferPlatform = PreferPlatform.moderate, //default value
    val profileCompleted: Boolean = false
)

enum class AgeGroup(val displayName: String) {
    BELOW_18("Below 18"),
    AGE_18_25("18-25"),
    AGE_25_30("25-30"),
    AGE_30_PLUS("30+"),
    UNSPECIFIED("Unspecified") // For cases where the user hasn't selected an age
}
enum class PreferPlatform(val displayName: String) {
    cheap("meesho"),
    moderate("Myntra"),
    expensive("Zara")
}
