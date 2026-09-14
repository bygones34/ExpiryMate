package com.alperdursun.expirymate.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.alperdursun.expirymate.ui.theme.ExpiryMateRadius

@Composable
fun ExpiryMateCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(ExpiryMateRadius.Large),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val border = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            content = content
        )
    }
}
