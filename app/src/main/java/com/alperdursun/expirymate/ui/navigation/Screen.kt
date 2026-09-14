package com.alperdursun.expirymate.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.alperdursun.expirymate.R

sealed class Screen(
    val route: String,
    @param:StringRes val titleResId: Int,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
) {
    data object Home : Screen(
        route = "home",
        titleResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Items : Screen(
        route = "items",
        titleResId = R.string.nav_items,
        selectedIcon = Icons.Filled.Inventory2,
        unselectedIcon = Icons.Outlined.Inventory2
    )

    data object History : Screen(
        route = "history",
        titleResId = R.string.nav_history,
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )

    data object Settings : Screen(
        route = "settings",
        titleResId = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    data object AddItem : Screen(
        route = "add_item",
        titleResId = R.string.add_item_title
    )

    data object ItemDetail : Screen(
        route = "item/{itemId}",
        titleResId = R.string.item_detail_title
    ) {
        fun createRoute(itemId: Long): String = "item/$itemId"
    }

    data object EditItem : Screen(
        route = "item/{itemId}/edit",
        titleResId = R.string.edit_item_title
    ) {
        fun createRoute(itemId: Long): String = "item/$itemId/edit"
    }

    companion object {
        val bottomNavItems = listOf(Home, Items, History, Settings)
    }
}
