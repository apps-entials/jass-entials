package com.github.lucaengel.jass_entials.data.cards

data class Deck(val cards: List<Card> = listOf()) {

    constructor() : this(listOf())

    fun shuffled(): Deck {
        return this.copy(cards = cards.shuffled())
    }

    fun dealCards(players: List<Player>): Map<Player, List<Card>> {
        return players.zip(cards.chunked(9)).toMap()
    }

    companion object {
        val STANDARD_DECK = Deck(
            Suit.values().flatMap { suit ->
                Rank.values().map { rank ->
                    Card(rank, suit)
                }
            }
        )
    }
}