package com.github.lucaengel.jass_entials.cards

enum class Suit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES,
}

enum class Rank {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE,
}

data class Card(
    val rank: Rank,
    val suit: Suit,
) {
    constructor() : this(Rank.ACE, Suit.CLUBS)

}