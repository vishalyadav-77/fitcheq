package com.vayo.fitcheq.data.model

import kotlinx.serialization.Serializable

// Wrapper for root
@Serializable
data class FitGuideWrapper(
    val guides: List<FitGuide>
)

@Serializable
data class FitGuideCategory(
    val id: String,
    val title: String,
    val summary: String,
    val details: List<String>,
    val proTip: String,
)

@Serializable
data class FitGuide(
    val gender: String,
    val height: HeightGroup,
    val bodyType: BodyType,
    val chips: List<String>,
    val categories: List<FitGuideCategory>
)