package com.alperdursun.expirymate.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alperdursun.expirymate.MainActivity
import com.alperdursun.expirymate.R
import com.alperdursun.expirymate.domain.model.Item
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object NotificationHelper {

    const val CHANNEL_ID = "expiration_reminders"
    const val CHANNEL_NAME = "Expiration Reminders"
    const val CHANNEL_DESCRIPTION = "Reminders for products approaching their expiration date."

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showExpirationNotification(context: Context, item: Item) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            item.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val days = ChronoUnit.DAYS.between(LocalDate.now(), item.expirationDate)
        val bodyText = when {
            days < 0 -> "${item.name} has expired."
            days == 0L -> "${item.name} expires today."
            days == 1L -> "${item.name} expires tomorrow."
            else -> "${item.name} expires in $days days."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("ExpiryMate Reminder")
            .setContentText(bodyText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(item.id.toInt(), notification)
        } catch (_: SecurityException) {
            // Permission not granted or notification posting rejected by system
        }
    }
}
