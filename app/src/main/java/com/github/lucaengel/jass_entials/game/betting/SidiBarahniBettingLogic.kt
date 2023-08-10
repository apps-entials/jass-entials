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
        if (bettingState.bets.lastOrNull()?.playerEmail == currentBetterEmail
            && currentPlayerBet == null)
            return currentBetterEmail

        val idx = bettingState.playerEmails.indexOf(currentBetterEmail)
        return bettingState.playerEmails[(idx + 1) % bettingState.playerEmails.size]
    }

    override fun availableActions(
        currentBetterEmail: String,
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
        if (lastBet.playerEmail != currentBetterEmail) {
            listBuilder.add(Bet.BetAction.PASS)
        }

        // can start game if the last bet was from the current player
        if (lastBet.playerEmail == currentBetterEmail) {
            listBuilder.add(Bet.BetAction.START_GAME)
        }

        // can double if a member of the opposing team bet
        if (lastBet.playerEmail != bettingState.playerEmails[(bettingState.playerEmails.indexOf(currentBetterEmail) + 2) % 4]
            && lastBet.playerEmail != currentBetterEmail) {
            listBuilder.add(Bet.BetAction.DOUBLE)
        }

        return listBuilder.toList()
    }
}