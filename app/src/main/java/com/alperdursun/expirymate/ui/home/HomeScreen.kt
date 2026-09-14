package com.alperdursun.expirymate.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperdursun.expirymate.ExpiryMateApplication
import com.alperdursun.expirymate.R
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.ui.components.CategoryBadge
import com.alperdursun.expirymate.ui.components.ExpiryMateCard
import com.alperdursun.expirymate.ui.components.ExpiryMateSectionHeader
import com.alperdursun.expirymate.ui.components.ExpiryUrgencyBadge
import com.alperdursun.expirymate.ui.theme.DangerContainerDark
import com.alperdursun.expirymate.ui.theme.DangerContainerLight
import com.alperdursun.expirymate.ui.theme.ExpiryMateRadius
import com.alperdursun.expirymate.ui.theme.ExpiryMateSpacing
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
import com.alperdursun.expirymate.util.DateUtils
import com.alperdursun.expirymate.util.containerColor
import com.alperdursun.expirymate.util.icon
import com.alperdursun.expirymate.util.onContainerColor

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAddNewItemClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {},
    viewModel: HomeViewModel = viewModel(
        factory = run {
            val appContainer = (LocalContext.current.applicationContext as ExpiryMateApplication).container
            HomeViewModel.Factory(
                itemRepository = appContainer.itemRepository,
                settingsRepository = appContainer.settingsRepository,
            )
        }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ExpiryMateSpacing.L),
        verticalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.XL)
    ) {
        ExpirationSummarySection(
            expiredCount = uiState.expiredCount,
            thisWeekCount = uiState.thisWeekCount,
            laterCount = uiState.laterCount
        )

        NextToExpireSection(
            nextItem = uiState.nextToExpire,
            onItemClick = onItemClick
        )

        ExpiringSoonSection(
            items = uiState.expiringSoonItems,
            onItemClick = onItemClick
        )
    }

    if (!uiState.hasSeenWelcome && !uiState.isLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(R.string.welcome_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.welcome_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = viewModel::onWelcomeDismissed) {
                    Text(stringResource(R.string.welcome_action))
                }
            }
        )
    }
}

@Composable
private fun ExpirationSummarySection(
    expiredCount: Int,
    thisWeekCount: Int,
    laterCount: Int,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    Column {
        ExpiryMateSectionHeader(title = stringResource(R.string.home_overview))
        Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.S)
        ) {
            SummaryCard(
                title = stringResource(R.string.home_expired),
                count = expiredCount.toString(),
                icon = Icons.Default.ErrorOutline,
                containerColor = if (darkTheme) DangerContainerDark else DangerContainerLight,
                contentColor = if (darkTheme) OnDangerContainerDark else OnDangerContainerLight,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = stringResource(R.string.home_this_week),
                count = thisWeekCount.toString(),
                icon = Icons.Default.Alarm,
                containerColor = if (darkTheme) WarningContainerDark else WarningContainerLight,
                contentColor = if (darkTheme) OnWarningContainerDark else OnWarningContainerLight,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = stringResource(R.string.home_later),
                count = laterCount.toString(),
                icon = Icons.Default.Schedule,
                containerColor = if (darkTheme) SafeContainerDark else SafeContainerLight,
                contentColor = if (darkTheme) OnSafeContainerDark else OnSafeContainerLight,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    count: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    ExpiryMateCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .background(containerColor)
                .padding(ExpiryMateSpacing.M)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NextToExpireSection(
    nextItem: Item?,
    onItemClick: (Long) -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val context = LocalContext.current

    Column {
        ExpiryMateSectionHeader(title = stringResource(R.string.home_next_to_expire))
        Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))

        if (nextItem == null) {
            ExpiryMateCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.home_no_active_products),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(ExpiryMateSpacing.L)
                )
            }
        } else {
            val urgency = DateUtils.getExpiryUrgency(nextItem.expirationDate)
            val relativeText = DateUtils.getRelativeExpiryText(context, nextItem.expirationDate)
            val categoryContainer = nextItem.category.containerColor(darkTheme)
            val categoryOnContainer = nextItem.category.onContainerColor(darkTheme)

            ExpiryMateCard(
                onClick = { onItemClick(nextItem.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ExpiryMateSpacing.L),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(ExpiryMateRadius.Medium))
                            .background(categoryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = nextItem.category.icon,
                            contentDescription = null,
                            tint = categoryOnContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(ExpiryMateSpacing.M))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = nextItem.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(ExpiryMateSpacing.XS))
                        CategoryBadge(category = nextItem.category)
                    }
                    Spacer(modifier = Modifier.width(ExpiryMateSpacing.S))
                    ExpiryUrgencyBadge(
                        urgency = urgency,
                        text = relativeText
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpiringSoonSection(
    items: List<Item>,
    onItemClick: (Long) -> Unit
) {
    Column {
        ExpiryMateSectionHeader(
            title = stringResource(R.string.home_expiring_soon),
            trailingContent = {
                if (items.isNotEmpty()) {
                    val countText = if (items.size == 1) {
                        stringResource(R.string.home_item_count, items.size)
                    } else {
                        stringResource(R.string.home_items_count, items.size)
                    }
                    Text(
                        text = countText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))

        if (items.isEmpty()) {
            ExpiryMateCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.home_no_expiring_soon),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(ExpiryMateSpacing.M)
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.S)
            ) {
                items.forEach { item ->
                    ItemSummaryCard(
                        item = item,
                        onClick = { onItemClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemSummaryCard(
    item: Item,
    onClick: () -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val context = LocalContext.current
    val urgency = DateUtils.getExpiryUrgency(item.expirationDate)
    val relativeText = DateUtils.getRelativeExpiryText(context, item.expirationDate)
    val categoryContainer = item.category.containerColor(darkTheme)
    val categoryOnContainer = item.category.onContainerColor(darkTheme)

    ExpiryMateCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ExpiryMateSpacing.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(ExpiryMateRadius.Small))
                    .background(categoryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.category.icon,
                    contentDescription = null,
                    tint = categoryOnContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(ExpiryMateSpacing.M))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                CategoryBadge(category = item.category)
            }
            Spacer(modifier = Modifier.width(ExpiryMateSpacing.S))
            ExpiryUrgencyBadge(
                urgency = urgency,
                text = relativeText
            )
        }
    }
}
