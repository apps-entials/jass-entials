package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * A trick is a collection of cards played by each player in a round.
 *
 * @param cards A list of pairs of cards and the player who played them.
 */
data class Trick(
    val cards: List<Card>,
    val startingPlayerId: PlayerId,
    val trump: Trump,
) {

//    /**
//     * Represents the winner of a trick.
//     */
//    data class TrickWinner(val playerId: PlayerId, val trick: Trick) {
//        init {
//            if (trick.cards.size != GameStateHolder.players.size)
//                throw IllegalArgumentException("A trick must have exactly as many cards as there are players to be completed.")
//        }
//    }

    fun withNewCardPlayed(card: Card): Trick {
        if (isFull())
            throw IllegalStateException("Cannot add card to trick that is already full.")

        return this.copy(cards = cards + card)
    }

    fun nextTrick(): Trick {
        if (!isFull())
            throw IllegalStateException("Cannot move on to next trick if current trick is not full.")

        return this.copy(
            cards = listOf(),
            startingPlayerId = winner(),
        )
    }

    /**
     * Returns true if the trick is full, i.e. if 4 cards have been played.
     *
     * @return True if the trick is full.
     */
    fun isFull(): Boolean {
        return cards.size == 4
    }

    /**
     * Returns the player who played the highest card in the trick.
     *
     * @return The player who played the highest card.
     */
    fun winner(): PlayerId {
        if (!isFull())
            throw IllegalStateException("Cannot determine winner of trick that is not full.")

        val (_, position) = cards.foldRightIndexed(Pair(cards.first(), 0)) { idx, card, (highestCard, position) ->
            if (highestCard.isHigherThan(card, trump)) {
                Pair(highestCard, position)
            } else {
                Pair(card, idx)
            }
        }

        return startingPlayerId.nextPlayer(position)
    }

    /**
     * Returns the points of the trick.
     *
     * @return The points of the trick.
     */
    fun points(): Int {
        return cards.sumOf { it.points(trump) }
    }

    companion object {
        fun initial(startingPlayerId: PlayerId, trump: Trump): Trick {
            return Trick(listOf(), startingPlayerId, trump)
        }
    }
}