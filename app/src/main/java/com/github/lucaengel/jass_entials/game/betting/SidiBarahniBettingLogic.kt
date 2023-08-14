package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId

class SidiBarahniBettingLogic : BettingLogic {

    override fun nextPlayer(
        currentBetter: PlayerId,
        currentPlayerBet: Bet?,
        bettingState: BettingState
    ): PlayerId {
        // Also here, starting the game is indicated by passing after having the last bet
        if (bettingState.bets.lastOrNull()?.playerId == currentBetter
            && currentPlayerBet == null)
            return currentBetter

        return currentBetter.nextPlayer()
    }

    override fun availableActions(
        currentBetter: PlayerId,
        bettingState: BettingState
    ): List<Bet.BetAction> {
        val lastBet = bettingState.bets
            .lastOrNull()
            ?: return listOf(Bet.BetAction.BET, Bet.BetAction.PASS)

        val listBuilder = mutableListOf<Bet.BetAction>()

        // can bet if the last bet was not a match
        if (lastBet.bet != BetHeight.MATCH) {
            listBuilder.add(Bet.BetAction.BET)
        }

        // can pass if the last bet was not from the current player
        if (lastBet.playerId != currentBetter) {
            listBuilder.add(Bet.BetAction.PASS)
        }

        // can start game if the last bet was from the current player
        if (lastBet.playerId == currentBetter) {
            listBuilder.add(Bet.BetAction.START_GAME)
        }

        // can double if a member of the opposing team bet
        if (lastBet.playerId != currentBetter.teamMate()
            && lastBet.playerId != currentBetter
        ) {
            listBuilder.add(Bet.BetAction.DOUBLE)
        }

        return listBuilder.toList()
    }
}