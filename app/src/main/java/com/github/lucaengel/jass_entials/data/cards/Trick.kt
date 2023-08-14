package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * A trick is a collection of cards played by each player in a round.
 *
 * @param trickCards A list of pairs of cards and the player who played them.
 */
data class Trick(
    val trickCards: List<TrickCard>,
) {

    /**
     * A card played by a player.
     */
    data class TrickCard(
        val card: Card,
        val playerId: PlayerId,
    )

    /**
     * Represents the winner of a trick.
     */
    data class TrickWinner(val playerId: PlayerId, val trick: Trick) {
        init {
            if (trick.trickCards.size != GameStateHolder.players.size)
                throw IllegalArgumentException("A trick must have exactly as many cards as there are players to be completed.")
        }
    }

    constructor() : this(trickCards = listOf())

    fun withNewCardPlayed(card: Card, playerId: PlayerId): Trick {
        return this.copy(trickCards = trickCards + TrickCard(card, playerId))
    }

    /**
     * Returns true if the trick is full, i.e. if 4 cards have been played.
     *
     * @return True if the trick is full.
     */
    fun isFull(): Boolean {
        return trickCards.size == 4
    }

    /**
     * Returns the player who played the highest card in the trick.
     *
     * @param trump The trump of the round.
     * @return The player who played the highest card.
     */
    fun winner(trump: Trump): TrickWinner {
        return trickCards.foldRight(trickCards.first()) { (card, playerId), prevWinner ->
            if (prevWinner.card.isHigherThan(card, trump)) {
                prevWinner
            } else {
                TrickCard(card, playerId)
            }
        }.playerId.let { TrickWinner(it, this) }
    }

    /**
     * Returns the points of the trick.
     *
     * @param trump The trump of the round.
     * @return The points of the trick.
     */
    fun points(trump: Trump): Int {
        return trickCards.sumOf { it.card.points(trump) }
    }
}