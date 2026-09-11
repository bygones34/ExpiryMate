package com.alperdursun.expirymate.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

object DateUtils {

    private val defaultFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

    fun formatDate(date: LocalDate?): String {
        return date?.format(defaultFormatter) ?: ""
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
}
