package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.R

enum class Suit(val toString: String, val symbol: Char) {
    CLUBS("Clubs", '\u2663'),
    DIAMONDS("Diamonds", '\u2666'),
    HEARTS("Hearts", '\u2665'),
    SPADES("Spades", '\u2660'),
}

enum class Rank(private val rank: String, normalHeight: Int, val trumpHeight: Int) {
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
) {
    constructor() : this(Rank.TEN, Suit.HEARTS)

    override fun toString(): String {
        return "$rank${suit.symbol}"
    }
    companion object {
        private val cardImageMap = mapOf(
//            Card(Rank.SIX, Suit.CLUBS) to R.drawable.club_six,
//            Card(Rank.SEVEN, Suit.CLUBS) to R.drawable.club_seven,
//            Card(Rank.EIGHT, Suit.CLUBS) to R.drawable.club_eight,
//            Card(Rank.NINE, Suit.CLUBS) to R.drawable.club_nine,
//            Card(Rank.TEN, Suit.CLUBS) to R.drawable.club_ten,
//            Card(Rank.JACK, Suit.CLUBS) to R.drawable.club_jack,
//            Card(Rank.QUEEN, Suit.CLUBS) to R.drawable.club_queen,
//            Card(Rank.KING, Suit.CLUBS) to R.drawable.club_king,
//            Card(Rank.ACE, Suit.CLUBS) to R.drawable.club_ace,
//
//            Card(Rank.SIX, Suit.DIAMONDS) to R.drawable.diamond_six,
//            Card(Rank.SEVEN, Suit.DIAMONDS) to R.drawable.diamond_seven,
//            Card(Rank.EIGHT, Suit.DIAMONDS) to R.drawable.diamond_eight,
//            Card(Rank.NINE, Suit.DIAMONDS) to R.drawable.diamond_nine,
//            Card(Rank.TEN, Suit.DIAMONDS) to R.drawable.diamond_ten,
//            Card(Rank.JACK, Suit.DIAMONDS) to R.drawable.diamond_jack,
//            Card(Rank.QUEEN, Suit.DIAMONDS) to R.drawable.diamond_queen,
//            Card(Rank.KING, Suit.DIAMONDS) to R.drawable.diamond_king,
//            Card(Rank.ACE, Suit.DIAMONDS) to R.drawable.diamond_ace,
//
//            Card(Rank.SIX, Suit.HEARTS) to R.drawable.heart_six,
//            Card(Rank.SEVEN, Suit.HEARTS) to R.drawable.heart_seven,
//            Card(Rank.EIGHT, Suit.HEARTS) to R.drawable.heart_eight,
//            Card(Rank.NINE, Suit.HEARTS) to R.drawable.heart_nine,
//            Card(Rank.TEN, Suit.HEARTS) to R.drawable.heart_ten,
//            Card(Rank.JACK, Suit.HEARTS) to R.drawable.heart_jack,
//            Card(Rank.QUEEN, Suit.HEARTS) to R.drawable.heart_queen,
            Card(Rank.KING, Suit.HEARTS) to R.drawable.heart_king,
//            Card(Rank.ACE, Suit.HEARTS) to R.drawable.heart_ace,
//
//            Card(Rank.SIX, Suit.SPADES) to R.drawable.spade_six,
//            Card(Rank.SEVEN, Suit.SPADES) to R.drawable.spade_seven,
//            Card(Rank.EIGHT, Suit.SPADES) to R.drawable.spade_eight,
//            Card(Rank.NINE, Suit.SPADES) to R.drawable.spade_nine,
//            Card(Rank.TEN, Suit.SPADES) to R.drawable.spade_ten,
//            Card(Rank.JACK, Suit.SPADES) to R.drawable.spade_jack,
//            Card(Rank.QUEEN, Suit.SPADES) to R.drawable.spade_queen,
//            Card(Rank.KING, Suit.SPADES) to R.drawable.spade_king,
//            Card(Rank.ACE, Suit.SPADES) to R.drawable.spade_ace,
        )

        fun getCardImage(card: Card): Int {
//            return cardImageMap[card]!!
            return cardImageMap[Card(Rank.KING, Suit.HEARTS)]!!
        }
    }
}