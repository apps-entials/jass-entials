package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture

class CpuPlayer(var playerData: PlayerData) : Player {

    override fun playCard(gameState: GameState): CompletableFuture<Card> {
        println("player cards: ${playerData.cards}")

        val card = playerData.playableCards(gameState.currentTrick, gameState.currentTrump).random()
        playerData = playerData.copy(cards = playerData.cards.filter { c -> c != card })
        return CompletableFuture.completedFuture(card)
    }

    override fun bet(bettingState: BettingState) {
        TODO("Not yet implemented")
    }

    override fun chooseTrump(gameState: GameState) {
        TODO("Not yet implemented")
    }
}