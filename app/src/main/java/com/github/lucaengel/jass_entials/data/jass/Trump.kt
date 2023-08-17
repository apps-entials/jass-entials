package com.github.lucaengel.jass_entials.data.jass

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Suit

/**
 * Enum representing the trump suit in a Jass game.
 */
enum class Trump() {
    DIAMONDS,
    HEARTS,
    SPADES,
    CLUBS,
    UNGER_UFE,
    OBE_ABE;

    override fun toString(): String {
        return when (this) {
            DIAMONDS -> Suit.DIAMONDS.symbol()
            HEARTS -> Suit.HEARTS.symbol()
            SPADES -> Suit.SPADES.symbol()
            CLUBS -> Suit.CLUBS.symbol()
            UNGER_UFE -> "\u2191"
            OBE_ABE -> "\u2193"
        }
    }

    companion object {

        /**
         * Calculates the points of the given [cards] with the given [trump].
         *
         * @param cards the cards to calculate the points for
         * @param trump the trump suit
         */
        fun calculatePoints(cards: List<Card>, trump: Trump): Int {
            return cards.foldRight(0) { card, acc ->
                acc + card.points(trump)
            }
        }

        /**
         * Returns whether the given [suit] is the trump suit.
         */
        fun isSuitTrumpSuit(suit: Suit, trump: Trump): Boolean {
            return suit == Suit.DIAMONDS && trump == DIAMONDS ||
                    suit == Suit.HEARTS && trump == HEARTS ||
                    suit == Suit.SPADES && trump == SPADES ||
                    suit == Suit.CLUBS && trump == CLUBS
        }

        /**
         * Returns the [Suit] representation of the given [trump] or `null` if the given [trump] is not a suit trump.
         *
         * @param trump the trump to get the suit for
         * @return the [Suit] representation of the given [trump] or `null` if the given [trump] is not a suit trump
         */
        fun asSuit(trump: Trump): Suit? {
            return when (trump) {
                DIAMONDS -> Suit.DIAMONDS
                HEARTS -> Suit.HEARTS
                SPADES -> Suit.SPADES
                CLUBS -> Suit.CLUBS
                else -> null
            }
        }
    }
}