package com.alperdursun.expirymate.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperdursun.expirymate.ExpiryMateApplication
import com.alperdursun.expirymate.R
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.ui.components.ExpiryMateCard
import com.alperdursun.expirymate.ui.components.ExpiryMateSectionHeader
import com.alperdursun.expirymate.ui.components.ExpiryUrgencyBadge
import com.alperdursun.expirymate.ui.theme.ExpiryMateRadius
import com.alperdursun.expirymate.ui.theme.ExpiryMateSpacing
import com.alperdursun.expirymate.util.DateUtils
import com.alperdursun.expirymate.util.containerColor
import com.alperdursun.expirymate.util.displayNameResId
import com.alperdursun.expirymate.util.icon
import com.alperdursun.expirymate.util.onContainerColor

@Composable
fun ItemsScreen(
    modifier: Modifier = Modifier,
    onItemClick: (Long) -> Unit = {},
    viewModel: ItemsViewModel = viewModel(
        factory = run {
            val appContainer = (LocalContext.current.applicationContext as ExpiryMateApplication).container
            ItemsViewModel.Factory(
                repository = appContainer.itemRepository,
                reminderScheduler = appContainer.reminderScheduler,
            )
        }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = ExpiryMateSpacing.L)
    ) {
        Spacer(modifier = Modifier.height(ExpiryMateSpacing.L))

        // Title Header Area
        ExpiryMateSectionHeader(
            title = stringResource(R.string.items_title),
            trailingContent = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.items_active_count, uiState.totalActiveCount),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = ExpiryMateSpacing.M, vertical = ExpiryMateSpacing.XS)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(ExpiryMateSpacing.L))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.items_search_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.items_clear_search)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(ExpiryMateRadius.ExtraLarge),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(ExpiryMateSpacing.M))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.S)
        ) {
            val isAllSelected = uiState.selectedCategory == null
            FilterChip(
                selected = isAllSelected,
                onClick = { viewModel.onCategorySelected(null) },
                label = { Text(stringResource(R.string.items_filter_all)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(ExpiryMateRadius.Medium)
            )

            ItemCategory.entries.forEach { category ->
                val isSelected = uiState.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text(stringResource(category.displayNameResId)) },
                    leadingIcon = {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(ExpiryMateRadius.Medium)
                )
            }
        }

        Spacer(modifier = Modifier.height(ExpiryMateSpacing.M))

        // Items List or Empty State
        if (uiState.items.isEmpty()) {
            EmptyItemsState(
                isFiltered = uiState.selectedCategory != null || uiState.searchQuery.isNotEmpty(),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.S)
            ) {
                items(
                    items = uiState.items,
                    key = { it.id }
                ) { item ->
                    ItemCard(
                        item = item,
                        onClick = { onItemClick(item.id) },
                        onMarkAsUsed = { viewModel.markAsUsed(item.id) },
                        onMarkAsDiscarded = { viewModel.markAsDiscarded(item.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(ExpiryMateSpacing.L))
                }
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    onMarkAsUsed: () -> Unit,
    onMarkAsDiscarded: () -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
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
                    .size(42.dp)
                    .clip(RoundedCornerShape(ExpiryMateRadius.Medium))
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(item.category.displayNameResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                val dateFormatted = DateUtils.formatDate(item.expirationDate)
                val detailsText = if (!item.notes.isNullOrEmpty()) {
                    "${item.notes} · $dateFormatted"
                } else {
                    dateFormatted
                }
                Text(
                    text = detailsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(ExpiryMateSpacing.S))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExpiryUrgencyBadge(
                    urgency = urgency,
                    text = relativeText
                )
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.items_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.items_action_mark_used)) },
                            onClick = {
                                showMenu = false
                                onMarkAsUsed()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.items_action_mark_discarded)) },
                            onClick = {
                                showMenu = false
                                onMarkAsDiscarded()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyItemsState(
    isFiltered: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(ExpiryMateSpacing.XXL)
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(ExpiryMateSpacing.L))
            Text(
                text = if (isFiltered) stringResource(R.string.items_empty_filtered_title) else stringResource(R.string.items_empty_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))
            Text(
                text = if (isFiltered) stringResource(R.string.items_empty_filtered_description) else stringResource(R.string.items_empty_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
