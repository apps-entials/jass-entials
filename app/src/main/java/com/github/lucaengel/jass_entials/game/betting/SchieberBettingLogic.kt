    package com.github.lucaengel.jass_entials.game.betting

    import com.github.lucaengel.jass_entials.data.game_state.Bet
    import com.github.lucaengel.jass_entials.data.game_state.BettingState

    class SchieberBettingLogic : BettingLogic {

        override fun nextPlayer(
            currentBetterEmail: String,
            currentPlayerBet: Bet?,
            bettingState: BettingState
        ): String {
            if (currentPlayerBet != null) return currentBetterEmail

            val idx = bettingState.playerEmails.indexOf(currentBetterEmail)
            return bettingState.playerEmails[(idx + 2) % bettingState.playerEmails.size]
        }

        override fun availableActions(
            currentPlayerEmail: String,
            bettingState: BettingState
        ): List<Bet.BetAction> {
            if (bettingState.betActions.size >= 2) // both players passed
                return listOf(Bet.BetAction.BET)

            return listOf(Bet.BetAction.BET, Bet.BetAction.PASS)
        }
    }