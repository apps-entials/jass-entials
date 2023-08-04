package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.PlayerData

/**
 * Class representing a local player
 *
 * @property playerData the player data
 */
class LocalPlayer(private val playerData: PlayerData) /*: Player*/ {

    /*override fun playCard(gameState: GameState): CompletableFuture<Card> {
        return CompletableFuture.completedFuture(playerData.playableCards(gameState.currentTrick, gameState.currentTrump).random())
    }

    override fun bet(bettingState: BettingState): CompletableFuture<BettingState> {
        TODO()
    }

    override fun chooseTrump(gameState: GameState): CompletableFuture<Trump> {
        TODO()
    }*/
}