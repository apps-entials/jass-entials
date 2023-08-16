package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

/**
 * A player in a game of Jass.
 */
data class PlayerData(
    val id: PlayerId,
    val firstName: String,
    val lastName: String,
    val cards: List<Card>,
    val teamNb: Int,
    val token: String,
) : Serializable {
    constructor() : this(
        id = PlayerId.PLAYER_1,
        firstName = "",
        lastName = "",
        cards = listOf(),
        teamNb = 0,
        token = "",
    )

    fun withCardPlayed(card: Card): PlayerData {
        if (!cards.contains(card)) throw IllegalStateException("Player does not have card $card")

        return copy(cards = cards.minus(card))
    }

    /**
     * Returns the cards that can be played for the given [trick] with the given [trump].
     *
     * @param trick the trick that has been played so far
     * @param trump the trump suit
     * @return the cards that can be played
     */
    fun playableCards(trick: Trick, trump: Trump): List<Card> {
        return playableCards(trick, trump, cards)
    }

    companion object {
        fun playableCards(trick: Trick, trump: Trump, cards: List<Card>): List<Card> {
            val firstCard = trick.cards
                .firstOrNull()
                ?: return cards // no card has been played yet --> can play any card

            val trumpSuit = Trump.asSuit(trump)

            // get trump cards (null if trump was unger ufe or obe abe)
            val trumpCards: List<Card> = if (trumpSuit != null) {
                cardsOfSuit(trumpSuit, cards)
            } else {
                listOf()
            }

            // cards of the suit of the first card played
            val firstCardSuitCards = cardsOfSuit(firstCard.suit, cards)

            // if the player does not have any cards of the suit of the first card played, they can play any card
            // as long as they do not under trump (ungertrumpfe)
            if (firstCardSuitCards.isEmpty()) {
                if (trumpSuit == null) return cards

                val trickTrumpCards = cardsOfSuit(trumpSuit, trick.cards)

                // if no trump lying, no under trumping (ungertrumpfe) possible
                if (trickTrumpCards.isEmpty()) return cards

                val highestTrickTrumpCard = trickTrumpCards
                    .reduceRight { c1, c2 -> if (c1.isHigherThan(c2, trump)) c1 else c2 }

                return cards.filter { it.suit != trumpSuit || it.isHigherThan(highestTrickTrumpCard, trump) }
            }

            // no under trumping (ungertrumpfe) possible
            val playableTrumpCards = playableTrumpCards(trumpCards, trick, trumpSuit)

            val playableCards = (firstCardSuitCards + playableTrumpCards).distinct()

            // if the player only has one card left and it is a jack of trump, they can play any card
            // as you are allowed to keep the jack of trump even if you could play it as long as you have other cards
            if (playableCards.size == 1
                && trumpSuit != null
                && playableCards.first() == Card(trumpSuit, Rank.JACK)
            ) {
                return cards
            }

            // no empty check needed since we already checked if
            // the player has cards of the suit of the first card played
            return playableCards
        }

        /**
         * Returns the trump cards that can be played for the given [trick] with the given [trump].
         *
         * @param trumpCards the trump cards of the player
         * @param trick the trick that has been played so far
         * @param trump the trump suit (null if trump was unger ufe or obe abe)
         * @return the trump cards that can be played
         */
        private fun playableTrumpCards(
            trumpCards: List<Card>,
            trick: Trick,
            trump: Suit?
        ): List<Card> {
            if (trump == null) return listOf()

            var playableTrumpCards = trumpCards

            return when (trick.cards.indexOfFirst { it.suit == trump }) {
                -1 -> playableTrumpCards // no trump has been played yet
                0 -> playableTrumpCards // first card is trump --> can play any trump
                else -> { // no under trumping (ungertrumpfe) possible
                    val maxTrumpRank = trick.cards.maxOf {
                        if (it.suit == trump)
                            it.rank.trumpHeight
                        else
                            Rank.SIX.trumpHeight
                    }

                    playableTrumpCards = trumpCards.filter { it.rank.trumpHeight > maxTrumpRank }

                    playableTrumpCards
                }
            }
        }

        /**
         * Returns the cards of the given [suit].
         *
         * @param suit the suit
         * @return the cards of the given [suit]
         */
        private fun cardsOfSuit(suit: Suit, cards: List<Card>): List<Card> {
            return cards.filter { it.suit == suit }
        }
    }
}