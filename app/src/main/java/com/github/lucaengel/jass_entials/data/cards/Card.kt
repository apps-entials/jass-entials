package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.R
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

/**
 * The suits of a card.
 *
 * @property toString The name of the suit.
 * @property symbol The symbol of the suit.
 */
enum class Suit(val toString: String, val symbol: Char) : Serializable  {
    CLUBS("Clubs", '\u2663'),
    SPADES("Spades", '\u2660'),
    HEARTS("Hearts", '\u2665'),
    DIAMONDS("Diamonds", '\u2666'),
}

/**
 * The ranks of a card.
 *
 * @property rank The name of the rank.
 * @property normalHeight The height of the card in a normal game.
 * @property trumpHeight The height of the card in a trump game.
 */
enum class Rank(private val rank: String, normalHeight: Int, val trumpHeight: Int) : Serializable  {
    SIX("6", 6, 6),
    SEVEN("7", 7, 7),
    EIGHT("8", 8, 8),
    NINE("9", 9, 15),
    TEN("10", 10, 10),
    JACK("J", 11, 16),
    QUEEN("Q", 12, 12),
    KING("K", 13, 13),
    ACE("A", 14, 14);

    override fun toString(): String {
        return rank
    }
}

/**
 * A card in a game of Jass.
 *
 * @property rank The rank of the card.
 * @property suit The suit of the card.
 */
data class Card(
    val rank: Rank,
    val suit: Suit,
) : Serializable {
    constructor() : this(Rank.TEN, Suit.HEARTS)

    /**
     * Returns the points of this card considering the given trump.
     *
     * @param trump The trump of the game.
     */
    fun points(trump: Trump): Int {
        if ((trump == Trump.OBE_ABE || trump == Trump.UNGER_UFE) && rank == Rank.EIGHT) return 8

        if (trump == Trump.UNGER_UFE) {
            if (rank == Rank.SIX) return 11
            if (rank == Rank.ACE) return 0
        }

        if (Trump.isSuitTrumpSuit(suit, trump)) {
            if (rank == Rank.JACK) return 20
            if (rank == Rank.NINE) return 14
        }

        return when (rank) {
            Rank.TEN -> 10
            Rank.JACK -> 2
            Rank.QUEEN -> 3
            Rank.KING -> 4
            Rank.ACE -> 11
            else -> 0
        }
    }

    /**
     * Returns true if this card is higher than the given card.
     * Assumes that this card was played first (e.g., clubs then heart --> clubs wins)
     */
    fun isHigherThan(that: Card, trump: Trump): Boolean {
        // this is trump
        if (Trump.isSuitTrumpSuit(this.suit, trump)) {
            if (Trump.isSuitTrumpSuit(that.suit, trump)) {
                return this.rank.trumpHeight > that.rank.trumpHeight
            }
            return true
        }

        // only that is trump
        if (Trump.isSuitTrumpSuit(that.suit, trump)) {
            return false
        }

        // lei haute
        if (this.suit != that.suit) return true

        //same suit: check rank
        // unger ufe
        if (trump == Trump.UNGER_UFE) return this.rank < that.rank

        // no trump
        return this.rank > that.rank
    }

    override fun toString(): String {
        return "$rank${suit.symbol}"
    }

    companion object {

        private val cardImageMap = mapOf(
            Card(Rank.SIX, Suit.CLUBS) to R.drawable.clubs_6,
            Card(Rank.SEVEN, Suit.CLUBS) to R.drawable.clubs_7,
            Card(Rank.EIGHT, Suit.CLUBS) to R.drawable.clubs_8,
            Card(Rank.NINE, Suit.CLUBS) to R.drawable.clubs_9,
            Card(Rank.TEN, Suit.CLUBS) to R.drawable.clubs_10,
            Card(Rank.JACK, Suit.CLUBS) to R.drawable.clubs_jack,
            Card(Rank.QUEEN, Suit.CLUBS) to R.drawable.clubs_queen,
            Card(Rank.KING, Suit.CLUBS) to R.drawable.clubs_king,
            Card(Rank.ACE, Suit.CLUBS) to R.drawable.clubs_ace,

            Card(Rank.SIX, Suit.DIAMONDS) to R.drawable.diamonds_6,
            Card(Rank.SEVEN, Suit.DIAMONDS) to R.drawable.diamonds_7,
            Card(Rank.EIGHT, Suit.DIAMONDS) to R.drawable.diamonds_8,
            Card(Rank.NINE, Suit.DIAMONDS) to R.drawable.diamonds_9,
            Card(Rank.TEN, Suit.DIAMONDS) to R.drawable.diamonds_10,
            Card(Rank.JACK, Suit.DIAMONDS) to R.drawable.diamonds_jack,
            Card(Rank.QUEEN, Suit.DIAMONDS) to R.drawable.diamonds_queen,
            Card(Rank.KING, Suit.DIAMONDS) to R.drawable.diamonds_king,
            Card(Rank.ACE, Suit.DIAMONDS) to R.drawable.diamonds_ace,

            Card(Rank.SIX, Suit.HEARTS) to R.drawable.heart_6,
            Card(Rank.SEVEN, Suit.HEARTS) to R.drawable.heart_7,
            Card(Rank.EIGHT, Suit.HEARTS) to R.drawable.heart_8,
            Card(Rank.NINE, Suit.HEARTS) to R.drawable.heart_9,
            Card(Rank.TEN, Suit.HEARTS) to R.drawable.heart_10,
            Card(Rank.JACK, Suit.HEARTS) to R.drawable.heart_jack,
            Card(Rank.QUEEN, Suit.HEARTS) to R.drawable.heart_queen,
            Card(Rank.KING, Suit.HEARTS) to R.drawable.heart_king,
            Card(Rank.ACE, Suit.HEARTS) to R.drawable.heart_ace,

            Card(Rank.SIX, Suit.SPADES) to R.drawable.spades_6,
            Card(Rank.SEVEN, Suit.SPADES) to R.drawable.spades_7,
            Card(Rank.EIGHT, Suit.SPADES) to R.drawable.spades_8,
            Card(Rank.NINE, Suit.SPADES) to R.drawable.spades_9,
            Card(Rank.TEN, Suit.SPADES) to R.drawable.spades_10,
            Card(Rank.JACK, Suit.SPADES) to R.drawable.spades_jack,
            Card(Rank.QUEEN, Suit.SPADES) to R.drawable.spades_queen,
            Card(Rank.KING, Suit.SPADES) to R.drawable.spades_king,
            Card(Rank.ACE, Suit.SPADES) to R.drawable.spades_ace,
        )

        /**
         * Returns the image id of the given card.
         */
        fun getCardImage(card: Card): Int {
            return cardImageMap[card]!!
        }
    }
}