package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

interface BettingLogic {

    /**
     * Returns the next player to bet.
     *
     * @param currentBetterId the player who is currently betting
     * @param currentPlayerBet the bet of the current player, possibly null (when passed or, in Sidi Barrani, when the player selected start game)
     * @param bettingState the current betting state (before adding the current player's bet)
     * @return the next player to bet
     */
    fun nextPlayer(currentBetterId: PlayerId, currentPlayerBet: Bet?, bettingState: BettingState): PlayerId

    /**
     * Returns the available actions for the current player.
     *
     * @param currentBetterId the player who is currently betting
     * @param bettingState the current betting state
     * @return the list of available actions
     */
    fun availableActions(currentBetterId: PlayerId, bettingState: BettingState): List<Bet.BetAction>

    /**
     * Returns the available trumps for the current player.
     *
     * @param currentBetterId the player who is currently betting
     * @param prevBets the list of all bets that have been placed
     * @return the list of available trumps
     */
    fun availableTrumps(currentBetterId: PlayerId, prevBets: List<Bet>): List<Trump>

    /**
     * Returns the player who starts the game.
     *
     * @param startingBetterId the player who started the betting round
     * @param winningBet the winning bet
     * @return the player id who starts the game
     */
    fun gameStartingPlayerId(startingBetterId: PlayerId, winningBet: Bet): PlayerId
}