package com.github.lucaengel.jass_entials.data.cards.ops

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Suit
import java.io.Serializable

data class Test(
    val playerData: List<PlayerData> = listOf(),
    val currentPlayerData: PlayerData,
    val currentRound: Int,
    val currentTrick: Map<PlayerData, Card> = mapOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Suit = Suit.CLUBS,
    val playerDataCards: Map<PlayerData, List<Card>> = mapOf(),
) : Serializable {

}
