package com.marvel.recruiter.game

import kotlin.random.Random

/** Mínimo de heróis recrutados para abrir a loja (mesmo mínimo de time do RF-08). */
const val SHOP_UNLOCK_HEROES = 3

const val SHOP_PRICE_COMMON = 150
const val SHOP_PRICE_RARE = 300
const val SHOP_PRICE_LEGENDARY = 750

data class ShopPick(val cvId: Long, val rarity: Rarity, val price: Int)

fun shopSlots(rarity: Rarity): Int = when (rarity) {
    Rarity.COMMON -> 3
    Rarity.RARE -> 3
    Rarity.LEGENDARY -> 1
}

fun shopPrice(rarity: Rarity): Int = when (rarity) {
    Rarity.COMMON -> SHOP_PRICE_COMMON
    Rarity.RARE -> SHOP_PRICE_RARE
    Rarity.LEGENDARY -> SHOP_PRICE_LEGENDARY
}

/** Sorteia a vitrine do dia: cada seção sai só do pool da sua raridade, sem repetir herói. */
fun drawShop(pool: List<PackCandidate>, random: Random): List<ShopPick> {
    val picks = mutableListOf<ShopPick>()
    for (rarity in Rarity.entries) {
        val tier = pool.filter { rarityOf(it.strength) == rarity }.toMutableList()
        repeat(shopSlots(rarity)) {
            if (tier.isEmpty()) return@repeat
            val cvId = drawFromPack(tier, random)
            tier.removeAll { it.cvId == cvId }
            picks += ShopPick(cvId, rarity, shopPrice(rarity))
        }
    }
    return picks
}
