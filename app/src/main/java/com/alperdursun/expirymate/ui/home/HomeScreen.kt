package com.alperdursun.expirymate.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperdursun.expirymate.ExpiryMateApplication
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.util.DateUtils

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
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

        QuickAddCard(onAddNewItemClick = onAddNewItemClick)
    }

    if (!uiState.hasSeenWelcome && !uiState.isLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Welcome to ExpiryMate 👋",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Track expiration dates. Get reminded. Waste less.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = viewModel::onWelcomeDismissed) {
                    Text("Get Started")
                }
            }
        )
    }
}

@Composable
private fun ExpirationSummarySection(
    expiredCount: Int,
    thisWeekCount: Int,
    laterCount: Int
) {
    Column {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryCard(
                title = "Expired",
                count = expiredCount.toString(),
                icon = Icons.Default.ErrorOutline,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "This Week",
                count = thisWeekCount.toString(),
                icon = Icons.Default.Alarm,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Later",
                count = laterCount.toString(),
                icon = Icons.Default.Schedule,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun NextToExpireSection(
    nextItem: Item?,
    onItemClick: (Long) -> Unit
) {
    Column {
        Text(
            text = "Next to Expire",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (nextItem == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "No active products tracked yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            val isExpired = DateUtils.isExpired(nextItem.expirationDate)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(nextItem.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isExpired) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isExpired) MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = nextItem.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Category: ${nextItem.category.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isExpired) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = DateUtils.getRelativeExpiryText(nextItem.expirationDate),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpired) MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expiring Soon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (items.isNotEmpty()) {
                Text(
                    text = "${items.size} item${if (items.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (items.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "No items expiring soon.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
    onClick: () -> Unit
) {
    val isExpired = DateUtils.isExpired(item.expirationDate)
    val urgencyColor = when {
        isExpired -> MaterialTheme.colorScheme.error
        DateUtils.isThisWeek(item.expirationDate) -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(urgencyColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                text = DateUtils.getRelativeExpiryText(item.expirationDate),
                style = MaterialTheme.typography.bodySmall,
                color = if (isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = if (isExpired) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun QuickAddCard(
    onAddNewItemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keep your pantry and medicine cabinet organized",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAddNewItemClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add Product Item")
            }
        }
    }
}
