package com.alperdursun.expirymate.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alperdursun.expirymate.data.reminder.ReminderScheduler
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.data.repository.SettingsRepository
import com.alperdursun.expirymate.domain.model.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isRemindersEnabled: Boolean = true,
    val defaultReminderDays: Int = 1,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isDynamicColorsEnabled: Boolean = true,
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val itemRepository: ItemRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.isRemindersEnabled,
        settingsRepository.defaultReminderDays,
        settingsRepository.themeMode,
        settingsRepository.isDynamicColorsEnabled,
    ) { enabled, defaultDays, theme, dynamicColors ->
        SettingsUiState(
            isRemindersEnabled = enabled,
            defaultReminderDays = defaultDays,
            themeMode = theme,
            isDynamicColorsEnabled = dynamicColors,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(),
    )

    fun onRemindersEnabledToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setRemindersEnabled(enabled)
            if (enabled) {
                val activeItems = itemRepository.observeActiveItems().first()
                reminderScheduler.rescheduleAllActiveReminders(activeItems)
            } else {
                reminderScheduler.cancelAllReminders()
            }
        }
    }

    fun onDefaultReminderDaysSelected(days: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultReminderDays(days)
        }
    }

    fun onThemeModeSelected(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun onDynamicColorsToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorsEnabled(enabled)
        }
    }

    companion object {
        fun Factory(
            settingsRepository: SettingsRepository,
            itemRepository: ItemRepository,
            reminderScheduler: ReminderScheduler,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(settingsRepository, itemRepository, reminderScheduler)
            }
        }
    }
}
