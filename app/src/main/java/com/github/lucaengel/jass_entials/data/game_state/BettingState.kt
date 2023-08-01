package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassTypes
import com.github.lucaengel.jass_entials.data.jass.Trump

data class BettingState(
    val playerData: List<PlayerData>,
    val currentBetter: PlayerData,
    val jassType: JassTypes,
    val bets: List<Bet>,
    val gameState: GameState,
){

    constructor(): this(
        playerData = listOf(PlayerData(), PlayerData(), PlayerData(), PlayerData()),
        currentBetter = PlayerData(),
        jassType = JassTypes.SCHIEBER,
        bets = listOf(),
        gameState = GameState(),
    )

    fun nextPlayer(placedBet: Bet? = null): BettingState {


        val nextPlayerIdx = (playerData.indexOf(currentBetter) + 1) % playerData.size
        return this.copy(
            currentBetter = playerData[nextPlayerIdx],
            bets = if (placedBet != null) bets + placedBet else bets,
        )
    }

    fun availableBets(): List<Int> {
        return (1..12)
                .map { 10*it + 30 }
            .filter { it > (bets.lastOrNull()?.bet ?: 0) }
    }

    fun availableTrumps(): List<Trump> {
        if (bets.lastOrNull()?.playerData == currentBetter) {
            return Trump.values().filter { it != bets.last().suit }
        }

        return Trump.values().toList()
    }

    fun startGame(currentPlayerIdx: Int): GameState { // TODO: make sure to have the same random seed for every player!!!
        if (bets.isEmpty()) // TODO: handle this case better! (restart betting phase)
            throw IllegalStateException("Cannot start game without bets")

        println("betting state current player: ${playerData[currentPlayerIdx]}")

        return GameState(
            currentPlayerIdx,
            playerDatas = playerData,
            currentPlayerData = bets.last().playerData,
            startingPlayerData = bets.last().playerData,
            currentRound = 0,
            currentTrick = Trick(),
            currentTrickNumber = 0,
            currentTrump = Trump.CLUBS,
            playerCards = playerData.zip(playerData.map { it.cards }).toMap(),
        )
    }
}

data class Bet(val playerData: PlayerData, val suit: Trump, val bet: Int)
