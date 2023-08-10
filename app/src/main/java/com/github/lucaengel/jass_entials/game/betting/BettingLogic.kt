package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState

interface BettingLogic {

    fun nextPlayer(currentBetterEmail: String, currentPlayerBet: Bet?, bettingState: BettingState): String

    fun availableActions(currentPlayerEmail: String, bettingState: BettingState): List<Bet.BetAction>
}