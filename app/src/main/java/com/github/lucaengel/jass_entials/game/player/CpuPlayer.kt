package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * Class representing a CPU player
 *
 * @property playerId the player data
 */
class CpuPlayer(
    val playerId: PlayerId,
    val seed: Long = Random.nextLong(),
    nbSimulations: Int = 9
) : Player {

    private val monteCarloCpu = MonteCarloCardPlayer(playerId, seed, nbSimulations)

    override fun bet(bettingState: BettingState): CompletableFuture<BettingState> {

        if ((bettingState.availableActions().contains(Bet.BetAction.PASS))
            && (Random.nextFloat() > 0.2 || bettingState.availableBets().isEmpty())) {

            return CompletableFuture.completedFuture(bettingState.nextPlayer())

        // if player can start the game, do it with a 80% chance
        // if player has to start the game, do it (available actions only contains start game)
        } else if (bettingState.availableActions().contains(Bet.BetAction.START_GAME)
            && (Random.nextFloat() > 0.2 || bettingState.availableActions().size == 1)) {

            return CompletableFuture.completedFuture(bettingState.nextPlayer())
        } else {
            return CompletableFuture.completedFuture(
                bettingState.nextPlayer(
                    Bet(
                        playerId = playerId,
                        bet = bettingState.availableBets().first(),
                        trump = bettingState.availableTrumps().random(),
                    )
                )
            )
        }
    }

    override fun cardToPlay(roundState: RoundState, handCards: List<Card>): CompletableFuture<Card> {
        return CompletableFuture.completedFuture(
            monteCarloCpu.monteCarloCardToPlay(
                roundState = roundState,
                handCards = handCards
            )
        )
    }
}
