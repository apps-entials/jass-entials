package com.github.lucaengel.jass_entials.data.jass

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Suit

enum class Trump {
    DIAMONDS,
    HEARTS,
    SPADES,
    CLUBS,
    UNGER_UFE,
    OBE_ABE;

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
    }
}