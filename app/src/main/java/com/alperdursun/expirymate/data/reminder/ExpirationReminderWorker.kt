package com.alperdursun.expirymate.data.reminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alperdursun.expirymate.ExpiryMateApplication
import com.alperdursun.expirymate.domain.model.ItemStatus
import com.alperdursun.expirymate.util.NotificationHelper
import kotlinx.coroutines.flow.first

class ExpirationReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_ITEM_ID = "key_item_id"
    }

    override suspend fun doWork(): Result {
        val itemId = inputData.getLong(KEY_ITEM_ID, -1L)
        if (itemId == -1L) {
            return Result.failure()
        }

        val appContainer = (applicationContext as? ExpiryMateApplication)?.container
            ?: return Result.failure()

        val isRemindersEnabled = appContainer.settingsRepository.isRemindersEnabled.first()
        if (!isRemindersEnabled) {
            return Result.success()
        }

        val item = appContainer.itemRepository.getItemById(itemId)
        if ((item == null) || (item.status != ItemStatus.ACTIVE)) {
            return Result.success()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return Result.success()
            }
        }

        NotificationHelper.showExpirationNotification(applicationContext, item)
        return Result.success()
    }
}
