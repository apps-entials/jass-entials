package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet

interface BettingLogic {

    fun nextPlayer(currentPlayerBet: Bet): PlayerData

    fun availableActions(currentPlayerData: PlayerData): Bet.BetAction
}