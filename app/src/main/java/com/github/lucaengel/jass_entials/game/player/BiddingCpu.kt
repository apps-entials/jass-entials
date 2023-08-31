package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState

interface BiddingCpu {

    /**
     * Returns a bet for the given [bettingState] and [handCards] or null if the cpu passes
     *
     * @param bettingState the current betting state
     * @param handCards the hand cards of the player
     * @return the bet or null if the cpu passes
     */
    fun bet(bettingState: BettingState, handCards: List<Card>): Bet?
}