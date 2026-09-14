package com.alperdursun.expirymate.ui.itemdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.alperdursun.expirymate.domain.model.ItemStatus
import com.alperdursun.expirymate.ui.components.CategoryBadge
import com.alperdursun.expirymate.ui.components.ExpiryMateCard
import com.alperdursun.expirymate.ui.components.ExpiryUrgencyBadge
import com.alperdursun.expirymate.ui.components.PrimaryActionButton
import com.alperdursun.expirymate.ui.components.StatusBadge
import com.alperdursun.expirymate.ui.theme.ExpiryMateRadius
import com.alperdursun.expirymate.ui.theme.ExpiryMateSpacing
import com.alperdursun.expirymate.util.DateUtils
import com.alperdursun.expirymate.util.containerColor
import com.alperdursun.expirymate.util.icon
import com.alperdursun.expirymate.util.onContainerColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Long,
    onNavigateBack: () -> Unit,
    onEditItem: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailViewModel = viewModel(
        factory = run {
            val appContainer = (LocalContext.current.applicationContext as ExpiryMateApplication).container
            ItemDetailViewModel.Factory(
                itemId = itemId,
                repository = appContainer.itemRepository,
                reminderScheduler = appContainer.reminderScheduler,
            )
        }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(value = false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.item_detail_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_navigate_back)
                        )
                    }
                },
                actions = {
                    if (uiState.item != null) {
                        if (uiState.item?.status == ItemStatus.ACTIVE) {
                            IconButton(onClick = { onEditItem(itemId) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.action_edit_item)
                                )
                            }
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_delete_item),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    // Handled gracefully via initial state
                }
                uiState.isNotFound || (uiState.item == null) -> {
                    ItemNotFoundState(onNavigateBack = onNavigateBack)
                }
                else -> {
                    val item = uiState.item!!
                    ItemDetailContent(
                        item = item,
                        onMarkAsUsed = viewModel::markAsUsed,
                        onMarkAsDiscarded = viewModel::markAsDiscarded
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && uiState.item != null) {
        val item = uiState.item!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(R.string.dialog_delete_title)) },
            text = { Text(stringResource(R.string.dialog_delete_body, item.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteItem(onDeleted = onNavigateBack)
                    }
                ) {
                    Text(stringResource(R.string.btn_delete), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun ItemDetailContent(
    item: Item,
    onMarkAsUsed: () -> Unit,
    onMarkAsDiscarded: () -> Unit,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val context = LocalContext.current
    val categoryContainer = item.category.containerColor(darkTheme)
    val categoryOnContainer = item.category.onContainerColor(darkTheme)
    val urgency = DateUtils.getExpiryUrgency(item.expirationDate)
    val relativeText = DateUtils.getRelativeExpiryText(context, item.expirationDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ExpiryMateSpacing.L, vertical = ExpiryMateSpacing.M),
        verticalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.M)
    ) {
        // Header Card
        ExpiryMateCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(ExpiryMateSpacing.M)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(ExpiryMateRadius.Medium))
                            .background(categoryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.category.icon,
                            contentDescription = null,
                            tint = categoryOnContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(ExpiryMateSpacing.M))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(ExpiryMateSpacing.XS))
                        Row(horizontalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.S)) {
                            CategoryBadge(category = item.category)
                            StatusBadge(status = item.status)
                        }
                    }
                }
            }
        }

        // Expiration & Reminder Card
        DetailSectionCard(title = stringResource(R.string.item_detail_expiration_reminder)) {
            DetailInfoRow(
                icon = Icons.Default.CalendarMonth,
                label = stringResource(R.string.label_expiration_date),
                value = DateUtils.formatDate(item.expirationDate),
                trailingBadge = {
                    ExpiryUrgencyBadge(
                        urgency = urgency,
                        text = relativeText
                    )
                }
            )
            val reminderNoticeText = when (item.reminderDaysBefore) {
                0 -> stringResource(R.string.reminder_same_day)
                1 -> stringResource(R.string.reminder_1_day_before)
                3 -> stringResource(R.string.reminder_3_days_before)
                7 -> stringResource(R.string.reminder_7_days_before)
                else -> stringResource(R.string.reminder_days_before, item.reminderDaysBefore)
            }
            DetailInfoRow(
                icon = Icons.Default.Notifications,
                label = stringResource(R.string.label_reminder_notice),
                value = reminderNoticeText
            )
        }

        // Notes Card (if present)
        if (!item.notes.isNullOrEmpty()) {
            DetailSectionCard(title = stringResource(R.string.item_detail_notes)) {
                DetailInfoRow(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    label = stringResource(R.string.item_detail_additional_notes),
                    value = item.notes
                )
            }
        }

        // Action Buttons (if ACTIVE)
        if (item.status == ItemStatus.ACTIVE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.M)
            ) {
                PrimaryActionButton(
                    text = stringResource(R.string.items_action_mark_used),
                    onClick = onMarkAsUsed,
                    icon = Icons.Default.Check,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = onMarkAsDiscarded,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(ExpiryMateRadius.Medium),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.items_action_mark_discarded),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ExpiryMateCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(ExpiryMateSpacing.M),
            verticalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.M)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    trailingBadge: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(ExpiryMateSpacing.M))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (trailingBadge != null) {
            Spacer(modifier = Modifier.width(ExpiryMateSpacing.S))
            trailingBadge()
        }
    }
}

@Composable
private fun ItemNotFoundState(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(ExpiryMateSpacing.XXL),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(ExpiryMateSpacing.L))
            Text(
                text = stringResource(R.string.item_detail_not_found_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))
            Text(
                text = stringResource(R.string.item_detail_not_found_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(ExpiryMateSpacing.L))
            PrimaryActionButton(
                text = stringResource(R.string.btn_go_back),
                onClick = onNavigateBack
            )
        }
    }
}
