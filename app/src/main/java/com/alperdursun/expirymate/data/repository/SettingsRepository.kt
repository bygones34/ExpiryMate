package com.alperdursun.expirymate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val KEY_EXPIRATION_REMINDERS_ENABLED = booleanPreferencesKey("expiration_reminders_enabled")
        val KEY_DEFAULT_REMINDER_DAYS = intPreferencesKey("default_reminder_days")
    }

    val isRemindersEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_EXPIRATION_REMINDERS_ENABLED] ?: true
        }

    val defaultReminderDays: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_DEFAULT_REMINDER_DAYS] ?: 1
        }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_EXPIRATION_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setDefaultReminderDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_REMINDER_DAYS] = days
        }
    }
}
