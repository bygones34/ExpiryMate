package com.alperdursun.expirymate.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alperdursun.expirymate.ui.theme.DangerContainerDark
import com.alperdursun.expirymate.ui.theme.DangerContainerLight
import com.alperdursun.expirymate.ui.theme.ExpiryMateRadius
import com.alperdursun.expirymate.ui.theme.OnDangerContainerDark
import com.alperdursun.expirymate.ui.theme.OnDangerContainerLight
import com.alperdursun.expirymate.ui.theme.OnSafeContainerDark
import com.alperdursun.expirymate.ui.theme.OnSafeContainerLight
import com.alperdursun.expirymate.ui.theme.OnWarningContainerDark
import com.alperdursun.expirymate.ui.theme.OnWarningContainerLight
import com.alperdursun.expirymate.ui.theme.SafeContainerDark
import com.alperdursun.expirymate.ui.theme.SafeContainerLight
import com.alperdursun.expirymate.ui.theme.WarningContainerDark
import com.alperdursun.expirymate.ui.theme.WarningContainerLight
import com.alperdursun.expirymate.util.ExpiryUrgency

@Composable
fun ExpiryUrgencyBadge(
    urgency: ExpiryUrgency,
    text: String,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val (containerColor, contentColor) = when (urgency) {
        ExpiryUrgency.EXPIRED -> Pair(
            if (darkTheme) DangerContainerDark else DangerContainerLight,
            if (darkTheme) OnDangerContainerDark else OnDangerContainerLight
        )
        ExpiryUrgency.WARNING -> Pair(
            if (darkTheme) WarningContainerDark else WarningContainerLight,
            if (darkTheme) OnWarningContainerDark else OnWarningContainerLight
        )
        ExpiryUrgency.SAFE -> Pair(
            if (darkTheme) SafeContainerDark else SafeContainerLight,
            if (darkTheme) OnSafeContainerDark else OnSafeContainerLight
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ExpiryMateRadius.Small),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
