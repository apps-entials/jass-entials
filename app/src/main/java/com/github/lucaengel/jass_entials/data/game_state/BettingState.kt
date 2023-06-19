package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.jass.JassTypes
import com.github.lucaengel.jass_entials.data.jass.Trump

data class BettingState(
    val players: List<Player>,
    val currentBetter: Player,
    val jassType: JassTypes,
    val bets: List<Bet>,
    val gameState: GameState,
){

    constructor(): this(
        players = listOf(Player(), Player(), Player(), Player()),
        currentBetter = Player(),
        jassType = JassTypes.SCHIEBER,
        bets = listOf(),
        gameState = GameState(),
    )

    fun nextPlayer(placedBet: Bet? = null): BettingState {


        val nextPlayerIdx = (players.indexOf(currentBetter) + 1) % players.size
        return this.copy(
            currentBetter = players[nextPlayerIdx],
            bets = if (placedBet != null) bets + placedBet else bets,
        )
    }

    fun availableBets(): List<Int> {
        return (1..12)
                .map { 10*it + 30 }
            .filter { it > (bets.lastOrNull()?.bet ?: 0) }
    }

    fun availableTrumps(): List<Trump> {
        if (bets.lastOrNull()?.player == currentBetter) {
            return Trump.values().filter { it != bets.last().suit }
        }

        return Trump.values().toList()
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
            currentTrump = Trump.CLUBS,
            playerCards = Deck.STANDARD_DECK.shuffled().dealCards(players),
        )
    }
}

data class Bet(val player: Player, val suit: Trump, val bet: Int)
