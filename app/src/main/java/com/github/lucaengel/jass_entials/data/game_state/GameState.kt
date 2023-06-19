package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

data class GameState(
    val players: List<Player> = listOf(),
    val currentPlayer: Player,
    val currentRound: Int,
    val currentTrick: Map<Player, Card> = mapOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Trump = Trump.CLUBS,
    val playerCards: Map<Player, List<Card>> = mapOf(),
) : Serializable {

    constructor() : this(
        players = listOf(),
        currentPlayer = Player(),
        currentRound = 0,
        currentTrick = mapOf(),
        currentTrickNumber = 0,
        currentTrump = Trump.CLUBS,
        playerCards = mapOf(),
    )
    fun nextTrick(): GameState {
        if (currentTrickNumber == 9)
            return this.nextRound()

        return this.copy(
            currentTrick = mapOf(),
            currentTrickNumber = currentTrickNumber + 1,
        )
    }

    private fun nextRound(): GameState {
        return this.copy(
            currentRound = currentRound + 1,
            playerCards = Deck.STANDARD_DECK.shuffled().dealCards(players),
        )
    }

    fun playCard(player: Player, card: Card): GameState {
        return this.copy(
            currentTrick = currentTrick + (player to card),
        )
    }

    fun setTrump(trump: Trump): GameState {
        return this.copy(
            currentTrump = trump,
        )
    }
}