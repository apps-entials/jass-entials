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
 * @property playerEmail the player data
 */
class CpuPlayer(val playerEmail: String, private val threadSleepTime: Long = 300) : Player {

    override fun playCard(gameState: GameState, player: PlayerData): CompletableFuture<Card> {
        val card = player.playableCards(gameState.currentTrick, gameState.currentTrump).random()

        // TODO: maybe update player data here and return it as well

        val cardFuture = CompletableFuture<Card>()
        CompletableFuture.runAsync {
            Thread.sleep(threadSleepTime)
            cardFuture.complete(card)
        }

        return cardFuture
    }

    override fun bet(bettingState: BettingState): CompletableFuture<BettingState> {

        val bettingFuture = CompletableFuture<BettingState>()
        CompletableFuture.runAsync {
            Thread.sleep(3*threadSleepTime)

            if ((bettingState.availableActions().contains(Bet.BetAction.PASS))
                && (Random.nextFloat() > 0.2 || bettingState.availableBets().isEmpty())) {

                bettingFuture.complete(bettingState.nextPlayer())
            // if player can start the game, do it with a 80% chance
            // if player has to start the game, do it (available actions only contains start game)
            } else if (bettingState.availableActions().contains(Bet.BetAction.START_GAME)
                && (Random.nextFloat() > 0.2 || bettingState.availableActions().size == 1)) {

                bettingFuture.complete(bettingState.nextPlayer())
            } else {
                bettingFuture.complete(
                    bettingState.nextPlayer(
                        Bet(
                            playerEmail = playerEmail,
                            bet = bettingState.availableBets().first(),
                            suit = bettingState.availableTrumps().random(),
                        )
                    )
                )
            }
        }
        return bettingFuture
    }

    override fun chooseTrump(gameState: GameState): CompletableFuture<Trump> {
        TODO("Not yet implemented")
    }
}