package com.alperdursun.expirymate.ui.additem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperdursun.expirymate.ExpiryMateApplication
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.util.DateUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val reminderOptions = listOf(
    0 to "Same day",
    1 to "1 day before",
    3 to "3 days before",
    7 to "7 days before",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddItemViewModel = viewModel(
        factory = run {
            val appContainer = (LocalContext.current.applicationContext as ExpiryMateApplication).container
            AddItemViewModel.Factory(
                repository = appContainer.itemRepository,
                settingsRepository = appContainer.settingsRepository,
            )
        }
    )
) {
    val context = LocalContext.current
    val appContainer = remember { (context.applicationContext as ExpiryMateApplication).container }
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    var showDatePickerDialog by remember { mutableStateOf(value = false) }

    var pendingSavedItem by remember { mutableStateOf<Item?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            val itemToSchedule = pendingSavedItem
            pendingSavedItem = null
            if (isGranted && (itemToSchedule != null)) {
                appContainer.reminderScheduler.scheduleReminder(itemToSchedule)
            }
            onNavigateBack()
        }
    )

    LaunchedEffect(Unit) {
        viewModel.saveSuccessEvent.collectLatest { savedItem ->
            val isRemindersEnabled = appContainer.settingsRepository.isRemindersEnabled.first()
            if (!isRemindersEnabled) {
                onNavigateBack()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    appContainer.reminderScheduler.scheduleReminder(savedItem)
                    onNavigateBack()
                } else {
                    pendingSavedItem = savedItem
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                appContainer.reminderScheduler.scheduleReminder(savedItem)
                onNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add New Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📌",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Fields marked with (*) are required to track expiration accurately.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Section 1: Required Fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Primary Details",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Product Name Field (Required)
                    OutlinedTextField(
                        value = formState.productName,
                        onValueChange = viewModel::onNameChanged,
                        label = {
                            Text(
                                text = "Product Name *",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        placeholder = { Text("e.g. Fresh Organic Milk, Paracetamol") },
                        isError = formState.nameError != null,
                        supportingText = formState.nameError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Expiration Date Field (Required)
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = DateUtils.formatDate(formState.expirationDate),
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    text = "Expiration Date *",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            placeholder = { Text("Select date") },
                            isError = formState.dateError != null,
                            supportingText = formState.dateError?.let { { Text(it) } },
                            trailingIcon = {
                                IconButton(onClick = { showDatePickerDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Select Date"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePickerDialog = true }
                        )
                    }
                }
            }

            // Section 2: Optional Categorization & Reminders
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Category & Reminder",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // Category Selection
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tag,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Category *",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ItemCategory.entries.forEach { category ->
                                FilterChip(
                                    selected = category == formState.category,
                                    onClick = { viewModel.onCategorySelected(category) },
                                    label = { Text(category.displayName) },
                                    shape = RoundedCornerShape(10.dp),
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
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reminder Notice",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            reminderOptions.forEach { (days, label) ->
                                FilterChip(
                                    selected = days == formState.reminderDaysBefore,
                                    onClick = { viewModel.onReminderDaysSelected(days) },
                                    label = { Text(label) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                    // Optional Notes Field
                    OutlinedTextField(
                        value = formState.notes,
                        onValueChange = viewModel::onNotesChanged,
                        label = { Text("Notes (Optional)") },
                        placeholder = { Text("Add storage location, quantity, or extra details...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Save Item Button
            Button(
                onClick = viewModel::saveItem,
                enabled = !formState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (formState.isSaving) "Saving..." else "Save Item",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
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
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
