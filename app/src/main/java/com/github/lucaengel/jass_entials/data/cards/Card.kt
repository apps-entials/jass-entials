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
enum class Suit(private val toString: String, val symbol: Char) : Serializable  {
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
enum class Rank(private val rank: String, private val normalHeight: Int, val trumpHeight: Int) : Serializable  {
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
    val suit: Suit,
    val rank: Rank,
) : Serializable {
    constructor() : this(Suit.HEARTS, Rank.TEN)

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
            Card(Suit.CLUBS, Rank.SIX) to R.drawable.clubs_6,
            Card(Suit.CLUBS, Rank.SEVEN) to R.drawable.clubs_7,
            Card(Suit.CLUBS, Rank.EIGHT) to R.drawable.clubs_8,
            Card(Suit.CLUBS, Rank.NINE) to R.drawable.clubs_9,
            Card(Suit.CLUBS, Rank.TEN) to R.drawable.clubs_10,
            Card(Suit.CLUBS, Rank.JACK) to R.drawable.clubs_jack,
            Card(Suit.CLUBS, Rank.QUEEN) to R.drawable.clubs_queen,
            Card(Suit.CLUBS, Rank.KING) to R.drawable.clubs_king,
            Card(Suit.CLUBS, Rank.ACE) to R.drawable.clubs_ace,

            Card(Suit.DIAMONDS, Rank.SIX) to R.drawable.diamonds_6,
            Card(Suit.DIAMONDS, Rank.SEVEN) to R.drawable.diamonds_7,
            Card(Suit.DIAMONDS, Rank.EIGHT) to R.drawable.diamonds_8,
            Card(Suit.DIAMONDS, Rank.NINE) to R.drawable.diamonds_9,
            Card(Suit.DIAMONDS, Rank.TEN) to R.drawable.diamonds_10,
            Card(Suit.DIAMONDS, Rank.JACK) to R.drawable.diamonds_jack,
            Card(Suit.DIAMONDS, Rank.QUEEN) to R.drawable.diamonds_queen,
            Card(Suit.DIAMONDS, Rank.KING) to R.drawable.diamonds_king,
            Card(Suit.DIAMONDS, Rank.ACE) to R.drawable.diamonds_ace,

            Card(Suit.HEARTS, Rank.SIX) to R.drawable.heart_6,
            Card(Suit.HEARTS, Rank.SEVEN) to R.drawable.heart_7,
            Card(Suit.HEARTS, Rank.EIGHT) to R.drawable.heart_8,
            Card(Suit.HEARTS, Rank.NINE) to R.drawable.heart_9,
            Card(Suit.HEARTS, Rank.TEN) to R.drawable.heart_10,
            Card(Suit.HEARTS, Rank.JACK) to R.drawable.heart_jack,
            Card(Suit.HEARTS, Rank.QUEEN) to R.drawable.heart_queen,
            Card(Suit.HEARTS, Rank.KING) to R.drawable.heart_king,
            Card(Suit.HEARTS, Rank.ACE) to R.drawable.heart_ace,

            Card(Suit.SPADES, Rank.SIX) to R.drawable.spades_6,
            Card(Suit.SPADES, Rank.SEVEN) to R.drawable.spades_7,
            Card(Suit.SPADES, Rank.EIGHT) to R.drawable.spades_8,
            Card(Suit.SPADES, Rank.NINE) to R.drawable.spades_9,
            Card(Suit.SPADES, Rank.TEN) to R.drawable.spades_10,
            Card(Suit.SPADES, Rank.JACK) to R.drawable.spades_jack,
            Card(Suit.SPADES, Rank.QUEEN) to R.drawable.spades_queen,
            Card(Suit.SPADES, Rank.KING) to R.drawable.spades_king,
            Card(Suit.SPADES, Rank.ACE) to R.drawable.spades_ace,
        )

        /**
         * Returns the image id of the given card.
         */
        fun getCardImage(card: Card): Int {
            return cardImageMap[card]!!
        }
    }
}