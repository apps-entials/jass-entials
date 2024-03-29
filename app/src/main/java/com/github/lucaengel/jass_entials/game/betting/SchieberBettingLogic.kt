package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

class SchieberBettingLogic : BettingLogic {

    override fun nextPlayer(
        currentBetterId: PlayerId,
        currentPlayerBet: Bet?,
        bettingState: BettingState
    ): PlayerId {
        if (currentPlayerBet != null) return currentBetterId

        return currentBetterId.teamMate()
    }

    override fun availableActions(
        currentBetterId: PlayerId,
        bettingState: BettingState
    ): List<Bet.BetAction> {
        if (bettingState.betActions.size >= 2) // both players passed
            return listOf(Bet.BetAction.BET)

        return listOf(Bet.BetAction.BET, Bet.BetAction.PASS)
    }

    override fun availableTrumps(currentBetterId: PlayerId, prevBets: List<Bet>): List<Trump> {
        return Trump.values().toList()
    }

    override fun gameStartingPlayerId(startingBetterId: PlayerId, winningBet: Bet): PlayerId {
        return startingBetterId
    }
}