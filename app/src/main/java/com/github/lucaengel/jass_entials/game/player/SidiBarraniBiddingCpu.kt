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

        println("trump evaluations with partner bonus: $trumpEvalsWithPartnerBonus")

        val bet = schieberBiddingCpu.findBestBet(trumpEvalsWithPartnerBonus, Int.MIN_VALUE)!!
        val evalForBet = trumpEvalsWithPartnerBonus.first { it.trump == bet.trump }

        val betHeight = findBetHeight(bettingState, bet.trump, evalForBet, handCards)

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

    /**
     * Returns the trump evaluations with a bonus for the partner's bet.
     *
     * @param bettingState the current betting state
     * @param handCards the hand cards
     * @return the trump evaluations with a bonus for the partner's bet
     */
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

    /**
     * Returns the bonus points for a given trump if the partner already bet on it.
     *
     * @param bettingState the current betting state
     * @param handCards the hand cards
     * @param trump the trump to find the bonus for
     * @param teamPartnerBets the bets of the partner
     * @return the bonus points
     */
    private fun bonusForPartnerBet(
        bettingState: BettingState,
        handCards: List<Card>,
        trump: Trump,
        teamPartnerBets: List<Bet>
    ): Int {
        val guaranteedCardsTeamMate = bettingState.cardDistributionsHandler.guaranteedCards()
            .getOrDefault(playerId.teamMate(), setOf())
        val bonus = if (trump.asSuit() == null) {
            0
        } else if (guaranteedCardsTeamMate.contains(Card(trump.asSuit()!!, Rank.JACK))) {
            17
        } else if (guaranteedCardsTeamMate.contains(Card(trump.asSuit()!!, Rank.NINE))) {
            12
        } else {
            0
        }

        return bonus + 2 * teamPartnerBets.first { it.trump == trump }.bet.value / 3
    }

    /**
     * Returns the bet height for a given bet. This is the height the player should bid.
     *
     * @param bettingState the current betting state
     * @param trump the trump to find the bet height for
     * @param evalForBet the evaluation for the given trump
     * @param handCards the hand cards
     * @return the bet height
     */
    private fun findBetHeight(
        bettingState: BettingState,
        trump: Trump,
        evalForBet: SchieberBiddingCpu.TrumpEvaluation,
        handCards: List<Card>
    ): BetHeight {

        val lastBet = bettingState.bets.lastOrNull()
        val lastBetHeight = lastBet?.bet ?: BetHeight.NONE

        // if cannot bid higher than previous bet, pass
        return if (lastBetHeight >= BetHeight.fromPoints(evalForBet.points)) {
            BetHeight.NONE
        } else if (trump == Trump.OBE_ABE) {
            getBetJumpForNumberOfCards(handCards, bettingState, Rank.ACE)
        } else if (trump == Trump.UNGER_UFE) {
            getBetJumpForNumberOfCards(handCards, bettingState, Rank.SIX)
        } else {
            // suit trumps
            findSuitTrumpBetHeight(handCards, trump, bettingState)
        }
    }

    /**
     * Returns the bet height based on the number of cards of a given rank in the hand. Mainly for 6's and Ace's.
     *
     * @param handCards the hand cards
     * @param bettingState the current betting state
     * @param rankOfCards the rank of the cards to count
     * @return the bet height
     */
    private fun getBetJumpForNumberOfCards(
        handCards: List<Card>,
        bettingState: BettingState,
        rankOfCards: Rank
    ): BetHeight {
        val nbAces = handCards.count { it.rank == rankOfCards } - 1

        return bettingState.availableBets()[nbAces.coerceAtLeast(0)]
    }

    /**
     * Returns the bet height fitting to the current trump and the hand cards.
     *
     * @param handCards the hand cards
     * @param trump the trump to find the bet height for
     * @param bettingState the current betting state
     * @return the bet height
     */
    private fun findSuitTrumpBetHeight(
        handCards: List<Card>,
        trump: Trump,
        bettingState: BettingState
    ): BetHeight {
        val trumpCards = handCards.filter { it.suit == trump.asSuit() }

        val lastTeamBetForTrump = bettingState.bets
            .filter {
                it.trump == trump
                        && it.playerId.teamId() == playerId.teamId()
            }.lastOrNull()

        return if (lastTeamBetForTrump == null) {
            firstTeamBetForTrump(trumpCards, bettingState)
        } else if (lastTeamBetForTrump.bet.isOdd()) {
            // partner has the nell --> I need the jack (or the ace)
            betKnowingGivenCard(trumpCards, bettingState, Rank.JACK)
        } else if (!lastTeamBetForTrump.bet.isOdd()) {
            // partner has the jack --> I need the nell (or the ace)
            betKnowingGivenCard(trumpCards, bettingState, Rank.NINE)
        } else {
            // nothing fits --> pass
            BetHeight.NONE
        }
    }

    /**
     * Returns the betheight based on what highest trump card the partner has. This assumes the partner has the nell or the jack.
     *
     * @param trumpCards the trump cards in the hand
     * @param bettingState the current betting state
     * @param currentPlayersHighestTrump the rank of the trump card the current player has (if not, then they have the ace, if not, pass)
     * @return the bet height
     */
    private fun betKnowingGivenCard(
        trumpCards: List<Card>,
        bettingState: BettingState,
        currentPlayersHighestTrump: Rank // either jack or nell, team mate has the other card
    ): BetHeight {
        val cardsNeededForBet = when (currentPlayersHighestTrump) {
            Rank.JACK -> 1
            Rank.NINE -> 2
            else -> return BetHeight.NONE
        }

        return if (trumpCards.any { it.rank == currentPlayersHighestTrump }) {
            // need 2 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            betForGivenMinNumberOfTrumps(trumpCards, bettingState, cardsNeededForBet, currentPlayersHighestTrump == Rank.JACK)
        } else if (trumpCards.any { it.rank == Rank.ACE } && trumpCards.size >= 3) {
            // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            // if current is looking for the nell, then the team partner has the jack --> bet even if only have the ace, else odd
            betForGivenMinNumberOfTrumps(trumpCards, bettingState, 3, currentPlayersHighestTrump == Rank.NINE)
        } else {
            BetHeight.NONE
        }
    }

    /**
     * Returns the first bet height for a given trump. This is called when this is the first bet for the player's team.
     *
     * @param trumpCards the trump cards in the hand
     * @param bettingState the current betting state
     * @return the bet height
     */
    private fun firstTeamBetForTrump(
        trumpCards: List<Card>,
        bettingState: BettingState
    ): BetHeight {
        return if (trumpCards.any { it.rank == Rank.JACK }) {
            // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            betForGivenMinNumberOfTrumps(trumpCards, bettingState, 3, true)
        } else if (trumpCards.any { it.rank == Rank.NINE }) {
            // need 3 per suit, every additional one, jump by 20 point (i.e., 2 in the ordinal)
            betForGivenMinNumberOfTrumps(trumpCards, bettingState, 3, false)
        } else {
            BetHeight.NONE
        }
    }

    /**
     * Returns the bet height for a given minimum number of trumps.
     *
     * @param trumpCards the trump cards in the hand
     * @param bettingState the current betting state
     * @param minNumberOfTrumps the minimum number of trumps needed
     * @param shouldBetEven whether the bet should be even or odd
     * @return the bet height
     */
    private fun betForGivenMinNumberOfTrumps(
        trumpCards: List<Card>,
        bettingState: BettingState,
        minNumberOfTrumps: Int,
        shouldBetEven: Boolean
    ): BetHeight {
        val nHigher = ((trumpCards.size - minNumberOfTrumps) * 2).coerceAtLeast(0)
        return bettingState.availableBets().first()
            .let { if (shouldBetEven) it.firstEven() else it.firstOdd() }.nHigher(nHigher)
    }
}