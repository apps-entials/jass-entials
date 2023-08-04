package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test

class PlayerDataTest {

    private val defaultPlayerData = PlayerData(
        email = "",
        playerIdx = 0,
        firstName = "",
        lastName = "",
        cards = listOf(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.TEN, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.CLUBS),
            Card(Rank.SIX, Suit.CLUBS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ),
        teamNb = 0,
        token = "",
    )

    @Test
    fun playableCardsShowsAllCardsWithEmptyTrick() {
        val emptyTrick = Trick(listOf())

        val playableCards = defaultPlayerData.playableCards(emptyTrick, Trump.HEARTS)

        println(playableCards)
        println(defaultPlayerData.cards)

        assertThat(playableCards, Matchers.containsInAnyOrder(*defaultPlayerData.cards.toTypedArray()))
    }

    @Test
    fun playableCardsShowsTrumpCardsAndPlayableSuitCardsWhenJustASuitHasBeenPlayed() {
        val trick = Trick(listOf(Pair(Card(Rank.SEVEN, Suit.HEARTS), PlayerData())))

        val playableCards = defaultPlayerData.playableCards(trick, Trump.CLUBS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),

            Card(Rank.ACE, Suit.CLUBS),
            Card(Rank.SIX, Suit.CLUBS),
        ))
    }

    @Test
    fun cannotUndertrump() {
        val trick = Trick(listOf(
            Pair(Card(Rank.SEVEN, Suit.DIAMONDS), PlayerData()),
            Pair(Card(Rank.TEN, Suit.HEARTS), PlayerData()),
            )
        )

        val playableCards = defaultPlayerData.playableCards(trick, Trump.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.TEN, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.HEARTS),
        )) // no six since that would be undertrumping
    }

    @Test
    fun ifAceOfTrumpPlayedAndNoUnderTrumpingNellAndJackStillPlayable() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),
            Card(Rank.JACK, Suit.HEARTS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ))

        val trick = Trick(listOf(
            Pair(Card(Rank.SEVEN, Suit.DIAMONDS), PlayerData()),
            Pair(Card(Rank.ACE, Suit.HEARTS), PlayerData()),
        ))

        val playableCards = player.playableCards(trick, Trump.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.NINE, Suit.HEARTS),
            Card(Rank.JACK, Suit.HEARTS),
        ))
    }

    @Test
    fun ifNellOfTrumpPlayedAndNoUnderTrumpingNellAndJackStillPlayable() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.JACK, Suit.HEARTS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ))

        val trick = Trick(listOf(
            Pair(Card(Rank.SEVEN, Suit.DIAMONDS), PlayerData()),
            Pair(Card(Rank.NINE, Suit.HEARTS), PlayerData()),
        ))

        val playableCards = player.playableCards(trick, Trump.HEARTS)

        println(playableCards)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.JACK, Suit.HEARTS),
        ))
    }

    @Test
    fun ifTrumpPlayedFirstAllTrumpsAllowed() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ))

        val trick = Trick(listOf(
            Pair(Card(Rank.TEN, Suit.HEARTS), PlayerData()),
            Pair(Card(Rank.TEN, Suit.DIAMONDS), PlayerData()),
        ))

        val playableCards = player.playableCards(trick, Trump.HEARTS)
        println("playableCards: $playableCards")

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),
        ))
    }

    @Test
    fun playableCardsForUngerUfeAreOnlyCardsWithTheSameSuit() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ))

        val trick = Trick(listOf(
            Pair(Card(Rank.TEN, Suit.HEARTS), PlayerData()),
            Pair(Card(Rank.TEN, Suit.DIAMONDS), PlayerData()),
        ))

        val playableCards = player.playableCards(trick, Trump.UNGER_UFE)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),
        ))
    }

    @Test
    fun ifUserHasNoTrumpCardsAndTrumpWasPlayedTheUserCanPlayAnyCard() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ))

        val trick = Trick(listOf(
            Pair(Card(Rank.TEN, Suit.CLUBS), PlayerData()),
            Pair(Card(Rank.TEN, Suit.DIAMONDS), PlayerData()),
        ))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }

    @Test
    fun noUnderTrumpingAllowed() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ))

        val trick = Trick(listOf(
            Pair(Card(Rank.TEN, Suit.HEARTS), PlayerData()),
            Pair(Card(Rank.TEN, Suit.DIAMONDS), PlayerData()),
        ))

        val playableCards = player.playableCards(trick, Trump.DIAMONDS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            // not the six of diamonds as this would be under trumping
            Card(Rank.ACE, Suit.DIAMONDS),
        ))
    }

    @Test // no trump cards and no first card suit cards in the hand
    fun ifNoCardCanBePlayedAllCardsCanBePlayed() {

        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),
        ))

        val trick = Trick(listOf(
            Pair(Card(Rank.TEN, Suit.DIAMONDS), PlayerData()),
            Pair(Card(Rank.TEN, Suit.CLUBS), PlayerData()),
        ))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }
}
