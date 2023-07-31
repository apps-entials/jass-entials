package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.R
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

enum class Suit(val toString: String, val symbol: Char) : Serializable  {
    CLUBS("Clubs", '\u2663'),
    DIAMONDS("Diamonds", '\u2666'),
    SPADES("Spades", '\u2660'),
    HEARTS("Hearts", '\u2665'),
}

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

data class Card(
    val rank: Rank,
    val suit: Suit,
) : Serializable {
    constructor() : this(Rank.TEN, Suit.HEARTS)

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

        fun getCardImage(card: Card): Int {
            return cardImageMap[card]!!
        }
    }
}