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
        val firstCard = trick.trickCards
            .firstOrNull()
            ?.card
            ?: return cards // no card has been played yet --> can play any card

        // cards of the suit of the first card played
        val firstCardSuitCards = cardsOfSuit(firstCard.suit)
        // if the player does not have any cards of the suit of the first card played, they can play any card
        if (firstCardSuitCards.isEmpty())
            return cards

        val trumpSuit = Trump.asSuit(trump)

        // get trump cards (null if trump was unger ufe or obe abe)
        val trumpCards: List<Card> = if (trumpSuit != null) {
            cardsOfSuit(trumpSuit)
        } else {
            listOf()
        }


        // no under trumping (ungertrumpfe) possible
        val playableTrumpCards = playableTrumpCards(trumpCards, trick, trumpSuit)

        val playableCards = (firstCardSuitCards + playableTrumpCards).distinct()

        // if the player only has one card left and it is a jack of trump, they can play any card
        // as you are allowed to keep the jack of trump even if you could play it as long as you have other cards
        if (playableCards.size == 1
            && trumpSuit != null
            && playableCards.first() == Card(Rank.JACK, trumpSuit)) {
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

        return when (trick.trickCards.indexOfFirst { it.card.suit == trump }) {
            -1 -> playableTrumpCards // no trump has been played yet
            0 -> playableTrumpCards // first card is trump --> can play any trump
            else -> { // no under trumping (ungertrumpfe) possible
                val maxTrumpRank = trick.trickCards.maxOf {
                    if (it.card.suit == trump)
                        it.card.rank.trumpHeight
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