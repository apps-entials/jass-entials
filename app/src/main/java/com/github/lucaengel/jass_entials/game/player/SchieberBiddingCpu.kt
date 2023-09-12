package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump

class SchieberBiddingCpu(
    val playerId: PlayerId,
) : BiddingCpu {
    companion object {
        private const val FIRST_PLAYER_THRESHOLD = 58

        private const val SECOND_PLAYER_THRESHOLD = 52

        private val THRESHOLDS = listOf(FIRST_PLAYER_THRESHOLD, SECOND_PLAYER_THRESHOLD, Int.MIN_VALUE)

        const val TRUMP_JACK_VALUE = 40
        const val TRUMP_NINE_VALUE = 31
        const val TRUMP_ACE_VALUE = 15
        const val TRUMP_LOW_VALUE = 10
        const val TRUMP_ACE_BOCK_VALUE = 10
        const val TRUMP_KING_BOCK_VALUE = 5
        const val TRUMP_QUEEN_BOCK_VALUE = 5

        const val MIN_TRUMP_VALUE = 55

        const val OBE_ACE_VALUE = 20
        const val OBE_KING_VALUE = 12
        const val OBE_QUEEN_VALUE = 10
        const val OBE_LOW_VALUE = 8
        const val OBE_LOW_VALUE_AFTER_KING = 6
        const val OBE_LOW_VALUE_AFTER_QUEEN = 3
        const val OBE_KING_BOCK_VALUE = 8
        const val OBE_QUEEN_BOCK_VALUE = 6
    }

    data class TrumpEvaluation(val trump: Trump, val points: Int)

    override fun bet(bettingState: BettingState, handCards: List<Card>): Bet? {

        if (bettingState.availableActions().contains(Bet.BetAction.BET)) println("contains bet action")
        if (bettingState.availableActions().contains(Bet.BetAction.START_GAME)) println("contains start game action")
        if (!bettingState.availableActions().contains(Bet.BetAction.BET)
            || bettingState.betActions.size > 2) { // TODO: this second condition might not be needed
            return null
        }

        println("bet actions: ${bettingState.betActions}")

        val evaluations = evaluateTrumps(
            handCards = handCards,
            trumps = Trump.values().toList(),
            isVorderhand = bettingState.betActions.isEmpty()
        )

        println("hand cards : $handCards")
        println("evaluations: $evaluations")
        return findBestBet(
            evaluations = evaluations,
            threshold = THRESHOLDS[bettingState.betActions.size.coerceAtMost(THRESHOLDS.size - 1)]
        )

    }

    fun findBestBet(
        evaluations: List<TrumpEvaluation>,
        threshold: Int
    ): Bet? {
        println("threshold: $threshold")
        val trumpEvaluation = evaluations.filter { it.points >= threshold }.shuffled()
            .maxByOrNull { it.points }
            ?: return null

        return Bet(
            playerId = playerId,
            trump = trumpEvaluation.trump,
            bet = BetHeight.MATCH,
        )
    }

    /**
     * Evaluates the trumps for a given hand.
     *
     * @param handCards The hand cards of the player.
     * @param trumps The trumps to evaluate.
     * @param isVorderhand Whether the player is Vorderhand or not.
     * @return A list of [TrumpEvaluation]s for the given trumps.
     */
    fun evaluateTrumps(handCards: List<Card>, trumps: List<Trump>, isVorderhand: Boolean): List<TrumpEvaluation> {
        val trumpValues = mutableListOf<TrumpEvaluation>()
        for (trump in trumps) {
            when (trump) {
                Trump.UNGER_UFE -> {
                    trumpValues.add(evaluateUngerUfe(handCards))
                }
                Trump.OBE_ABE -> {
                    trumpValues.add(evaluateObeAbe(handCards))
                }
                else -> {
                    val trumpSuit = Trump.asSuit(trump)!!

                    val points = trumpPoints(handCards, trumpSuit, isVorderhand)
                    trumpValues.add(TrumpEvaluation(trump, points))
                }
            }
        }

        return trumpValues
    }


    // todo subtract certain number of points if only two trumps (e.g., jack and nell)

    // todo: long suit such as: [♥J, ♥8, ♠8, ♦A, ♦K, ♦Q, ♦10, ♦9, ♦7] --> bid obenabe  (instead of diamonds in the current setting)
    //  since missing jack!

    // todo decrease value of nell / ace / both if jack of trump is missing
    private fun trumpPoints(handCards: List<Card>, trumpSuit: Suit, isVorderhand: Boolean): Int {
        val trumpCards = Card.cardsOfSuit(trumpSuit, handCards)
        val trumpRanks = trumpCards.map { it.rank }.toSet()
        val otherCards = handCards - trumpCards.toSet()

        var nbLowTrumps = trumpCards.size

        // Vorderhand bidding:
        // jack with 3

        var points = 0
        if (trumpRanks.contains(Rank.JACK)) {
            points += TRUMP_JACK_VALUE
            nbLowTrumps--
        }

        if (trumpRanks.contains(Rank.NINE)) {
            points += TRUMP_NINE_VALUE
            nbLowTrumps--
        }

        if (trumpRanks.contains(Rank.ACE)) {
            points += TRUMP_ACE_VALUE
            nbLowTrumps--
        }

        // other trump cards
        points += nbLowTrumps * TRUMP_LOW_VALUE
        println("points: $points min trump value: $MIN_TRUMP_VALUE, isVorderhand: $isVorderhand")

        if (points <= MIN_TRUMP_VALUE && isVorderhand) {
            return points
        }


        // non-trump points

        // other suit aces
        points += otherCards.filter { it.rank == Rank.ACE }.size * TRUMP_ACE_BOCK_VALUE


        // bock-cards
        points += otherCards.filter {
            (it.rank == Rank.KING
                    && Card.cardsOfSuit(it.suit, otherCards).size > 1
                    )
        }.size * TRUMP_KING_BOCK_VALUE

        points += otherCards.filter { (it.rank == Rank.QUEEN
            && Card.cardsOfSuit(it.suit, otherCards).size > 2)
        }.size * TRUMP_QUEEN_BOCK_VALUE

        println("actual points: $points")
        return points
    }

    private fun evaluateUngerUfe(handCards: List<Card>): TrumpEvaluation {
        // same evaluation as with obe-abe. For now, points for 8 and 10 not taken into account for evaluation
        val ungerUfeAsObeAbe = handCards.map { it.copy(
            rank = Rank.values()[Rank.values().size - 1 - it.rank.ordinal])
        }

        println("hand cards: $handCards")
        return evaluateObeAbe(
            ungerUfeAsObeAbe
        ).copy(trump = Trump.UNGER_UFE)
    }

    private fun evaluateObeAbe(handCards: List<Card>): TrumpEvaluation {
        var points = 0
        handCards.groupBy { it.suit }.forEach { (_, cards) ->
            val ranks = cards.map { it.rank }.toSet()

            if (ranks.contains(Rank.ACE)) {
                points += OBE_ACE_VALUE

                if (ranks.contains(Rank.KING)) {
                    points += OBE_KING_VALUE

                    if (ranks.contains(Rank.QUEEN)) {
                        points += OBE_QUEEN_VALUE

                        points += (ranks.size - 3) * OBE_LOW_VALUE
                    } else {
                        points += (ranks.size - 3) * OBE_LOW_VALUE_AFTER_KING
                    }
                // Ace queen and at least a third card of the same suit
                } else if (ranks.contains(Rank.QUEEN) && ranks.size > 2) {
                    points += (ranks.size - 1) * OBE_LOW_VALUE_AFTER_QUEEN
                }
            } else if (ranks.contains(Rank.KING) && ranks.size >= 2) {
                points += OBE_KING_BOCK_VALUE
            } else if (ranks.contains(Rank.QUEEN) && ranks.size >= 3) {
                points += OBE_QUEEN_BOCK_VALUE
            }
        }

        return TrumpEvaluation(Trump.OBE_ABE, points)
    }
}