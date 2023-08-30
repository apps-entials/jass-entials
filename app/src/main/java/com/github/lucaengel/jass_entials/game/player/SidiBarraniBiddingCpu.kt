package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

class SidiBarraniBiddingCpu(
    val playerId: PlayerId
) : BiddingCpu {

    private val schieberBiddingCpu = SchieberBiddingCpu(playerId)

    override fun bet(bettingState: BettingState, handCards: List<Card>): Bet? {
        // elevate my potential bets
        val teamPartnerBets = bettingState.bets.filter { it.playerId == playerId.teamMate() }
        val teamPartnersTrumps = teamPartnerBets.map { it.trump }.toSet()

        // helps to know how much of a jump I can make with my bet
        // e.g., if I already bid hearts once, then I want to announce ace's, ...
        val myBets = bettingState.bets.filter { it.playerId == playerId }

        val trumpEvaluations = schieberBiddingCpu.evaluateTrumps(
            handCards = handCards,
            trumps = bettingState.availableTrumps().toList(),
            isVorderhand = true
        )

        val trumpEvalsWithPartnerBonus = trumpEvaluations.map { eval ->
            if (teamPartnersTrumps.contains(eval.trump)) {
                eval.copy(points = eval.points + bonusForPartnerBet(bettingState, handCards, eval.trump, teamPartnerBets))
            } else {
                eval
            }
        }

        val bet = schieberBiddingCpu.findBestBet(trumpEvalsWithPartnerBonus, 0)
            ?: return null

        val evalForBet = trumpEvalsWithPartnerBonus.first { it.trump == bet.trump }

        val betHeight = findBetHeight(bettingState, bet, evalForBet, handCards)

        return if (betHeight == BetHeight.NONE) {
            null
        } else {
            Bet(
                playerId = playerId,
                bet = betHeight,
                trump = bet.trump
            )
        }
    }

    private fun bonusForPartnerBet(
        bettingState: BettingState,
        handCards: List<Card>,
        trump: Trump,
        teamPartnerBets: List<Bet>
    ): Int {
        return teamPartnerBets.first { it.trump == trump }.bet.value / 2
    }

    private fun findBetHeight(
        bettingState: BettingState,
        bet: Bet,
        evalForBet: SchieberBiddingCpu.TrumpEvaluation,
        handCards: List<Card>
    ): BetHeight {

        val lastBet = bettingState.bets.lastOrNull()
        val lastBetHeight = lastBet?.bet ?: BetHeight.NONE

        // if cannot bid higher than previous bet, pass
        if (lastBetHeight >= BetHeight.fromPoints(evalForBet.points)) {
            return BetHeight.NONE
        }

        if (bet.trump == Trump.OBE_ABE) {
            val nbAces = handCards.count { it.rank == Rank.ACE } - 1

            return bettingState.availableBets()[nbAces.coerceAtLeast(0)]
        } else if (bet.trump == Trump.UNGER_UFE) {
            val nbSixes = handCards.count { it.rank == Rank.SIX } - 1

            return bettingState.availableBets()[nbSixes.coerceAtLeast(0)]
        }

        // suit trumps
        val trumpCards = handCards.filter { it.suit == bet.trump.asSuit() }

        val lastTeamBetForTrump = bettingState.bets
            .filter { it.trump == bet.trump
                    && it.playerId.teamId() == playerId.teamId()
            }.lastOrNull()

        if (lastTeamBetForTrump == null) {
            if (trumpCards.any { it.rank == Rank.JACK }) {
                // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
                val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
                return bettingState.availableBets().first()
                    .firstEven().nHigher(nHigher)
            } else if (trumpCards.any {it.rank == Rank.NINE}) {
                // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
                val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
                return bettingState.availableBets().first()
                    .firstOdd().nHigher(nHigher)
            }
        } else if (lastTeamBetForTrump.bet.isOdd()) {
            // partner has the nell
            if (trumpCards.any { it.rank == Rank.JACK }) { // TODO: double check that only one jack is needed!!!
                // need 1 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
                val nHigher = ((trumpCards.size - 1) * 2).coerceAtLeast(0)
                return bettingState.availableBets().first()
                    .firstEven().nHigher(nHigher)
            } else if (trumpCards.any { it.rank == Rank.ACE } && trumpCards.size >= 3) {
                // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
                val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
                return bettingState.availableBets().first()
                    .firstOdd().nHigher(nHigher)
            }
        } else if (!lastTeamBetForTrump.bet.isOdd()) {
            // partner has the jack
            if (trumpCards.any { it.rank == Rank.NINE }) {
                // need 2 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
                val nHigher = ((trumpCards.size - 2) * 2).coerceAtLeast(0)
                return bettingState.availableBets().first()
                    .firstEven().nHigher(nHigher)
            } else if (trumpCards.any { it.rank == Rank.ACE } && trumpCards.size >= 3) {
                // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
                val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
                return bettingState.availableBets().first()
                    .firstOdd().nHigher(nHigher)
            }
        }

        // nothing fits --> pass
        return BetHeight.NONE
    }
}