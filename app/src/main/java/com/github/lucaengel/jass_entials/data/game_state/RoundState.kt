package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassConstants
import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * Represents the state of a Jass round.
 *
 * @param score The current score.
 * @param unplayedCards The unplayed cards.
 * @param trick The current trick.
 * @param trickNumber The number of the current trick.
 * @param cardDistributionsHandler Continuously augmenting data set containing the card distributions for each player known by everyone
 */
data class RoundState(
    private val score: Score,
    private val unplayedCards: List<Card>,
    private val trick: Trick,
    private val trickNumber: Int,
    //a map from players to cards they do not have
//    private val suitsNotInHand: Map<PlayerId, Set<Suit>> = mapOf(),
    private val cardDistributionsHandler: CardDistributionsHandler
) {

//    fun suitsNotInHand(): Map<PlayerId, Set<Suit>> {
//        return suitsNotInHand
//    }
    fun cardDistributionsHandler(): CardDistributionsHandler {
        return cardDistributionsHandler
    }

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

    // TODO: consider refactoring this since the roundstate kind
    //  of implicitly keeps track of the round and not the game
    /**
     * Returns the winning team in the current game or null if it is a draw
     *
     * @return The winning team in the current game or null if it is a draw
     */
    fun winningTeam(): TeamId? {
        return score.winningTeam()
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
    fun withTrickCollected(isSimulating: Boolean = false): RoundState {
        if (!trick.isFull())
            throw IllegalStateException("Cannot collect a trick that is not full.")

        if (!isSimulating) {
            println("\n\n\ncollecting trick $trickNumber:")
            println("unplayedCards: $unplayedCards")
            println("card distributions:\n$cardDistributionsHandler")
            println("\n\n\n")
        }




        val points = trick.points() + if (trickNumber == JassConstants.TRICKS_PER_ROUND) 5 else 0
        return RoundState(
            score = score.withPointsAdded(trick.winner().teamId(), points),
            unplayedCards = unplayedCards,
            trick = trick.nextTrick(),
            trickNumber = trickNumber + 1,
            cardDistributionsHandler = cardDistributionsHandler,
//            suitsNotInHand = if (isSimulating) suitsNotInHand else updateSuitsNotInHand(),
        )
    }

//    /**
//     * Returns an updated map containing the suits not in hand for each player.
//     *
//     * @return The updated map.
//     */
//    private fun updateSuitsNotInHand(): Map<PlayerId, Set<Suit>> {
//        val suit = trick.cards.first().suit
//        val newSuitsNotInHand = this.suitsNotInHand.toMutableMap()
//        for ((idx, card) in trick.cards.drop(1).withIndex()) {
//            // if cannot follow suit and did not play trump on a non-trump suit, add suit to suits not in hand
//            if (card.suit != suit
//                && card.suit != Trump.asSuit(trick.trump)
//                ) {
//                val playerId = trick.startingPlayerId.playerAtPosition(idx + 1)
//
//                // if jack of trump is still in the game, we check if it is guaranteed to be in someone else's hand
//                // only if that is true, we add the suit to the suits not in hand (o/w the player could still have it)
//                if (suit == Trump.asSuit(trick.trump) && unplayedCards.contains(Card(suit, Rank.JACK))) {
//                    GameStateHolder.guaranteedCards.toList().firstOrNull { it.second.contains(Card(suit, Rank.JACK)) }?.let {
//                        if (it.first != playerId) {
//                            newSuitsNotInHand += playerId to newSuitsNotInHand.getOrDefault(
//                                playerId,
//                                setOf()
//                            ).plus(suit)
//                        }
//                    }
//
//                    continue
//                }
//
//
//                println("updating suits not in hand: for trick: $trick")
//                println("player $playerId should be at index ${idx + 1} and played $card")
//
//
//                newSuitsNotInHand += playerId to newSuitsNotInHand.getOrDefault(playerId, setOf()).plus(suit)
//            }
//        }
//
//        return newSuitsNotInHand
//    }

    /**
     * Returns a new [RoundState] with the given [card] played.
     *
     * @throws IllegalStateException If the given [card] is not in the [unplayedCards] list.
     * @param card The card to play.
     * @return The new [RoundState].
     */
    fun withCardPlayed(card: Card, isSimulating: Boolean = false): RoundState {
        if (!unplayedCards.contains(card))
            throw IllegalStateException("Cannot play $card as it has already been played.")

        val newTrick = trick.withNewCardPlayed(card)

        if (!isSimulating) cardDistributionsHandler.updateCardDistributions(
            card = card,
            trick = newTrick,
            unplayedCards = unplayedCards,
            nextPlayer(),
        )

        return this.copy(
            unplayedCards = unplayedCards - card,
            trick = newTrick,
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
        fun initial(trump: Trump, startingPlayerId: PlayerId, score: Score = Score.INITIAL, cardDistributionsHandler: CardDistributionsHandler): RoundState {
            return RoundState(
                score = score,
                unplayedCards = Deck.STANDARD_DECK.cards,
                trick = Trick.initial(
                    startingPlayerId = startingPlayerId,
                    trump = trump,
                ),
                trickNumber = 1,
                cardDistributionsHandler = cardDistributionsHandler,
            )
        }
    }
}
