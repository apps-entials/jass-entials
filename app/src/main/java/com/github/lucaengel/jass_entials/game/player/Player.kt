package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import java.util.concurrent.CompletableFuture

/**
 * A player in a game of Jass.
 */
interface Player {

    /**
     * Plays a card from the player's hand for the given game state.
     *
     * @param roundState the current game state
     * @return the card to play
     */
    fun cardToPlay(roundState: RoundState, handCards: List<Card>): CompletableFuture<Card>

    /**
     * Bets for the given betting state.
     *
     * @param bettingState the current betting state
     * @return the new betting state
     */
    fun bet(bettingState: BettingState, handCards: List<Card>): CompletableFuture<BettingState>

    /**
     * Returns whether or not the player wants to double the current bet.
     *
     * @param bet the current betting state
     * @return whether or not the player wants to double the current bet
     */
    fun wantsToDouble(bet: Bet, handCards: List<Card>): CompletableFuture<Boolean>
}