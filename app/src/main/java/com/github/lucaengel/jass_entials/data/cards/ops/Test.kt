package com.github.lucaengel.jass_entials.data.cards.ops

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.cards.Suit
import java.io.Serializable

data class Test(
    val players: List<Player> = listOf(),
    val currentPlayer: Player,
    val currentRound: Int,
    val currentTrick: Map<Player, Card> = mapOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Suit = Suit.CLUBS,
    val playerCards: Map<Player, List<Card>> = mapOf(),
) : Serializable {

}
