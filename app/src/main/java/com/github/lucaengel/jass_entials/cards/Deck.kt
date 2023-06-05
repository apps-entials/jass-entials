package com.github.lucaengel.jass_entials.cards

data class Deck(val cards: List<Card> = listOf()) {

    fun shuffled(): Deck {
        return this.copy(cards = cards.shuffled())
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