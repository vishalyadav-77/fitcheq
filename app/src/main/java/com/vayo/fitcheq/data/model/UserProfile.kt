package com.vayo.fitcheq.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class UserProfile(
    val uId: String = "",
    val name: String = "",
    val phone: String= "",
    val email: String= "",
    val gender: String = "",
    val occupation: String = "",
    val ageGroup: AgeGroup = AgeGroup.UNSPECIFIED, // Default value
    val preferPlatform: PreferPlatform = PreferPlatform.moderate,
    val profileCompleted: Boolean = false,
    val height: HeightGroup = HeightGroup.average,
    val bodyType: BodyType = BodyType.average,
    val dateJoined: Long = System.currentTimeMillis(), // store as timestamp
    val lastActive: Long = System.currentTimeMillis()  // update on login/logout
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

@Serializable
enum class HeightGroup(val displayName: String) {
    @SerialName("short")
    short("Below 5'4\""),

    @SerialName("average")
    average("5'4\" - 5'7\""),

    @SerialName("tall")
    tall("5'8\" - 5'11\""),

    @SerialName("very_tall")
    very_tall("6'0\" and above")
}

@Serializable
enum class BodyType(val displayName: String) {
    @SerialName("slim")
    slim("Slim"),

    @SerialName("athletic")
    athletic("Athletic"),

    @SerialName("average")
    average("Average"),

    @SerialName("muscular")
    muscular("Muscular"),

    @SerialName("plus_size")
    plus_size("Plus-Size")
}

