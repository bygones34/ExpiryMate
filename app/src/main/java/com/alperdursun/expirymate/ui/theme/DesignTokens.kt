package com.alperdursun.expirymate.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ExpiryMateRadius {
    val Small: Dp = 8.dp
    val Medium: Dp = 12.dp
    val Large: Dp = 16.dp
    val ExtraLarge: Dp = 24.dp
}

val ExpiryMateShapes = Shapes(
    small = RoundedCornerShape(ExpiryMateRadius.Small),
    medium = RoundedCornerShape(ExpiryMateRadius.Medium),
    large = RoundedCornerShape(ExpiryMateRadius.Large),
    extraLarge = RoundedCornerShape(ExpiryMateRadius.ExtraLarge)
)

object ExpiryMateSpacing {
    val XS: Dp = 4.dp
    val S: Dp = 8.dp
    val M: Dp = 12.dp
    val L: Dp = 16.dp
    val XL: Dp = 24.dp
    val XXL: Dp = 32.dp
}
