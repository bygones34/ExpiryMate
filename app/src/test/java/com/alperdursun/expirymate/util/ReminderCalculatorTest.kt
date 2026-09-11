package com.alperdursun.expirymate.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ReminderCalculatorTest {

    @Test
    fun testSameDayReminderFuture() {
        val expirationDate = LocalDate.of(2026, 9, 20)
        val now = LocalDateTime.of(2026, 9, 20, 8, 0) // 08:00 AM on expiration day

        val delay = ReminderCalculator.calculateDelayMillis(expirationDate, reminderDaysBefore = 0, now = now)

        // Expected target: 2026-09-20 at 09:00 AM -> 1 hour delay (3,600,000 ms)
        assertEquals(3_600_000L, delay)
    }

    @Test
    fun testOneDayBeforeReminder() {
        val expirationDate = LocalDate.of(2026, 9, 20)
        val now = LocalDateTime.of(2026, 9, 18, 9, 0) // Sept 18 at 09:00 AM

        val delay = ReminderCalculator.calculateDelayMillis(expirationDate, reminderDaysBefore = 1, now = now)

        // Target: Sept 19 at 09:00 AM -> 24 hours delay (86,400,000 ms)
        assertEquals(86_400_000L, delay)
    }

    @Test
    fun testThreeDaysBeforeReminder() {
        val expirationDate = LocalDate.of(2026, 9, 20)
        val now = LocalDateTime.of(2026, 9, 15, 9, 0) // Sept 15 at 09:00 AM

        val delay = ReminderCalculator.calculateDelayMillis(expirationDate, reminderDaysBefore = 3, now = now)

        // Target: Sept 17 at 09:00 AM -> 48 hours delay (172,800,000 ms)
        assertEquals(172_800_000L, delay)
    }

    @Test
    fun testReminderDatePassedButItemNotExpired() {
        val expirationDate = LocalDate.of(2026, 9, 20)
        val now = LocalDateTime.of(2026, 9, 17, 14, 0) // Sept 17 at 02:00 PM (past 09:00 AM target)

        val delay = ReminderCalculator.calculateDelayMillis(expirationDate, reminderDaysBefore = 3, now = now)

        // Past reminder time but item still active -> returns 0L for prompt execution
        assertEquals(0L, delay)
    }

    @Test
    fun testExpiredItemReturnsNull() {
        val expirationDate = LocalDate.of(2026, 9, 10)
        val now = LocalDateTime.of(2026, 9, 15, 9, 0) // Sept 15 (item expired Sept 10)

        val delay = ReminderCalculator.calculateDelayMillis(expirationDate, reminderDaysBefore = 1, now = now)

        // Item expired -> returns null (do not schedule)
        assertNull(delay)
    }

    @Test
    fun testMonthAndYearBoundaryCalculations() {
        val expirationDate = LocalDate.of(2026, 1, 1) // Jan 1, 2026
        val targetDateTime = ReminderCalculator.calculateTargetDateTime(expirationDate, reminderDaysBefore = 1)

        // Expected: Dec 31, 2025 at 09:00 AM
        assertEquals(LocalDateTime.of(2025, 12, 31, 9, 0), targetDateTime)
    }
}
