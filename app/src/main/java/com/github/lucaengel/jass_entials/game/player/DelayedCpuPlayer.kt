package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

class DelayedCpuPlayer(
    val playerId: PlayerId,
    seed: Long = Random.nextLong(),
    nbSimulations: Int = 600,
    private val threadSleepTime: Long = 300
) : Player {

    private val cpuPlayer = CpuPlayer(
        playerId = playerId,
        seed = seed,
        nbSimulations = nbSimulations
    )
    override fun cardToPlay(
        roundState: RoundState,
        handCards: List<Card>
    ): CompletableFuture<Card> {
        val cardFuture = CompletableFuture<Card>()
        val sleepFuture = CompletableFuture<Void>()

        // TODO: This is a temporary solution for testing to not have the cpu wait
        //  consider refactoring this to a more elegant solution
        if (GameStateHolder.runCpuAsynchronously) {
            CompletableFuture.runAsync {
                Thread.sleep(threadSleepTime)

                sleepFuture.complete(null)
            }
            CompletableFuture.runAsync {
//                Thread.sleep(threadSleepTime)

                cardFuture.complete(cpuPlayer.cardToPlay(roundState, handCards).join())
            }
        } else {
            // this is where tests run
            sleepFuture.complete(null)

            cardFuture.complete(cpuPlayer.cardToPlay(roundState, handCards).join())
        }
        val result = CompletableFuture<Card>()

        // once sleepFuture and cardFuture have completed, return the card
        sleepFuture.thenAcceptBoth(cardFuture) { _, card -> result.complete(card) }

        return result
    }

    override fun bet(bettingState: BettingState, handCards: List<Card>): CompletableFuture<BettingState> {
        val betFuture = CompletableFuture<BettingState>()

        // TODO: This is a temporary solution for testing to not have the cpu wait
        //  consider refactoring this to a more elegant solution
        if (GameStateHolder.runCpuAsynchronously) {
            CompletableFuture.runAsync {
                Thread.sleep(3 * threadSleepTime)

                betFuture.complete(cpuPlayer.bet(bettingState, handCards).join())
            }
        } else {
            // this is where tests run
            betFuture.complete(cpuPlayer.bet(bettingState, handCards).join())
        }

        return betFuture
    }

    override fun wantsToDouble(bet: Bet, handCards: List<Card>): CompletableFuture<Boolean> {
        return cpuPlayer.wantsToDouble(bet, handCards)
    }
}