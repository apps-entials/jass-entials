package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.jass.JassTypes

data class BettingState(
    val players: List<Player>,
    val currentBetter: Player,
    val jassType: JassTypes,
    val bets: List<Bet>,
    val gameState: GameState,
) {

    fun nextPlayer(): BettingState {
        val nextPlayerIdx = (players.indexOf(currentBetter) + 1) % players.size
        return this.copy(
            currentBetter = players[nextPlayerIdx],
        )
    }

    fun startGame(): GameState { // TODO: make sure to have the same random seed for every player!!!
        if (bets.isEmpty()) // TODO: handle this case better! (restart betting phase)
            throw IllegalStateException("Cannot start game without bets")

        return GameState(
            players = players,
            currentPlayer = bets.last().player,
            currentRound = 0,
            currentTrick = mapOf(),
            currentTrickNumber = 0,
            currentTrump = Suit.CLUBS,
            playerCards = Deck.STANDARD_DECK.shuffled().dealCards(players),
        )
    }
}

data class Bet(val player: Player, val suit: Suit, val bet: Int)
