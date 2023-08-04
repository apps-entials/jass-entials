package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * Class representing a CPU player
 *
 * @property playerData the player data
 */
class CpuPlayer(var playerData: PlayerData) : Player {

    override fun playCard(gameState: GameState): CompletableFuture<Card> {
        val card = playerData.playableCards(gameState.currentTrick, gameState.currentTrump).random()
        playerData = playerData.copy(cards = playerData.cards.filter { c -> c != card })

        val cardFuture = CompletableFuture<Card>()
        CompletableFuture.runAsync {
            Thread.sleep(300)
            cardFuture.complete(card)
        }

        return cardFuture
    }

    override fun bet(bettingState: BettingState): CompletableFuture<BettingState> {
        if (Random.nextFloat() > 0.1 || bettingState.availableBets().isEmpty()) return CompletableFuture.completedFuture(bettingState.nextPlayer())

        return CompletableFuture.completedFuture(
            bettingState.nextPlayer(
                Bet(
                    playerData = playerData,
                    bet = bettingState.availableBets().first(),
                    suit = bettingState.availableTrumps().first(),
                )
            )
        )
    }

    override fun chooseTrump(gameState: GameState): CompletableFuture<Trump> {
        TODO("Not yet implemented")
    }
}