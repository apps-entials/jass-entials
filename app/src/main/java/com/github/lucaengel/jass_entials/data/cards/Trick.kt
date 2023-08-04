package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * A trick is a collection of cards played by each player in a round.
 *
 * @param playerToCard A list of pairs of cards and the player who played them.
 */
data class Trick(
    val playerToCard: List<Pair<Card, PlayerData>>,
) {
    constructor() : this(playerToCard = listOf())

    /**
     * Returns true if the trick is full, i.e. if 4 cards have been played.
     *
     * @return True if the trick is full.
     */
    fun isFull(): Boolean {
        return playerToCard.size == 4
    }

    /**
     * Returns the player who played the highest card in the trick.
     *
     * @param trump The trump of the round.
     * @return The player who played the highest card.
     */
    fun winner(trump: Trump): PlayerData {
        return playerToCard.foldRight(playerToCard.first()) { (card, playerData), winner ->
            if (winner.first.isHigherThan(card, trump)) {
                winner
            } else {
                Pair(card, playerData)
            }
        }.second
    }
}