package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState

class SidiBarahniBettingLogic : BettingLogic {
    override fun nextPlayer(
        currentBetterEmail: String,
        currentPlayerBet: Bet?,
        bettingState: BettingState
    ): String {
        // Also here, starting the game is indicated by passing after having the last bet
        if (bettingState.bets.lastOrNull()?.playerEmail == currentBetterEmail && currentPlayerBet == null)
            return currentBetterEmail

        val idx = bettingState.playerEmails.indexOf(currentBetterEmail)
        return bettingState.playerEmails[(idx + 1) % bettingState.playerEmails.size]
    }

    override fun availableActions(
        currentPlayerEmail: String,
        bettingState: BettingState
    ): List<Bet.BetAction> {
        val lastBet = bettingState.bets
            .lastOrNull()
            ?: return listOf(Bet.BetAction.BET, Bet.BetAction.PASS)

        if (lastBet.playerEmail == currentPlayerEmail) {
            if (lastBet.bet == BetHeight.MATCH)
                return listOf(Bet.BetAction.START_GAME)

            return listOf(Bet.BetAction.BET, Bet.BetAction.START_GAME)
        }

        return listOf(Bet.BetAction.BET, Bet.BetAction.PASS)
    }
}