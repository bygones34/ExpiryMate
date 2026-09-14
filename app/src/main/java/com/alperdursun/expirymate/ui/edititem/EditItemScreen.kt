package com.alperdursun.expirymate.ui.edititem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperdursun.expirymate.ExpiryMateApplication
import com.alperdursun.expirymate.R
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.ui.components.FormSectionCard
import com.alperdursun.expirymate.ui.components.PrimaryActionButton
import com.alperdursun.expirymate.ui.theme.ExpiryMateRadius
import com.alperdursun.expirymate.ui.theme.ExpiryMateSpacing
import com.alperdursun.expirymate.util.DateUtils
import com.alperdursun.expirymate.util.displayNameResId
import com.alperdursun.expirymate.util.icon
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val reminderOptionDays = listOf(0, 1, 3, 7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    itemId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditItemViewModel = viewModel(
        factory = run {
            val appContainer = (LocalContext.current.applicationContext as ExpiryMateApplication).container
            EditItemViewModel.Factory(
                itemId = itemId,
                repository = appContainer.itemRepository,
                settingsRepository = appContainer.settingsRepository,
                reminderScheduler = appContainer.reminderScheduler,
            )
        }
    )
) {
    val focusManager = LocalFocusManager.current
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    var showDatePickerDialog by remember { mutableStateOf(value = false) }

    LaunchedEffect(Unit) {
        viewModel.saveSuccessEvent.collectLatest {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_item_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_navigate_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ExpiryMateSpacing.L, vertical = ExpiryMateSpacing.M),
            verticalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.M)
        ) {
            // Header Info Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ExpiryMateRadius.Medium),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = ExpiryMateSpacing.M, vertical = ExpiryMateSpacing.S),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✏️", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.width(ExpiryMateSpacing.S))
                    Text(
                        text = stringResource(R.string.edit_item_info),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Section 1: Required Fields
            FormSectionCard(title = stringResource(R.string.section_primary_details)) {
                // Product Name Field
                OutlinedTextField(
                    value = formState.productName,
                    onValueChange = viewModel::onNameChanged,
                    label = { Text(stringResource(R.string.label_product_name), fontWeight = FontWeight.Bold) },
                    placeholder = { Text(stringResource(R.string.placeholder_product_name)) },
                    isError = formState.nameError != null,
                    supportingText = formState.nameError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(ExpiryMateRadius.Medium),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    )
                )

                // Expiration Date Field
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = DateUtils.formatDate(formState.expirationDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_expiration_date), fontWeight = FontWeight.Bold) },
                        placeholder = { Text(stringResource(R.string.placeholder_expiration_date)) },
                        isError = formState.dateError != null,
                        supportingText = formState.dateError?.let { { Text(it) } },
                        trailingIcon = {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                showDatePickerDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = stringResource(R.string.action_select_date)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(ExpiryMateRadius.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                focusManager.clearFocus()
                                showDatePickerDialog = true
                            }
                    )
                }
            }

            // Section 2: Category & Reminder
            FormSectionCard(
                title = stringResource(R.string.section_category_reminder),
                titleColor = MaterialTheme.colorScheme.secondary
            ) {
                // Category Selection
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(ExpiryMateSpacing.XS))
                        Text(
                            text = stringResource(R.string.label_category),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.S)
                    ) {
                        ItemCategory.entries.forEach { category ->
                            FilterChip(
                                selected = category == formState.category,
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.onCategorySelected(category)
                                },
                                label = { Text(stringResource(category.displayNameResId)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(ExpiryMateRadius.Medium),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }

                // Reminder Selection
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(ExpiryMateSpacing.XS))
                        Text(
                            text = stringResource(R.string.label_reminder_notice),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(ExpiryMateSpacing.S)
                    ) {
                        reminderOptionDays.forEach { days ->
                            val labelText = when (days) {
                                0 -> stringResource(R.string.reminder_same_day)
                                1 -> stringResource(R.string.reminder_1_day_before)
                                3 -> stringResource(R.string.reminder_3_days_before)
                                7 -> stringResource(R.string.reminder_7_days_before)
                                else -> stringResource(R.string.reminder_days_before, days)
                            }
                            FilterChip(
                                selected = days == formState.reminderDaysBefore,
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.onReminderDaysSelected(days)
                                },
                                label = { Text(labelText) },
                                shape = RoundedCornerShape(ExpiryMateRadius.Medium)
                            )
                        }
                    }
                }

                // Optional Notes Field
                OutlinedTextField(
                    value = formState.notes,
                    onValueChange = viewModel::onNotesChanged,
                    label = { Text(stringResource(R.string.label_notes)) },
                    placeholder = { Text(stringResource(R.string.placeholder_notes)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(ExpiryMateRadius.Medium),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    )
                )
            }

            // Save Changes Button CTA
            PrimaryActionButton(
                text = if (formState.isSaving) stringResource(R.string.btn_saving_changes) else stringResource(R.string.btn_save_changes),
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveItem()
                },
                enabled = !formState.isSaving,
                isLoading = formState.isSaving,
                icon = Icons.Default.Check
            )

            Spacer(modifier = Modifier.height(ExpiryMateSpacing.S))
        }
    }

    // Date Picker Dialog
    if (showDatePickerDialog) {
        val initialSelectedMillis = formState.expirationDate
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
            ?: LocalDate.now()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val selectedLocalDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            viewModel.onDateSelected(selectedLocalDate)
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
