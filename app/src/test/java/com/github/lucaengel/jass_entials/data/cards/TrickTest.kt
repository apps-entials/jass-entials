package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class TrickTest {

    @Test
    fun isFullReturnsTrueOnlyForTrickWith4CardsInside() {
        var trick = Trick()
        for (i in 0..3) {
            trick = trick.copy(playerToCard = trick.playerToCard + Pair(Card(Rank.SIX, Suit.CLUBS), PlayerData()))

            assertThat(trick.isFull(), `is`(i == 3))
        }
    }

    @Test
    fun winnerReturnsHighestCardPlayerOfStartingSuit() {
        val trick = Trick(listOf(
            Pair(Card(Rank.SEVEN, Suit.HEARTS), PlayerData().copy(firstName = "1")),
            Pair(Card(Rank.ACE, Suit.DIAMONDS), PlayerData().copy(firstName = "2")),
            Pair(Card(Rank.EIGHT, Suit.HEARTS), PlayerData().copy(firstName = "3")),
            Pair(Card(Rank.SEVEN, Suit.CLUBS), PlayerData().copy(firstName = "4")),
        ))
        assertThat(trick.winner(Trump.SPADES).firstName, `is`("3"))
    }

    @Test
    fun winnerReturnsHighestTrumpPlayer() {
        val trick = Trick(listOf(
            Pair(Card(Rank.SEVEN, Suit.CLUBS), PlayerData().copy(firstName = "1")),
            Pair(Card(Rank.ACE, Suit.DIAMONDS), PlayerData().copy(firstName = "2")),
            Pair(Card(Rank.EIGHT, Suit.HEARTS), PlayerData().copy(firstName = "3")),
            Pair(Card(Rank.SEVEN, Suit.CLUBS), PlayerData().copy(firstName = "4")),
        ))
        assertThat(trick.winner(Trump.HEARTS).firstName, `is`("3"))
    }

    @Test
    fun winnerReturnsCorrectPlayerForUngerUfe() {
        val trick = Trick(listOf(
            Pair(Card(Rank.ACE, Suit.CLUBS), PlayerData().copy(firstName = "1")),
            Pair(Card(Rank.ACE, Suit.DIAMONDS), PlayerData().copy(firstName = "2")),
            Pair(Card(Rank.EIGHT, Suit.HEARTS), PlayerData().copy(firstName = "3")),
            Pair(Card(Rank.SIX, Suit.CLUBS), PlayerData().copy(firstName = "4")),
        ))
        assertThat(trick.winner(Trump.UNGER_UFE).firstName, `is`("4"))
    }
}