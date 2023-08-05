package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * Class representing a CPU player
 *
 * @property playerEmail the player data
 */
class CpuPlayer(val playerEmail: String, private val threadSleepTime: Long = 300) : Player {
    private val idx = GameStateHolder.players.indexOfFirst { it.email == playerEmail }

    override fun playCard(gameState: GameState): CompletableFuture<Card> {
        val player = GameStateHolder.players[idx]
        val card = player.playableCards(gameState.currentTrick, gameState.currentTrump).random()
        val newPlayer = player.copy(cards = player.cards.minus(card))

        GameStateHolder.players = GameStateHolder.players.map {
            if (it.email == playerEmail) newPlayer else it
        }

        val cardFuture = CompletableFuture<Card>()
        CompletableFuture.runAsync {
            Thread.sleep(threadSleepTime)
            cardFuture.complete(card)
        }

        return cardFuture
    }

    override fun bet(bettingState: BettingState): CompletableFuture<BettingState> {
        var firstName = ""
        var lastName = ""
        GameStateHolder.players = GameStateHolder.players.map {
            if (it.email == playerEmail) {
                firstName = it.firstName
                lastName = it.lastName
                it.copy(firstName = "I'm", lastName = "thinking...")
            } else {
                it
            }
        }

        val bettingFuture = CompletableFuture<BettingState>()
        CompletableFuture.runAsync {
            Thread.sleep(3*threadSleepTime)

            if (Random.nextFloat() > 0.2 || bettingState.availableBets().isEmpty()) {
                bettingFuture.complete(bettingState.nextPlayer())
            } else {
                bettingFuture.complete(
                    bettingState.nextPlayer(
                        Bet(
                            playerEmail = playerEmail,
                            bet = bettingState.availableBets().first(),
                            suit = bettingState.availableTrumps().first(),
                        )
                    )
                )
            }
        }
        return bettingFuture.thenApply { bState ->
            GameStateHolder.players = GameStateHolder.players.map {
                if (it.email == playerEmail) {
                    it.copy(firstName = firstName, lastName = lastName)
                } else {
                    it
                }
            }

            bState
        }
    }

    override fun chooseTrump(gameState: GameState): CompletableFuture<Trump> {
        TODO("Not yet implemented")
    }
}