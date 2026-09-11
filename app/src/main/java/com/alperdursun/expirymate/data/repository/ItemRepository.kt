package com.alperdursun.expirymate.data.repository

import com.alperdursun.expirymate.data.local.ItemDao
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {

    fun observeActiveItems(): Flow<List<Item>> = itemDao.observeActiveItems()

    fun observeHistoryItems(): Flow<List<Item>> = itemDao.observeHistoryItems()

    fun observeItemById(itemId: Long): Flow<Item?> = itemDao.observeItemById(itemId)

    suspend fun getItemById(itemId: Long): Item? = itemDao.getItemById(itemId)

    suspend fun addItem(item: Item): Long = itemDao.insertItem(item)

    suspend fun updateItem(item: Item) = itemDao.updateItem(item)

    suspend fun markAsUsed(itemId: Long) {
        itemDao.updateItemStatus(itemId, ItemStatus.USED, System.currentTimeMillis())
    }

    suspend fun markAsDiscarded(itemId: Long) {
        itemDao.updateItemStatus(itemId, ItemStatus.DISCARDED, System.currentTimeMillis())
    }

    suspend fun deleteItem(itemId: Long) {
        itemDao.deleteItem(itemId)
    }
}
