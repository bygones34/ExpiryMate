package com.alperdursun.expirymate.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector
import com.alperdursun.expirymate.domain.model.ItemCategory

val ItemCategory.icon: ImageVector
    get() = when (this) {
        ItemCategory.FOOD -> Icons.Default.Restaurant
        ItemCategory.MEDICINE -> Icons.Default.Medication
        ItemCategory.COSMETICS -> Icons.Default.Spa
        ItemCategory.SUPPLEMENTS -> Icons.Default.HealthAndSafety
        ItemCategory.HOUSEHOLD -> Icons.Default.CleaningServices
        ItemCategory.OTHER -> Icons.Default.Inventory2
    }
