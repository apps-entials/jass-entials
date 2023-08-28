package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.jass.JassType
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

        // TODO: This is a temporary solution for testing to not have the cpu wait
        //  consider refactoring this to a more elegant solution
        if (GameStateHolder.runCpuAsynchronously) {
            CompletableFuture.runAsync {
                Thread.sleep(threadSleepTime)

                future.complete(cpuPlayer.cardToPlay(roundState, handCards).join())
            }
        } else {
            // this is where tests run
            future.complete(cpuPlayer.cardToPlay(roundState, handCards).join())
        }

        return future
    }

    override fun bet(bettingState: BettingState, handCards: List<Card>): CompletableFuture<BettingState> {
        val betFuture = CompletableFuture<BettingState>()

        // TODO: This is a temporary solution for testing to not have the cpu wait
        //  consider refactoring this to a more elegant solution
        if (GameStateHolder.runCpuAsynchronously) {
            CompletableFuture.runAsync {
                Thread.sleep((if (bettingState.jassType == JassType.SIDI_BARRANI) 6 else 3) * threadSleepTime)

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