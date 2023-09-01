package com.github.lucaengel.jass_entials.data.jass

import com.github.lucaengel.jass_entials.R
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.CardType
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder

/**
 * Enum representing the trump suit in a Jass game.
 */
enum class Trump {
    DIAMONDS,
    HEARTS,
    SPADES,
    CLUBS,
    UNGER_UFE,
    OBE_ABE;

    fun asPicture(): Int {
        return when (GameStateHolder.cardType) {
            CardType.FRENCH -> when (this) {
                CLUBS -> R.drawable.clubs
                SPADES -> R.drawable.spades
                HEARTS -> R.drawable.hearts
                DIAMONDS -> R.drawable.diamonds
                UNGER_UFE -> R.drawable.unger_ufe
                OBE_ABE -> R.drawable.ope_abe
            }
            CardType.GERMAN -> when (this) {
                CLUBS -> R.drawable.eicheln
                SPADES -> R.drawable.schilten
                HEARTS -> R.drawable.rosen
                DIAMONDS -> R.drawable.schellen
                UNGER_UFE -> R.drawable.unger_ufe
                OBE_ABE -> R.drawable.ope_abe
            }
        }
    }

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

    fun asSuit(): Suit? {
        return Trump.asSuit(this)
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