package com.github.lucaengel.jass_entials.data.cards

import java.io.Serializable

/**
 * A player in a game of Jass.
 */
data class Player(
    val email: String,
    val playerIdx: Int,
    val firstName: String,
    val lastName: String,
    val cards: List<Card>,
    val teamNb: Int,
    val token: String,
) : Serializable {
    constructor() : this(
        email = "",
        playerIdx = 0,
        firstName = "",
        lastName = "",
        cards = listOf(),
        teamNb = 0,
        token = "",
    )

    /**
     * Returns the cards that can be played for the given [trick] with the given [trump].
     *
     * @param trick the trick that has been played so far
     * @param trump the trump suit
     * @return the cards that can be played
     */
    fun playableCards(trick: Trick, trump: Suit): List<Card> {
        val firstCard = trick.cards
            .firstOrNull()
            ?: return cards

        val trumpCards = cardsOfSuit(trump)

        // hold suit
        if (firstCard.suit == trump) {
            if (trumpCards.isNotEmpty())
                return trumpCards

            // no trump cards
            return cards
        }

        val firstCardSuitCards = cardsOfSuit(firstCard.suit)

        val playableTrumpCards = playableTrumpCards(trumpCards, trick, trump)

        val playableCards = firstCardSuitCards + playableTrumpCards

        if (playableCards.isNotEmpty())
            return playableCards

        return cards
    }

    private fun playableTrumpCards(
        trumpCards: List<Card>,
        trick: Trick,
        trump: Suit
    ): List<Card> {
        var playableTrumpCards = trumpCards

        return when (trick.cards.indexOfFirst { it.suit == trump }) {
            -1 -> playableTrumpCards // no trump has been played yet
            0 -> playableTrumpCards // first card is trump --> can play any trump
            else -> { // no under trumping (ungertrumpfe) possible
                val maxTrumpRank = trick.cards.maxOf {
                    if (it.suit == trump) it.rank.trumpHeight else Rank.SIX.trumpHeight
                }

                playableTrumpCards = trumpCards.filter { it.rank.trumpHeight > maxTrumpRank }

                playableTrumpCards
            }
        }
    }

    private fun cardsOfSuit(suit: Suit): List<Card> {
        return cards.filter { it.suit == suit }
    }
}