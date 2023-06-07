package com.github.lucaengel.jass_entials.data.cards

enum class Suit(val toString: String, val symbol: Char) {
    CLUBS("Clubs", '\u2663'),
    DIAMONDS("Diamonds", '\u2666'),
    HEARTS("Hearts", '\u2665'),
    SPADES("Spades", '\u2660'),
}

enum class Rank(val toString: String) {
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A"),
}

data class Card(
    val rank: Rank,
    val suit: Suit,
) {
    constructor() : this(Rank.ACE, Suit.CLUBS)

}