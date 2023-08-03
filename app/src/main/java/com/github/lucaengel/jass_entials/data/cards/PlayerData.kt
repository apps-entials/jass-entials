package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

/**
 * A player in a game of Jass.
 */
data class PlayerData(
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
    fun playableCards(trick: Trick, trump: Trump): List<Card> {
        val firstCard = trick.playerToCard
            .firstOrNull()
            ?.first
            ?: return cards

        val trumpSuit = Trump.asSuit(trump)

        // get trump cards (null if trump was unger ufe or obe abe)
        val trumpCards: List<Card> = if (trumpSuit != null) {
            cardsOfSuit(trumpSuit)
        } else {
            listOf()
        }

        // cards of the suit of the first card played
        val firstCardSuitCards = cardsOfSuit(firstCard.suit)
        // no under trumping (ungertrumpfe) possible
        val playableTrumpCards = playableTrumpCards(trumpCards, trick, trumpSuit)

        val playableCards = (firstCardSuitCards + playableTrumpCards).distinct()

        return playableCards
            .ifEmpty { cards }
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

        return when (trick.playerToCard.indexOfFirst { it.first.suit == trump }) {
            -1 -> playableTrumpCards // no trump has been played yet
            0 -> playableTrumpCards // first card is trump --> can play any trump
            else -> { // no under trumping (ungertrumpfe) possible
                val maxTrumpRank = trick.playerToCard.maxOf {
                    if (it.first.suit == trump)
                        it.first.rank.trumpHeight
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
    private fun cardsOfSuit(suit: Suit): List<Card> {
        return cards.filter { it.suit == suit }
    }
}