    package com.github.lucaengel.jass_entials.game.betting

    import com.github.lucaengel.jass_entials.data.game_state.Bet
    import com.github.lucaengel.jass_entials.data.game_state.BettingState
    import com.github.lucaengel.jass_entials.data.game_state.PlayerId

    class SchieberBettingLogic : BettingLogic {

        // inherit the javadoc

        override fun nextPlayer(
            currentBetter: PlayerId,
            currentPlayerBet: Bet?,
            bettingState: BettingState
        ): PlayerId {
            if (currentPlayerBet != null) return currentBetter

            return currentBetter.teamMate()
        }

        override fun availableActions(
            currentBetter: PlayerId,
            bettingState: BettingState
        ): List<Bet.BetAction> {
            if (bettingState.betActions.size >= 2) // both players passed
                return listOf(Bet.BetAction.BET)

            return listOf(Bet.BetAction.BET, Bet.BetAction.PASS)
        }
    }