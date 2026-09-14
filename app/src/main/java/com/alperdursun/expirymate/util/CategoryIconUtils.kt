package com.alperdursun.expirymate.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.ui.theme.CategoryCosmeticsDark
import com.alperdursun.expirymate.ui.theme.CategoryCosmeticsLight
import com.alperdursun.expirymate.ui.theme.CategoryFoodDark
import com.alperdursun.expirymate.ui.theme.CategoryFoodLight
import com.alperdursun.expirymate.ui.theme.CategoryHouseholdDark
import com.alperdursun.expirymate.ui.theme.CategoryHouseholdLight
import com.alperdursun.expirymate.ui.theme.CategoryMedicineDark
import com.alperdursun.expirymate.ui.theme.CategoryMedicineLight
import com.alperdursun.expirymate.ui.theme.CategoryOtherDark
import com.alperdursun.expirymate.ui.theme.CategoryOtherLight
import com.alperdursun.expirymate.ui.theme.CategorySupplementsDark
import com.alperdursun.expirymate.ui.theme.CategorySupplementsLight
import com.alperdursun.expirymate.ui.theme.OnCategoryCosmeticsDark
import com.alperdursun.expirymate.ui.theme.OnCategoryCosmeticsLight
import com.alperdursun.expirymate.ui.theme.OnCategoryFoodDark
import com.alperdursun.expirymate.ui.theme.OnCategoryFoodLight
import com.alperdursun.expirymate.ui.theme.OnCategoryHouseholdDark
import com.alperdursun.expirymate.ui.theme.OnCategoryHouseholdLight
import com.alperdursun.expirymate.ui.theme.OnCategoryMedicineDark
import com.alperdursun.expirymate.ui.theme.OnCategoryMedicineLight
import com.alperdursun.expirymate.ui.theme.OnCategoryOtherDark
import com.alperdursun.expirymate.ui.theme.OnCategoryOtherLight
import com.alperdursun.expirymate.ui.theme.OnCategorySupplementsDark
import com.alperdursun.expirymate.ui.theme.OnCategorySupplementsLight

val ItemCategory.icon: ImageVector
    get() = when (this) {
        ItemCategory.FOOD -> Icons.Default.Restaurant
        ItemCategory.MEDICINE -> Icons.Default.Medication
        ItemCategory.COSMETICS -> Icons.Default.Spa
        ItemCategory.SUPPLEMENTS -> Icons.Default.HealthAndSafety
        ItemCategory.HOUSEHOLD -> Icons.Default.CleaningServices
        ItemCategory.OTHER -> Icons.Default.Inventory2
    }

fun ItemCategory.containerColor(darkTheme: Boolean): Color {
    return if (darkTheme) {
        when (this) {
            ItemCategory.FOOD -> CategoryFoodDark
            ItemCategory.MEDICINE -> CategoryMedicineDark
            ItemCategory.COSMETICS -> CategoryCosmeticsDark
            ItemCategory.SUPPLEMENTS -> CategorySupplementsDark
            ItemCategory.HOUSEHOLD -> CategoryHouseholdDark
            ItemCategory.OTHER -> CategoryOtherDark
        }
    } else {
        when (this) {
            ItemCategory.FOOD -> CategoryFoodLight
            ItemCategory.MEDICINE -> CategoryMedicineLight
            ItemCategory.COSMETICS -> CategoryCosmeticsLight
            ItemCategory.SUPPLEMENTS -> CategorySupplementsLight
            ItemCategory.HOUSEHOLD -> CategoryHouseholdLight
            ItemCategory.OTHER -> CategoryOtherLight
        }
    }
}

fun ItemCategory.onContainerColor(darkTheme: Boolean): Color {
    return if (darkTheme) {
        when (this) {
            ItemCategory.FOOD -> OnCategoryFoodDark
            ItemCategory.MEDICINE -> OnCategoryMedicineDark
            ItemCategory.COSMETICS -> OnCategoryCosmeticsDark
            ItemCategory.SUPPLEMENTS -> OnCategorySupplementsDark
            ItemCategory.HOUSEHOLD -> OnCategoryHouseholdDark
            ItemCategory.OTHER -> OnCategoryOtherDark
        }
    } else {
        when (this) {
            ItemCategory.FOOD -> OnCategoryFoodLight
            ItemCategory.MEDICINE -> OnCategoryMedicineLight
            ItemCategory.COSMETICS -> OnCategoryCosmeticsLight
            ItemCategory.SUPPLEMENTS -> OnCategorySupplementsLight
            ItemCategory.HOUSEHOLD -> OnCategoryHouseholdLight
            ItemCategory.OTHER -> OnCategoryOtherLight
        }
    }
}
