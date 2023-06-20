package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

data class GameState(
    val players: List<Player> = listOf(),
    val currentPlayer: Player, // player that has to play the next card
    val startingPlayer: Player, // player that started the current trick
    val currentRound: Int,
    val currentTrick: Map<Player, Card> = mapOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Trump = Trump.CLUBS,
    val playerCards: Map<Player, List<Card>> = mapOf(),
) : Serializable {

    constructor() : this(
        players = listOf(),
        currentPlayer = Player(),
        startingPlayer = Player(),
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

        val newPlayer = player.copy(cards = player.cards.filter { c -> c != card })
        return this.copy(
            currentTrick = currentTrick + (newPlayer to card),
            players = players.map { if (it == player) newPlayer else it },
            playerCards = playerCards.map { if (it.key == player) newPlayer to newPlayer.cards else it.key to it.value }.toMap(),
        )
    }

    fun setTrump(trump: Trump): GameState {
        return this.copy(
            currentTrump = trump,
        )
    }
}