package com.marvel.recruiter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel.recruiter.data.local.entity.ShopOfferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    @Query("SELECT COUNT(*) FROM shop_offer WHERE dayKey = :dayKey")
    suspend fun countForDay(dayKey: Long): Int

    @Query("SELECT * FROM shop_offer WHERE dayKey = :dayKey ORDER BY price DESC, id ASC")
    fun observeDay(dayKey: Long): Flow<List<ShopOfferEntity>>

    @Query("SELECT * FROM shop_offer WHERE id = :id")
    suspend fun getById(id: Long): ShopOfferEntity?

    @Insert
    suspend fun insertAll(offers: List<ShopOfferEntity>)

    @Query("DELETE FROM shop_offer")
    suspend fun clear()

    @Query("UPDATE shop_offer SET purchased = 1 WHERE id = :id")
    suspend fun markPurchased(id: Long)
}
