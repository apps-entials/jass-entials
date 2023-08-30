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
        val trumpEvalsWithPartnerBonus = trumpEvaluationsWithPartnerBonus(bettingState, handCards)

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

    private fun trumpEvaluationsWithPartnerBonus(
        bettingState: BettingState,
        handCards: List<Card>
    ): List<SchieberBiddingCpu.TrumpEvaluation> {
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
                eval.copy(
                    points = eval.points + bonusForPartnerBet(
                        bettingState,
                        handCards,
                        eval.trump,
                        teamPartnerBets
                    )
                )
            } else {
                eval
            }
        }
        return trumpEvalsWithPartnerBonus
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
        return if (lastBetHeight >= BetHeight.fromPoints(evalForBet.points)) {
            BetHeight.NONE
        } else if (bet.trump == Trump.OBE_ABE) {
            val nbAces = handCards.count { it.rank == Rank.ACE } - 1

            bettingState.availableBets()[nbAces.coerceAtLeast(0)]
        } else if (bet.trump == Trump.UNGER_UFE) {
            val nbSixes = handCards.count { it.rank == Rank.SIX } - 1

            bettingState.availableBets()[nbSixes.coerceAtLeast(0)]
        } else {
            // suit trumps
            findSuitTrumpBetHeight(handCards, bet, bettingState)
        }
    }

    private fun findSuitTrumpBetHeight(
        handCards: List<Card>,
        bet: Bet,
        bettingState: BettingState
    ): BetHeight {
        val trumpCards = handCards.filter { it.suit == bet.trump.asSuit() }

        val lastTeamBetForTrump = bettingState.bets
            .filter {
                it.trump == bet.trump
                        && it.playerId.teamId() == playerId.teamId()
            }.lastOrNull()

        return if (lastTeamBetForTrump == null) {
            firstTeamBetForTrump(trumpCards, bettingState)
        } else if (lastTeamBetForTrump.bet.isOdd()) {
            // partner has the nell
            betKnowingPartnerHasNell(trumpCards, bettingState)
        } else if (!lastTeamBetForTrump.bet.isOdd()) {
            // partner has the jack
            betKnowingPartnerHasJack(trumpCards, bettingState)
        } else {
            // nothing fits --> pass
            BetHeight.NONE
        }
    }

    private fun betKnowingPartnerHasJack(
        trumpCards: List<Card>,
        bettingState: BettingState
    ): BetHeight {
        return if (trumpCards.any { it.rank == Rank.NINE }) {
            // need 2 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            val nHigher = ((trumpCards.size - 2) * 2).coerceAtLeast(0)
            bettingState.availableBets().first()
                .firstEven().nHigher(nHigher)
        } else if (trumpCards.any { it.rank == Rank.ACE } && trumpCards.size >= 3) {
            // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
            bettingState.availableBets().first()
                .firstOdd().nHigher(nHigher)
        } else {
            BetHeight.NONE
        }
    }

    private fun betKnowingPartnerHasNell(
        trumpCards: List<Card>,
        bettingState: BettingState
    ): BetHeight {
        return if (trumpCards.any { it.rank == Rank.JACK }) { // TODO: double check that only one jack is needed!!!
            // need 1 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            val nHigher = ((trumpCards.size - 1) * 2).coerceAtLeast(0)
            bettingState.availableBets().first()
                .firstEven().nHigher(nHigher)
        } else if (trumpCards.any { it.rank == Rank.ACE } && trumpCards.size >= 3) {
            // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
            bettingState.availableBets().first()
                .firstOdd().nHigher(nHigher)
        } else {
            BetHeight.NONE
        }
    }

    private fun firstTeamBetForTrump(
        trumpCards: List<Card>,
        bettingState: BettingState
    ): BetHeight {
        return if (trumpCards.any { it.rank == Rank.JACK }) {
            // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
            bettingState.availableBets().first()
                .firstEven().nHigher(nHigher)
        } else if (trumpCards.any { it.rank == Rank.NINE }) {
            // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            val nHigher = ((trumpCards.size - 3) * 2).coerceAtLeast(0)
            bettingState.availableBets().first()
                .firstOdd().nHigher(nHigher)
        } else {
            BetHeight.NONE
        }
    }
}