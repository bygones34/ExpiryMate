package com.alperdursun.expirymate.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val expirationDate: LocalDate,
    val category: ItemCategory,
    val reminderDaysBefore: Int,
    val notes: String? = null,
    val status: ItemStatus = ItemStatus.ACTIVE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
)
