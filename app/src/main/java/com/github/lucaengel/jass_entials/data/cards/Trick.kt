package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.jass.Trump

data class Trick(
    val playerToCard: List<Pair<Card, PlayerData>>,
) {
    constructor() : this(playerToCard = listOf())

    fun isFull(): Boolean {
        return playerToCard.size == 4
    }

    fun winner(trump: Trump): PlayerData {
        return playerToCard.foldRight(playerToCard.first()) { (card, playerData), winner ->
            if (card.isHigherThan(winner.first, trump)) {
                Pair(card, playerData)
            } else {
                winner
            }
        }.second
    }
}