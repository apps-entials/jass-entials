package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

class CoiffeurBettingLogic : BettingLogic {

    override fun nextPlayer(
        currentBetterId: PlayerId,
        currentPlayerBet: Bet?,
        bettingState: BettingState
    ): PlayerId {
        if (currentPlayerBet != null) return currentBetterId

        return currentBetterId.nextPlayer()
    }

    override fun availableActions(
        currentBetterId: PlayerId,
        bettingState: BettingState
    ): List<Bet.BetAction> {
        if (Trump.values().size ==
            (GameStateHolder.prevTrumpsByTeam[currentBetterId.teamId()]?.size
                ?: 0)
        ) // already made all trumps --> other team has to make the rest of their trumps
            return listOf(Bet.BetAction.PASS)

        // everyone passed once --> cstarting better must make trump
        if (bettingState.betActions.size >= 4)
            return listOf(Bet.BetAction.BET)

        return listOf(Bet.BetAction.BET, Bet.BetAction.PASS)
    }

    override fun availableTrumps(currentBetterId: PlayerId, prevBets: List<Bet>): List<Trump> {
        val prevTrumps = GameStateHolder.prevTrumpsByTeam[currentBetterId.teamId()]
            ?: emptySet()
        return Trump.values().toList().filter {
            !prevTrumps.contains(it)
        }
    }

    override fun gameStartingPlayerId(startingBetterId: PlayerId, winningBet: Bet): PlayerId {
        return startingBetterId
    }

    companion object {
        fun factorForTrump(trump: Trump): Int {
            return when (trump) {
                Trump.DIAMONDS -> 1
                Trump.HEARTS -> 2
                Trump.SPADES -> 3
                Trump.CLUBS -> 4
                Trump.UNGER_UFE -> 5
                Trump.OBE_ABE -> 6
            }
        }
    }
}