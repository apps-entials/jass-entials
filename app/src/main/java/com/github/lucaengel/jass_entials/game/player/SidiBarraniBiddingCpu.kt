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
         * @param allBets all bets that have been placed so far
         */
        fun extractKnowledgeFromBets(nbBetsInLastPass: Int, allBets: List<Bet>) {
            if (allBets.isEmpty()) return

            val lastBets = allBets.takeLast(nbBetsInLastPass + 1)

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
                    when (currBet.trump) {
                        Trump.OBE_ABE -> {
                            GameStateHolder.acesPerPlayer += currBet.playerId to jump
                        }
                        Trump.UNGER_UFE -> {
                            GameStateHolder.sixesPerPlayer += currBet.playerId to jump
                        }
                        // any suit trump
                        else -> {
                            // TODO: adapt algo for if the partner had already bid this suit before --> nel z zwöit, ass z dritt, etc.

                            val currGuaranteedCards =
                                GameStateHolder.guaranteedCards[currBet.playerId] ?: setOf()

                            val firstSuchBet = allBets
                                .first {
                                    it.trump == currBet.trump
                                            && it.playerId.teamId() == currBet.playerId.teamId()
                                }

                            val nbCurrTeamTrumpBets = allBets
                                .count { it.trump == currBet.trump
                                        && it.playerId.teamId() == currBet.playerId.teamId()
                                }
                            // since, now, we do not know exactly what is being announced!
                            if (nbCurrTeamTrumpBets > 2) return




                            val guaranteedCard: Card
                            val nbCardsForSuit: Int
                            if (isEven && firstSuchBet == currBet) {
                                guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.JACK)
                                nbCardsForSuit = 3
                            } else if (isEven && firstSuchBet.bet.ordinal % 2 == 1) {
                                guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.ACE)
                                nbCardsForSuit = 3
                            // team partner announced the nell
                            } else if (isEven) {
                                guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.JACK)
                                nbCardsForSuit = 2
                            } else if (firstSuchBet == currBet) {
                                guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.NINE)
                                nbCardsForSuit = 3
                            // team partner announced the jack
                            } else if (firstSuchBet.bet.ordinal % 2 == 1) {
                                guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.NINE)
                                nbCardsForSuit = 2
                            } else {
                                guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.ACE)
                                nbCardsForSuit = 3
                            }

                            // add trump jack, nine, or ace to guaranteed cards
                            GameStateHolder.guaranteedCards += (currBet.playerId to
                                    currGuaranteedCards + guaranteedCard)
                                    // TODO: do you say ace with 2 when the partner announced the nell?


                            // number of cards for the given suit
                            val currCardsPerSuit = GameStateHolder.cardsPerSuitPerPlayer[currBet.playerId] ?: mapOf()

                            // an additional card for every 20 added points (2 added in the ordinal)
                            val cardsForCurrTrump = nbCardsForSuit + (jump - 1) / 2

                            GameStateHolder.cardsPerSuitPerPlayer += currBet.playerId to
                                    currCardsPerSuit + Pair(currBet.trump.asSuit()!!, cardsForCurrTrump)
                        }
                    }


                    println("\n\n\n")
                    println(" trumpsbyteams: ${GameStateHolder.prevTrumpsByTeam}")
                    println(" guaranteed c': ${GameStateHolder.guaranteedCards}")
                    println(" cardsuit p pl: ${GameStateHolder.cardsPerSuitPerPlayer}")
                    println(" aces per play: ${GameStateHolder.acesPerPlayer}")
                    println("\n\n\n")


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