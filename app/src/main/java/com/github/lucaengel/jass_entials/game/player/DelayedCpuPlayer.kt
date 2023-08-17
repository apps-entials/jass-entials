package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

class DelayedCpuPlayer(
    val playerId: PlayerId,
    seed: Long = Random.nextLong(),
    nbSimulations: Int = 100,
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
        val future = CompletableFuture<Card>()

        CompletableFuture.runAsync {
            Thread.sleep(threadSleepTime)

            future.complete(cpuPlayer.cardToPlay(roundState, handCards).join())
        }

        return future
    }

    override fun bet(bettingState: BettingState, handCards: List<Card>): CompletableFuture<BettingState> {
        val betFuture = CompletableFuture<BettingState>()
        CompletableFuture.runAsync {
            Thread.sleep(3 * threadSleepTime)

            betFuture.complete(cpuPlayer.bet(bettingState, handCards).join())
        }
        return betFuture
    }
}