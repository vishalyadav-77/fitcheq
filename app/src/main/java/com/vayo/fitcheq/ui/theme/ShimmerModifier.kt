package com.vayo.fitcheq.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.modernShimmer(
    isLoading: Boolean = true,
    cornerRadius: Dp = 0.dp,
    shimmerWidth: Float = 0.2f, // fraction of the card width
    shimmerAngle: Float = 20f,  // shimmer diagonal angle in degrees
    durationMillis: Int = 1200
): Modifier = composed {
    if (!isLoading) return@composed this

    val transition = rememberInfiniteTransition(label = "shimmer")

    // Animate shimmer progress across the card (0f..1f)
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    ).value

    this
        .clip(RoundedCornerShape(cornerRadius))
        .drawWithContent {
            drawContent() // Optional → if you want shimmer to overlay existing content

            val width = size.width
            val height = size.height

            // Width of shimmer band relative to card size
            val bandWidth = shimmerWidth * width

            // Convert angle to radians
            val angleRad = Math.toRadians(shimmerAngle.toDouble())

            // Movement distance (diagonal length of card)
            val diagonal = kotlin.math.sqrt(width * width + height * height)

            // Current offset of shimmer
            val offsetX = (progress * (diagonal + bandWidth)) - bandWidth
            val offsetY = kotlin.math.tan(angleRad) * offsetX

            // Gradient colors
            val brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.6f),
                    Color.Gray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 0.6f)
                ),
                start = Offset(offsetX.toFloat(), offsetY.toFloat()),
                end = Offset(offsetX.toFloat() + bandWidth, offsetY.toFloat() + bandWidth)
            )

            drawRoundRect(
                brush = brush,
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                size = size
            )
        }
}
