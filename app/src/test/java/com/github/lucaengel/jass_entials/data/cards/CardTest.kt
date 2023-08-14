package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.jass.Trump
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CardTest {

    @Test
    fun isHigherThanWorksForSameColor() {
        assertTrue(
            Card(Suit.CLUBS, Rank.SEVEN)
                .isHigherThan(
                    Card(Suit.CLUBS, Rank.SIX),
                    Trump.SPADES)
        )

    }

    @Test
    fun isHigherThanWorksForSameColorNelAndTrumpJack() {
        assertTrue(
            Card(Suit.CLUBS, Rank.NINE)
                .isHigherThan(
                    Card(Suit.CLUBS, Rank.ACE),
                    Trump.CLUBS)
        )

        assertTrue(
            Card(Suit.CLUBS, Rank.JACK)
                .isHigherThan(
                    Card(Suit.CLUBS, Rank.NINE),
                    Trump.CLUBS)
        )
    }

    @Test
    fun stechenWorks() {
        assertTrue(
            Card(Suit.CLUBS, Rank.SIX)
                .isHigherThan(
                    Card(Suit.HEARTS, Rank.ACE),
                    Trump.CLUBS)
        )

        assertFalse(
            Card(Suit.CLUBS, Rank.JACK)
                .isHigherThan(
                    Card(Suit.HEARTS, Rank.EIGHT),
                    Trump.HEARTS)
        )
    }

    @Test
    fun notKeepingSuitWithoutPlayingTrumpLooses() {
        assertTrue(
            Card(Suit.CLUBS, Rank.SIX)
                .isHigherThan(
                    Card(Suit.HEARTS, Rank.ACE),
                    Trump.DIAMONDS)
        )
    }

    @Test
    fun ungerUfeMakesTheLowerCardWin() {
        assertTrue(
            Card(Suit.CLUBS, Rank.SIX)
                .isHigherThan(
                    Card(Suit.CLUBS, Rank.ACE),
                    Trump.UNGER_UFE)
        )

        assertFalse(
            Card(Suit.CLUBS, Rank.ACE)
                .isHigherThan(
                    Card(Suit.CLUBS, Rank.SIX),
                    Trump.UNGER_UFE)
        )
    }

    @Test
    fun toStringContainsTheCorrectRank() {
        assertThat(Card(Suit.CLUBS, Rank.SIX).toString(), `is`("6\u2663"))
        assertThat(Card(Suit.SPADES, Rank.SIX).toString(), `is`("6\u2660"))
        assertThat(Card(Suit.HEARTS, Rank.SIX).toString(), `is`("6\u2665"))
        assertThat(Card(Suit.DIAMONDS, Rank.SIX).toString(), `is`("6\u2666"))
    }



    @Test
    fun pointsCalculatesUngerUfeCardsCorrectly() {
        val cardToPoints = mapOf(
            Card(Suit.CLUBS, Rank.SIX) to 11,
            Card(Suit.CLUBS, Rank.SEVEN) to 0,
            Card(Suit.CLUBS, Rank.EIGHT) to 8,
            Card(Suit.CLUBS, Rank.NINE) to 0,
            Card(Suit.CLUBS, Rank.TEN) to 10,
            Card(Suit.CLUBS, Rank.JACK) to 2,
            Card(Suit.CLUBS, Rank.QUEEN) to 3,
            Card(Suit.CLUBS, Rank.KING) to 4,
            Card(Suit.CLUBS, Rank.ACE) to 0,
        )

        cardToPoints.keys.forEach {
            assertThat(it.points(Trump.UNGER_UFE), `is`(cardToPoints[it]))
        }
    }

    @Test
    fun pointsCalculatesObeAbeCardsCorrectly() {
        val cardToPoints = mapOf(
            Card(Suit.CLUBS, Rank.SIX) to 0,
            Card(Suit.CLUBS, Rank.SEVEN) to 0,
            Card(Suit.CLUBS, Rank.EIGHT) to 8,
            Card(Suit.CLUBS, Rank.NINE) to 0,
            Card(Suit.CLUBS, Rank.TEN) to 10,
            Card(Suit.CLUBS, Rank.JACK) to 2,
            Card(Suit.CLUBS, Rank.QUEEN) to 3,
            Card(Suit.CLUBS, Rank.KING) to 4,
            Card(Suit.CLUBS, Rank.ACE) to 11,
        )

        cardToPoints.keys.forEach {
            assertThat(it.points(Trump.OBE_ABE), `is`(cardToPoints[it]))
        }
    }

    @Test
    fun pointsCalculatesTrumpCardsCorrectly() {
        val cardToPoints = mapOf(
            Card(Suit.CLUBS, Rank.SIX) to 0,
            Card(Suit.CLUBS, Rank.SEVEN) to 0,
            Card(Suit.CLUBS, Rank.EIGHT) to 0,
            Card(Suit.CLUBS, Rank.NINE) to 14,
            Card(Suit.CLUBS, Rank.TEN) to 10,
            Card(Suit.CLUBS, Rank.JACK) to 20,
            Card(Suit.CLUBS, Rank.QUEEN) to 3,
            Card(Suit.CLUBS, Rank.KING) to 4,
            Card(Suit.CLUBS, Rank.ACE) to 11,
        )

        cardToPoints.keys.forEach {
            assertThat(it.points(Trump.CLUBS), `is`(cardToPoints[it]))
        }
    }

    @Test
    fun checkThis() {
        //check this
        assertThat(Math.floorMod(2-3, 4), `is`(3))
    }

    @Test
    fun pointsCalculatesNonTrumpCardsCorrectly() {
        val cardToPoints = mapOf(
            Card(Suit.CLUBS, Rank.SIX) to 0,
            Card(Suit.CLUBS, Rank.SEVEN) to 0,
            Card(Suit.CLUBS, Rank.EIGHT) to 0,
            Card(Suit.CLUBS, Rank.NINE) to 0,
            Card(Suit.CLUBS, Rank.TEN) to 10,
            Card(Suit.CLUBS, Rank.JACK) to 2,
            Card(Suit.CLUBS, Rank.QUEEN) to 3,
            Card(Suit.CLUBS, Rank.KING) to 4,
            Card(Suit.CLUBS, Rank.ACE) to 11,
        )

        cardToPoints.keys.forEach {
            assertThat(it.points(Trump.HEARTS), `is`(cardToPoints[it]))
        }
    }
}