package com.alperdursun.expirymate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE status = 'ACTIVE' ORDER BY expirationDate ASC")
    fun observeActiveItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE status IN ('USED', 'DISCARDED') ORDER BY completedAt DESC")
    fun observeHistoryItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Query("UPDATE items SET status = :status, completedAt = :completedAtTimestamp WHERE id = :id")
    suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItem(id: Long)
}
