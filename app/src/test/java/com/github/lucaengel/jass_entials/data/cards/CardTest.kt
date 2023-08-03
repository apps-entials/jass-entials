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
            Card(Rank.SEVEN, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.SIX, Suit.CLUBS),
                    Trump.SPADES)
        )

    }

    @Test
    fun isHigherThanWorksForSameColorNelAndTrumpJack() {
        assertTrue(
            Card(Rank.NINE, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.ACE, Suit.CLUBS),
                    Trump.CLUBS)
        )

        assertTrue(
            Card(Rank.JACK, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.NINE, Suit.CLUBS),
                    Trump.CLUBS)
        )
    }

    @Test
    fun stechenWorks() {
        assertTrue(
            Card(Rank.SIX, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.ACE, Suit.HEARTS),
                    Trump.CLUBS)
        )

        assertFalse(
            Card(Rank.JACK, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.EIGHT, Suit.HEARTS),
                    Trump.HEARTS)
        )
    }

    @Test
    fun notKeepingSuitWithoutPlayingTrumpLooses() {
        assertTrue(
            Card(Rank.SIX, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.ACE, Suit.HEARTS),
                    Trump.DIAMONDS)
        )
    }

    @Test
    fun ungerUfeMakesTheLowerCardWin() {
        assertTrue(
            Card(Rank.SIX, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.ACE, Suit.CLUBS),
                    Trump.UNGER_UFE)
        )

        assertFalse(
            Card(Rank.ACE, Suit.CLUBS)
                .isHigherThan(
                    Card(Rank.SIX, Suit.CLUBS),
                    Trump.UNGER_UFE)
        )
    }

    @Test
    fun toStringContainsTheCorrectRank() {
        assertThat(Card(Rank.SIX, Suit.CLUBS).toString(), `is`("6\u2663"))
        assertThat(Card(Rank.SIX, Suit.SPADES).toString(), `is`("6\u2660"))
        assertThat(Card(Rank.SIX, Suit.HEARTS).toString(), `is`("6\u2665"))
        assertThat(Card(Rank.SIX, Suit.DIAMONDS).toString(), `is`("6\u2666"))
    }



    @Test
    fun pointsCalculatesUngerUfeCardsCorrectly() {
        val cardToPoints = mapOf(
            Card(Rank.SIX, Suit.CLUBS) to 11,
            Card(Rank.SEVEN, Suit.CLUBS) to 0,
            Card(Rank.EIGHT, Suit.CLUBS) to 8,
            Card(Rank.NINE, Suit.CLUBS) to 0,
            Card(Rank.TEN, Suit.CLUBS) to 10,
            Card(Rank.JACK, Suit.CLUBS) to 2,
            Card(Rank.QUEEN, Suit.CLUBS) to 3,
            Card(Rank.KING, Suit.CLUBS) to 4,
            Card(Rank.ACE, Suit.CLUBS) to 0,
        )

        cardToPoints.keys.forEach {
            assertThat(it.points(Trump.UNGER_UFE), `is`(cardToPoints[it]))
        }
    }

    @Test
    fun pointsCalculatesObeAbeCardsCorrectly() {
        val cardToPoints = mapOf(
            Card(Rank.SIX, Suit.CLUBS) to 0,
            Card(Rank.SEVEN, Suit.CLUBS) to 0,
            Card(Rank.EIGHT, Suit.CLUBS) to 8,
            Card(Rank.NINE, Suit.CLUBS) to 0,
            Card(Rank.TEN, Suit.CLUBS) to 10,
            Card(Rank.JACK, Suit.CLUBS) to 2,
            Card(Rank.QUEEN, Suit.CLUBS) to 3,
            Card(Rank.KING, Suit.CLUBS) to 4,
            Card(Rank.ACE, Suit.CLUBS) to 11,
        )

        cardToPoints.keys.forEach {
            assertThat(it.points(Trump.OBE_ABE), `is`(cardToPoints[it]))
        }
    }

    @Test
    fun pointsCalculatesTrumpCardsCorrectly() {
        val cardToPoints = mapOf(
            Card(Rank.SIX, Suit.CLUBS) to 0,
            Card(Rank.SEVEN, Suit.CLUBS) to 0,
            Card(Rank.EIGHT, Suit.CLUBS) to 0,
            Card(Rank.NINE, Suit.CLUBS) to 14,
            Card(Rank.TEN, Suit.CLUBS) to 10,
            Card(Rank.JACK, Suit.CLUBS) to 20,
            Card(Rank.QUEEN, Suit.CLUBS) to 3,
            Card(Rank.KING, Suit.CLUBS) to 4,
            Card(Rank.ACE, Suit.CLUBS) to 11,
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
            Card(Rank.SIX, Suit.CLUBS) to 0,
            Card(Rank.SEVEN, Suit.CLUBS) to 0,
            Card(Rank.EIGHT, Suit.CLUBS) to 0,
            Card(Rank.NINE, Suit.CLUBS) to 0,
            Card(Rank.TEN, Suit.CLUBS) to 10,
            Card(Rank.JACK, Suit.CLUBS) to 2,
            Card(Rank.QUEEN, Suit.CLUBS) to 3,
            Card(Rank.KING, Suit.CLUBS) to 4,
            Card(Rank.ACE, Suit.CLUBS) to 11,
        )

        cardToPoints.keys.forEach {
            assertThat(it.points(Trump.HEARTS), `is`(cardToPoints[it]))
        }
    }
}