package com.marvel.recruiter.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shop_offer",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["cvId"],
            childColumns = ["characterCvId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["characterCvId"])],
)
data class ShopOfferEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayKey: Long,
    val characterCvId: Long,
    val rarity: String,
    val price: Int,
    val purchased: Boolean = false,
)
