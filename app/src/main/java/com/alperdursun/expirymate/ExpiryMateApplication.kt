package com.alperdursun.expirymate

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.alperdursun.expirymate.data.local.AppDatabase
import com.alperdursun.expirymate.data.reminder.ReminderScheduler
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.data.repository.SettingsRepository
import com.alperdursun.expirymate.util.NotificationHelper

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class ExpiryMateApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannel(this)
    }
}

class AppContainer(context: Application) {
    private val database = AppDatabase.getInstance(context)
    val itemRepository = ItemRepository(database.itemDao())
    val settingsRepository = SettingsRepository(context.dataStore)
    val reminderScheduler = ReminderScheduler(context)
}
