package com.alperdursun.expirymate.domain.model

enum class ItemCategory(val displayName: String) {
    FOOD("Food"),
    MEDICINE("Medicine"),
    COSMETICS("Cosmetics"),
    SUPPLEMENTS("Supplements"),
    HOUSEHOLD("Household"),
    OTHER("Other");

    companion object {
        fun fromDisplayName(name: String): ItemCategory {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}
