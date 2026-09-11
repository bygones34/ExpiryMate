package com.alperdursun.expirymate.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    private val today = LocalDate.of(2026, 1, 15)

    @Test
    fun testExpiredDate() {
        val expiredDate = today.minusDays(1)
        assertTrue(DateUtils.isExpired(expiredDate, today))
        assertFalse(DateUtils.isThisWeek(expiredDate, today))
        assertFalse(DateUtils.isLater(expiredDate, today))
        assertEquals("Expired yesterday", DateUtils.getRelativeExpiryText(expiredDate, today))
    }

    @Test
    fun testExpiringToday() {
        val todayDate = today
        assertFalse(DateUtils.isExpired(todayDate, today))
        assertTrue(DateUtils.isThisWeek(todayDate, today))
        assertFalse(DateUtils.isLater(todayDate, today))
        assertEquals("Expires today", DateUtils.getRelativeExpiryText(todayDate, today))
    }

    @Test
    fun testExpiringWithinNext7Days() {
        val in3Days = today.plusDays(3)
        val in7Days = today.plusDays(7)

        assertFalse(DateUtils.isExpired(in3Days, today))
        assertTrue(DateUtils.isThisWeek(in3Days, today))
        assertFalse(DateUtils.isLater(in3Days, today))
        assertEquals("Expires in 3 days", DateUtils.getRelativeExpiryText(in3Days, today))

        assertTrue(DateUtils.isThisWeek(in7Days, today))
        assertFalse(DateUtils.isLater(in7Days, today))
    }

    @Test
    fun testExpiringLaterThan7Days() {
        val in8Days = today.plusDays(8)

        assertFalse(DateUtils.isExpired(in8Days, today))
        assertFalse(DateUtils.isThisWeek(in8Days, today))
        assertTrue(DateUtils.isLater(in8Days, today))
        assertEquals("Expires in 8 days", DateUtils.getRelativeExpiryText(in8Days, today))
    }
}
