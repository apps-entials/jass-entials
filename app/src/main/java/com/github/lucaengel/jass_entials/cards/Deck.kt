package com.github.lucaengel.jass_entials.cards

data class Deck(val cards: List<Card> = listOf()) {

    fun shuffled(): Deck {
        return this.copy(cards = cards.shuffled())
    }
}