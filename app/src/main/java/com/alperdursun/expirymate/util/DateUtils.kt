package com.alperdursun.expirymate.util

import android.content.Context
import com.alperdursun.expirymate.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

enum class ExpiryUrgency {
    EXPIRED,
    WARNING,
    SAFE
}

object DateUtils {

    fun getSupportedLocale(systemLocale: Locale = Locale.getDefault()): Locale {
        return if (systemLocale.language.equals("tr", ignoreCase = true)) {
            Locale.forLanguageTag("tr-TR")
        } else {
            Locale.US
        }
    }

    fun formatDate(date: LocalDate?, locale: Locale = Locale.getDefault()): String {
        if (date == null) return ""
        val targetLocale = getSupportedLocale(locale)
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(targetLocale)
        return date.format(formatter)
    }

    fun isExpired(date: LocalDate, today: LocalDate = LocalDate.now()): Boolean {
        return date.isBefore(today)
    }

    fun isThisWeek(date: LocalDate, today: LocalDate = LocalDate.now()): Boolean {
        return !date.isBefore(today) && !date.isAfter(today.plusDays(7))
    }

    fun isLater(date: LocalDate, today: LocalDate = LocalDate.now()): Boolean {
        return date.isAfter(today.plusDays(7))
    }

    fun getExpiryUrgency(date: LocalDate, today: LocalDate = LocalDate.now()): ExpiryUrgency {
        return when {
            isExpired(date, today) -> ExpiryUrgency.EXPIRED
            isThisWeek(date, today) -> ExpiryUrgency.WARNING
            else -> ExpiryUrgency.SAFE
        }
    }

    fun getRelativeExpiryText(date: LocalDate, today: LocalDate = LocalDate.now()): String {
        val days = ChronoUnit.DAYS.between(today, date)
        return when {
            days < 0 -> {
                val pastDays = abs(days)
                if (pastDays == 1L) "Expired yesterday" else "Expired $pastDays days ago"
            }
            days == 0L -> "Expires today"
            days == 1L -> "Expires tomorrow"
            else -> "Expires in $days days"
        }
    }

    fun getRelativeExpiryText(context: Context, date: LocalDate, today: LocalDate = LocalDate.now()): String {
        val days = ChronoUnit.DAYS.between(today, date)
        return when {
            days < 0 -> {
                val pastDays = abs(days)
                if (pastDays == 1L) {
                    context.getString(R.string.relative_expired_yesterday)
                } else {
                    context.getString(R.string.relative_expired_days_ago, pastDays)
                }
            }
            days == 0L -> context.getString(R.string.relative_expires_today)
            days == 1L -> context.getString(R.string.relative_expires_tomorrow)
            else -> context.getString(R.string.relative_expires_in_days, days)
        }
    }
}
