package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
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

        val bets = schieberBiddingCpu.evaluateTrumps(
            handCards = handCards,
            trumps = bettingState.availableTrumps().toList(),
            isVorderhand = true
        )

        val betsWithPartnerBonus = bets.map { eval ->
            if (teamPartnersTrumps.contains(eval.trump)) {
                eval.copy(points = eval.points + bonusForPartnerBet(bettingState, handCards, eval.trump))
            } else {
                eval
            }
        }

        val bet = schieberBiddingCpu.findBestBet(betsWithPartnerBonus, 0)
            ?: return null

        val evalForBet = betsWithPartnerBonus.first { it.trump == bet.trump }

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

    private fun bonusForPartnerBet(bettingState: BettingState, handCards: List<Card>, trump: Trump): Int {
        return 35
    }

    private fun findBetHeight(bettingState: BettingState, bet: Bet, evalForBet: SchieberBiddingCpu.TrumpEvaluation, handCards: List<Card>): BetHeight {
        val lastBet = bettingState.bets.lastOrNull()

        return if (lastBet == null) {
            bettingState.availableBets().firstOrNull() ?: BetHeight.NONE
        } else {
            if (bettingState.availableBets().contains(BetHeight.fromPoints(evalForBet.points))) {
                bettingState.availableBets().first()
            } else {
                BetHeight.NONE
            }
        }
    }
}