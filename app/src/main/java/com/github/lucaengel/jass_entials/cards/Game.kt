package com.github.lucaengel.jass_entials.cards

data class Game(
    val deck: Deck = Deck(),
    val players: List<Player> = listOf(),
    val currentRound: Int,
    val currentTrick: Map<Player, Card> = mapOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Suit = Suit.CLUBS,
) {
    fun nextTrick(): Game {
        if (currentTrickNumber == 9)
            return this.nextRound()

        return this.copy(
            currentTrick = mapOf(),
            currentTrickNumber = currentTrickNumber + 1,
        )
    }

    private fun nextRound(): Game {
        return this.copy(
            currentRound = currentRound + 1,
            deck = Deck.STANDARD_DECK.shuffled(),
        )
    }

    fun playCard(player: Player, card: Card): Game {
        return this.copy(
            currentTrick = currentTrick + (player to card),
        )
    }

    fun setTrump(suit: Suit): Game {
        return this.copy(
            currentTrump = suit,
        )
    }
}