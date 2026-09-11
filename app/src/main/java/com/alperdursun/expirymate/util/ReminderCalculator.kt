package com.alperdursun.expirymate.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object ReminderCalculator {

    val DEFAULT_REMINDER_TIME: LocalTime = LocalTime.of(9, 0)

    fun calculateTargetDateTime(
        expirationDate: LocalDate,
        reminderDaysBefore: Int,
    ): LocalDateTime {
        val reminderDate = expirationDate.minusDays(reminderDaysBefore.toLong())
        return LocalDateTime.of(reminderDate, DEFAULT_REMINDER_TIME)
    }

    fun calculateDelayMillis(
        expirationDate: LocalDate,
        reminderDaysBefore: Int,
        now: LocalDateTime = LocalDateTime.now()
    ): Long? {
        if (DateUtils.isExpired(expirationDate, now.toLocalDate())) {
            return null
        }

        val targetDateTime = calculateTargetDateTime(expirationDate, reminderDaysBefore)
        val delayMillis = Duration.between(now, targetDateTime).toMillis()

        return if (delayMillis <= 0L) {
            0L
        } else {
            delayMillis
        }
    }
}
