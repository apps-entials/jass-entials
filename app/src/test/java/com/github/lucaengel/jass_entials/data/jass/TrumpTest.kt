package com.github.lucaengel.jass_entials.data.jass

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Suit
import junit.framework.TestCase.assertFalse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test

class TrumpTest {

    @Test
    fun calculatePointsWorksForAllCardsAndSuits() {
        Trump.values().forEach {
            val points = Trump.calculatePoints(Deck.STANDARD_DECK.cards, it)

            // 152 since last trick's +5 points are not accounted for here
            assertThat(points, `is`(152))
        }
    }

    @Test
    fun isSuitTrumpSuitWorksForAllSuitsAndTrumps() {
        val suits = listOf(
            Suit.DIAMONDS,
            Suit.HEARTS,
            Suit.SPADES,
            Suit.CLUBS,
        )
        val trumpSuits = listOf(
            Trump.DIAMONDS,
            Trump.HEARTS,
            Trump.SPADES,
            Trump.CLUBS,
        )
        val nonSuitTrumps = listOf(Trump.OBE_ABE, Trump.UNGER_UFE)

        // check to see if all elements are tested
        assertThat(Suit.values().toList(), Matchers.containsInAnyOrder(*suits.toTypedArray()))
        assertThat(Trump.values().toList(), Matchers.containsInAnyOrder(*(trumpSuits+nonSuitTrumps).toTypedArray()))

        for (i in suits.indices) {
            for (j in trumpSuits.indices) {
                assertThat(Trump.isSuitTrumpSuit(suits[i], trumpSuits[j]), `is`(i == j))
            }
        }

        for (trump in nonSuitTrumps) {
            for (suit in Suit.values()) {
                assertFalse(Trump.isSuitTrumpSuit(suit, trump))
            }
        }
    }
}