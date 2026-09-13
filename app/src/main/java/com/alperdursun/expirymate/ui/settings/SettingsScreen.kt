package com.alperdursun.expirymate.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperdursun.expirymate.ExpiryMateApplication
import com.alperdursun.expirymate.domain.model.AppThemeMode

private const val PRIVACY_POLICY_URL = "https://bygones34.github.io/ExpiryMate/privacy-policy.html"

private val reminderOptions = listOf(
    0 to "Same day",
    1 to "1 day before",
    3 to "3 days before",
    7 to "7 days before",
)

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(
        factory = run {
            val appContainer = (LocalContext.current.applicationContext as ExpiryMateApplication).container
            SettingsViewModel.Factory(
                settingsRepository = appContainer.settingsRepository,
                itemRepository = appContainer.itemRepository,
                reminderScheduler = appContainer.reminderScheduler,
            )
        }
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDefaultDaysDialog by remember { mutableStateOf(value = false) }
    var showThemeDialog by remember { mutableStateOf(value = false) }

    val defaultReminderText = reminderOptions.find { it.first == uiState.defaultReminderDays }?.second ?: "1 day before"
    val appVersionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0.0"
        } catch (_: Exception) {
            "1.0.0"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Section 1: Notifications
        SettingsSection(title = "Notifications") {
            SettingsSwitchRow(
                icon = Icons.Default.Notifications,
                title = "Expiration Reminders",
                subtitle = "Receive notifications before products expire",
                checked = uiState.isRemindersEnabled,
                onCheckedChange = viewModel::onRemindersEnabledToggled
            )
            SettingsClickableRow(
                icon = Icons.Default.Notifications,
                title = "Default Reminder Timing",
                value = defaultReminderText,
                onClick = { showDefaultDaysDialog = true }
            )
        }

        // Section 2: Appearance
        SettingsSection(title = "Appearance") {
            SettingsClickableRow(
                icon = Icons.Default.Palette,
                title = "Theme Mode",
                value = uiState.themeMode.displayName,
                onClick = { showThemeDialog = true }
            )
            SettingsSwitchRow(
                icon = Icons.Default.Palette,
                title = "Dynamic Colors",
                subtitle = "Use Material You dynamic color palette",
                checked = uiState.isDynamicColorsEnabled,
                onCheckedChange = viewModel::onDynamicColorsToggled
            )
        }

        // Section 3: About
        SettingsSection(title = "About") {
            SettingsClickableRow(
                icon = Icons.Default.Info,
                title = "Version",
                value = appVersionName
            )
            SettingsClickableRow(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                value = "",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, PRIVACY_POLICY_URL.toUri())
                        context.startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        // Gracefully ignore if no browser/activity available to handle URL
                    }
                }
            )

        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Theme Mode Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    AppThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onThemeModeSelected(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = mode == uiState.themeMode,
                                onClick = {
                                    viewModel.onThemeModeSelected(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = mode.displayName, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Default Reminder Days Selection Dialog
    if (showDefaultDaysDialog) {
        AlertDialog(
            onDismissRequest = { showDefaultDaysDialog = false },
            title = { Text("Default Reminder Timing") },
            text = {
                Column {
                    reminderOptions.forEach { (days, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onDefaultReminderDaysSelected(days)
                                    showDefaultDaysDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = days == uiState.defaultReminderDays,
                                onClick = {
                                    viewModel.onDefaultReminderDaysSelected(days)
                                    showDefaultDaysDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDefaultDaysDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
}
