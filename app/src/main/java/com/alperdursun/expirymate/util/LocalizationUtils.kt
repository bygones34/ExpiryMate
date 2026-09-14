package com.alperdursun.expirymate.util

import androidx.annotation.StringRes
import com.alperdursun.expirymate.R
import com.alperdursun.expirymate.domain.model.AppThemeMode
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.domain.model.ItemStatus

val ItemCategory.displayNameResId: Int
    @StringRes
    get() = when (this) {
        ItemCategory.FOOD -> R.string.category_food
        ItemCategory.MEDICINE -> R.string.category_medicine
        ItemCategory.COSMETICS -> R.string.category_cosmetics
        ItemCategory.SUPPLEMENTS -> R.string.category_supplements
        ItemCategory.HOUSEHOLD -> R.string.category_household
        ItemCategory.OTHER -> R.string.category_other
    }

val ItemStatus.displayNameResId: Int
    @StringRes
    get() = when (this) {
        ItemStatus.ACTIVE -> R.string.status_active
        ItemStatus.USED -> R.string.status_used
        ItemStatus.DISCARDED -> R.string.status_discarded
    }

val AppThemeMode.displayNameResId: Int
    @StringRes
    get() = when (this) {
        AppThemeMode.SYSTEM -> R.string.theme_system
        AppThemeMode.LIGHT -> R.string.theme_light
        AppThemeMode.DARK -> R.string.theme_dark
    }
