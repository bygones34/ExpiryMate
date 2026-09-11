package com.alperdursun.expirymate.data.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.util.ReminderCalculator
import java.util.concurrent.TimeUnit

open class ReminderScheduler(private val context: Context?) {

    companion object {
        const val WORK_TAG = "expiration_reminders_tag"
        fun getWorkName(itemId: Long): String = "expiry_reminder_$itemId"
    }

    open fun scheduleReminder(item: Item) {
        val ctx = context ?: return
        val delayMillis = ReminderCalculator.calculateDelayMillis(item.expirationDate, item.reminderDaysBefore)
        if (delayMillis == null) {
            cancelReminder(item.id)
            return
        }

        val inputData = workDataOf(ExpirationReminderWorker.KEY_ITEM_ID to item.id)

        val request = OneTimeWorkRequestBuilder<ExpirationReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(ctx).enqueueUniqueWork(
            getWorkName(item.id),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    open fun cancelReminder(itemId: Long) {
        val ctx = context ?: return
        WorkManager.getInstance(ctx).cancelUniqueWork(getWorkName(itemId))
    }

    open fun cancelAllReminders() {
        val ctx = context ?: return
        WorkManager.getInstance(ctx).cancelAllWorkByTag(WORK_TAG)
    }

    open fun rescheduleAllActiveReminders(activeItems: List<Item>) {
        activeItems.forEach { item ->
            scheduleReminder(item)
        }
    }
}
