package com.alperdursun.expirymate.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.ui.theme.ExpiryMateRadius
import com.alperdursun.expirymate.util.containerColor
import com.alperdursun.expirymate.util.displayNameResId
import com.alperdursun.expirymate.util.icon
import com.alperdursun.expirymate.util.onContainerColor

@Composable
fun CategoryBadge(
    category: ItemCategory,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val containerColor = category.containerColor(darkTheme)
    val contentColor = category.onContainerColor(darkTheme)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ExpiryMateRadius.Small),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(category.displayNameResId),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
