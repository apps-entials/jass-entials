package com.github.lucaengel.jass_entials.cards

enum class Suit(val toString: String) {
    CLUBS("Clubs"),
    DIAMONDS("Diamonds"),
    HEARTS("Hearts"),
    SPADES("Spades"),
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