package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.cards.Rank
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
 * @param suitsNotInHand map from players to cards they do not have
 */
data class RoundState(
    private val score: Score,
    private val unplayedCards: List<Card>,
    private val trick: Trick,
    private val trickNumber: Int,
    //a map from players to cards they do not have
    private val suitsNotInHand: Map<PlayerId, Set<Suit>> = mapOf(),

) {

    fun suitsNotInHand(): Map<PlayerId, Set<Suit>> {
        return suitsNotInHand
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

        val points = trick.points() + if (trickNumber == JassConstants.TRICKS_PER_ROUND) 5 else 0
        return RoundState(
            score = score.withPointsAdded(trick.winner().teamId(), points),
            unplayedCards = unplayedCards,
            trick = trick.nextTrick(),
            trickNumber = trickNumber + 1,
            suitsNotInHand = if (isSimulating) suitsNotInHand else updateSuitsNotInHand(),
        )
    }

    /**
     * Returns an updated map containing the suits not in hand for each player.
     *
     * @return The updated map.
     */
    private fun updateSuitsNotInHand(): Map<PlayerId, Set<Suit>> {
        val suit = trick.cards.first().suit
        val newSuitsNotInHand = this.suitsNotInHand.toMutableMap()
        for ((idx, card) in trick.cards.drop(1).withIndex()) {
            // if cannot follow suit and did not play trump on a non-trump suit, add suit to suits not in hand
            if (card.suit != suit && card.suit != Trump.asSuit(trick.trump)) {
                val playerId = trick.startingPlayerId.playerAtPosition(idx + 1)
                newSuitsNotInHand += playerId to newSuitsNotInHand.getOrDefault(playerId, setOf()).plus(suit)
            }
        }

        return newSuitsNotInHand
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

        updateCardDistributions(card)

        return this.copy(
            unplayedCards = unplayedCards - card,
            trick = trick.withNewCardPlayed(card),
        )
    }

    /**
     * Responsible to update the card distributions known in GameStateHolder based on the newly played card.
     *
     * @param card The card that was played.
     */
    private fun updateCardDistributions(card: Card) {
        if (trick.trump == Trump.OBE_ABE
            && card.rank == Rank.ACE
            && GameStateHolder.acesPerPlayer.isNotEmpty()
        ) {
            val acesLeft = unplayedCards.filter { it.rank == Rank.ACE } - card
            if (acesLeft.isEmpty()) {
                GameStateHolder.acesPerPlayer = mapOf()
                return
            }

            val acesPerPlayer = GameStateHolder.acesPerPlayer[nextPlayer()] ?: 0
            if (acesPerPlayer > 0) GameStateHolder.acesPerPlayer += nextPlayer() to (acesPerPlayer - 1)

            // adjust guaranteed cards if they are clear
            if (GameStateHolder.acesPerPlayer.containsValue(acesLeft.size) && acesLeft.isNotEmpty()) {
                GameStateHolder.acesPerPlayer.toList().filter { it.second == acesLeft.size }.forEach {
                    GameStateHolder.guaranteedCards += it.first to
                            ((GameStateHolder.guaranteedCards[it.first] ?: setOf())
                                    + acesLeft)
                }
            }
        } else if (trick.trump == Trump.UNGER_UFE
            && card.rank == Rank.SIX
            && GameStateHolder.acesPerPlayer.isNotEmpty()
            ) {

            val sixesLeft = unplayedCards.filter { it.rank == Rank.SIX } - card
            if (sixesLeft.isEmpty()) {
                GameStateHolder.acesPerPlayer = mapOf()
                return
            }

            val sixesPerPlayer = GameStateHolder.acesPerPlayer[nextPlayer()] ?: 0
            if (sixesPerPlayer > 0) GameStateHolder.acesPerPlayer += nextPlayer() to (sixesPerPlayer - 1)

            // adjust guaranteed cards if they are clear
            if (GameStateHolder.acesPerPlayer.containsValue(sixesLeft.size)
                && sixesLeft.isNotEmpty()) {
                GameStateHolder.acesPerPlayer.toList().filter { it.second == sixesLeft.size }.forEach {
                    GameStateHolder.guaranteedCards += it.first to
                            ((GameStateHolder.guaranteedCards[it.first] ?: setOf())
                                    + sixesLeft)
                }
                // TODO: could also still update the cards per suit if the sixes / aces are not already included in the player's cards per suit
            }


        }

        // adjust guaranteed cards if they are clear for number of cards per color
        if (GameStateHolder.cardsPerSuit.isNotEmpty()) {
            val cardsLeft = unplayedCards.filter { it.suit == card.suit } - card

            if (GameStateHolder.cardsPerSuit.containsValue(mapOf(card.suit to cardsLeft.size))
                && cardsLeft.isNotEmpty()) {

                GameStateHolder.cardsPerSuit
                    .toList()
                    .filter { (it.second[card.suit] ?: 0) == cardsLeft.size }
                    .forEach { (id, _) ->

                        GameStateHolder.guaranteedCards += id to
                                (GameStateHolder.guaranteedCards[id] ?: setOf()) + cardsLeft.toSet()
                    }
            }
        }
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
