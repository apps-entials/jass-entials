package com.github.lucaengel.jass_entials.cards

data class Game(
    val deck: Deck = Deck(),
    val players: List<Player>,
    val currentRound: Int,
    val currentTrick: Map<Player, Card> = mapOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Suit = Suit.CLUBS,
) {

}