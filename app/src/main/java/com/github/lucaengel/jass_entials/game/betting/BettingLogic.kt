package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId

interface BettingLogic {

    /**
     * Returns the next player to bet.
     *
     * @param currentBetter the player who is currently betting
     * @param currentPlayerBet the bet of the current player, possibly null (when passed or, in Sidi Barahni, when the player selected start game)
     * @param bettingState the current betting state (before adding the current player's bet)
     */
    fun nextPlayer(currentBetter: PlayerId, currentPlayerBet: Bet?, bettingState: BettingState): PlayerId

    /**
     * Returns the available actions for the current player.
     *
     * @param currentBetter the player who is currently betting
     * @param bettingState the current betting state
     */
    fun availableActions(currentBetter: PlayerId, bettingState: BettingState): List<Bet.BetAction>
}