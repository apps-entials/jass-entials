package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import java.util.concurrent.CompletableFuture

/**
 * A player in a game of Jass.
 */
interface Player {

    fun playCard(gameState: GameState): CompletableFuture<Card>

    fun bet(bettingState: BettingState): CompletableFuture<BettingState>

    fun chooseTrump(gameState: GameState)
}