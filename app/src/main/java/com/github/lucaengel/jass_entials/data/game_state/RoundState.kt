package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassConstants
import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * Represents the state of a Jass round.
 */
data class RoundState(
    private val score: Score,
    private val unplayedCards: List<Card>,
    private val trick: Trick,
    private val trickNumber: Int,
) {

    /**
     * Returns the current score.
     *
     * @return The current score.
     */
    fun score(): Score {
        return score
    }

    /**
     * Returns the unplayed cards.
     *
     * @return The unplayed cards.
     */
    fun unplayedCards(): List<Card> {
        return unplayedCards
    }

    /**
     * Returns the current trick.
     *
     * @return The current trick.
     */
    fun trick(): Trick {
        return trick
    }

    /**
     * Returns the player who is next to play a card.
     */
    fun nextPlayer(): PlayerId {
        return trick.nextPlayer()
    }

//    /**
//     * Returns a new [RoundState] with the given [startingPlayerId] and [trump].
//     *
//     * @throws IllegalStateException If the current round is not finished.
//     * @param startingPlayerId The id of the player that starts the round.
//     * @param trump The trump of the round.
//     *
//     * @return The new [RoundState].
//     */
//    fun nextRound(startingPlayerId: PlayerId, trump: Trump): RoundState {
//        if (trickNumber <= JassConstants.TRICKS_PER_ROUND) {
//            throw IllegalStateException("Cannot start a new round when the current round is not finished.")
//        }
//        return RoundState(
//            score = score,
//            unplayedCards = Deck.STANDARD_DECK.cards,
//            trick = Trick.initial(
//                startingPlayerId = startingPlayerId,
//                trump = trump,
//            ),
//            trickNumber = 0,
//        )
//    }

    fun isRoundOver(): Boolean {
        return trickNumber > JassConstants.TRICKS_PER_ROUND
    }

    /**
     * Returns a new [RoundState] with the current trick collected.
     *
     * @throws IllegalStateException If the current trick is not full.
     * @return The new [RoundState].
     */
    fun withTrickCollected(): RoundState {
        if (!trick.isFull())
            throw IllegalStateException("Cannot collect a trick that is not full.")

        val points = trick.points() + if (trickNumber == JassConstants.TRICKS_PER_ROUND) 5 else 0
        return RoundState(
            score = score.withPointsAdded(trick.winner().team(), points),
            unplayedCards = unplayedCards,
            trick = trick.nextTrick(),
            trickNumber = trickNumber + 1,
        )
    }

    /**
     * Returns a new [RoundState] with the given [card] played.
     *
     * @throws IllegalStateException If the given [card] is not in the [unplayedCards] list.
     * @param card The card to play.
     * @return The new [RoundState].
     */
    fun withCardPlayed(card: Card): RoundState {
        if (!unplayedCards.contains(card))
            throw IllegalStateException("Cannot play $card as it has already been played.")

        return this.copy(
            unplayedCards = unplayedCards - card,
            trick = trick.withNewCardPlayed(card),
        )
    }

    companion object {

        /**
         * Returns the initial [RoundState] with the given [startingPlayerId] and [trump].
         *
         * @param startingPlayerId The id of the player that starts the round.
         * @param trump The trump of the round.
         * @return The new [RoundState].
         */
        fun initial(trump: Trump, startingPlayerId: PlayerId, score: Score = Score.INITIAL): RoundState {
            return RoundState(
                score = score,
                unplayedCards = Deck.STANDARD_DECK.cards,
                trick = Trick.initial(
                    startingPlayerId = startingPlayerId,
                    trump = trump,
                ),
                trickNumber = 1,
            )
        }
    }
}
