package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

class SidiBarraniBiddingCpu(
    val playerId: PlayerId
) : BiddingCpu {

    companion object {
        private val NORMAL_BIDDING_THRESHOLD = BetHeight.HUNDRED

        /**
         * Analyzes the last bets and extracts knowledge from them.
         *
         * @param nbBetsInLastPass the number of bets in the last pass (i.e., the number of bets since current player placed their last bet)
         * @param lastBets the last bets
         */
        fun extractKnowledgeFromBets(nbBetsInLastPass: Int, lastBets: List<Bet>) {
            if (lastBets.isEmpty()) return

            val betsWithLastBet =
                if (lastBets.size < nbBetsInLastPass + 1 || lastBets.size < 4) {
                    (listOf(
                        Bet(
                            PlayerId.PLAYER_1,
                            Trump.UNGER_UFE,
                            BetHeight.NONE
                        )
                    ) + lastBets).zipWithNext()
                } else {
                    lastBets.zipWithNext()
                }


            betsWithLastBet.forEach { (lastBet, currBet) ->
                val jump = currBet.bet.ordinal - lastBet.bet.ordinal
                // since none is at 0, 40 at 1, etc.:
                val isEven = currBet.bet.ordinal % 2 == 1

                if (currBet.bet <= NORMAL_BIDDING_THRESHOLD) {
                    // here, we can add the known cards to the evaluation
                    if (currBet.trump in listOf(
                            Trump.HEARTS,
                            Trump.DIAMONDS,
                            Trump.CLUBS,
                            Trump.SPADES
                        )
                    ) {
                        // TODO: adapt algo for if the partner had already bid this suit before --> nel z zwöit, ass z dritt, etc.

                        val currGuaranteedCards =
                            GameStateHolder.guaranteedCards[currBet.playerId] ?: setOf()

                        // add trump jack or nine to guaranteed cards
                        GameStateHolder.guaranteedCards += (currBet.playerId to
                                currGuaranteedCards +
                                if (isEven) Card(currBet.trump.asSuit()!!, Rank.JACK)
                                else Card(currBet.trump.asSuit()!!, Rank.NINE))

                        // number of cards for the given suit
                        val currCardsPerSuit = GameStateHolder.cardsPerSuitPerPlayer[currBet.playerId] ?: setOf()

                        // three as default and an additional card for every 20 added points (2 added in the ordinal)
                        val cardsForCurrTrump = 3 + (jump - 1) / 2

                        GameStateHolder.cardsPerSuitPerPlayer += currBet.playerId to
                                currCardsPerSuit + Pair(currBet.trump.asSuit()!!, cardsForCurrTrump)
                    } else {
                        GameStateHolder.acesPerPlayer += currBet.playerId to jump
                    }



                    println(" stuff1: ${GameStateHolder.prevTrumpsByTeam}")
                    println(" stuff2: ${GameStateHolder.guaranteedCards}")
                    println(" stuff3: ${GameStateHolder.cardsPerSuitPerPlayer}")
                    println(" stuff4: ${GameStateHolder.acesPerPlayer}")


                    // TODO: come up with technique for the aces (maybe just have a number of aces that are guaranteed without the suit?)

                    // TODO: come up with technique for adding value to trumps when obe abe was bid
                }
            }
        }
    }

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
                eval.copy(points = eval.points + bonusForPartnerBet(bettingState, handCards, eval.trump))
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

    private fun bonusForPartnerBet(bettingState: BettingState, handCards: List<Card>, trump: Trump): Int {
        return 35
    }

    private fun findBetHeight(bettingState: BettingState, bet: Bet, evalForBet: SchieberBiddingCpu.TrumpEvaluation, handCards: List<Card>): BetHeight {
        val lastBet = bettingState.bets.lastOrNull()

        // TODO: add logic to bidding for saying aces, sixes, jacks, nels, etc.
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