package com.github.lucaengel.jass_entials.data.cards

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test

class PlayerTest {

    private val defaultPlayer = Player(
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

        val playableCards = defaultPlayer.playableCards(emptyTrick, Suit.HEARTS)

        println(playableCards)
        println(defaultPlayer.cards)

        assertThat(playableCards, Matchers.containsInAnyOrder(*defaultPlayer.cards.toTypedArray()))
    }

    @Test
    fun playableCardsShowsTrumpCardsAndPlayableSuitCardsWhenJustASuitHasBeenPlayed() {
        val trick = Trick(listOf(Card(Rank.SEVEN, Suit.HEARTS)))

        val playableCards = defaultPlayer.playableCards(trick, Suit.CLUBS)

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
            Card(Rank.SEVEN, Suit.DIAMONDS),
            Card(Rank.TEN, Suit.HEARTS),
            )
        )

        val playableCards = defaultPlayer.playableCards(trick, Suit.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.TEN, Suit.DIAMONDS),

            Card(Rank.ACE, Suit.HEARTS),
        )) // no six since that would be undertrumping
    }

    @Test
    fun ifAceOfTrumpPlayedAndNoUnderTrumpingNellAndJackStillPlayable() {
        val player = defaultPlayer.copy(cards = listOf(
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
            Card(Rank.SEVEN, Suit.DIAMONDS),
            Card(Rank.ACE, Suit.HEARTS),
        ))

        val playableCards = player.playableCards(trick, Suit.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.NINE, Suit.HEARTS),
            Card(Rank.JACK, Suit.HEARTS),
        ))
    }

    @Test
    fun ifNellOfTrumpPlayedAndNoUnderTrumpingNellAndJackStillPlayable() {
        val player = defaultPlayer.copy(cards = listOf(
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
            Card(Rank.SEVEN, Suit.DIAMONDS),
            Card(Rank.NINE, Suit.HEARTS),
        ))

        val playableCards = player.playableCards(trick, Suit.HEARTS)

        println(playableCards)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.DIAMONDS),

            Card(Rank.JACK, Suit.HEARTS),
        ))
    }

    @Test
    fun ifTrumpPlayedFirstAllTrumpsAllowed() {
        val player = defaultPlayer.copy(cards = listOf(
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
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.TEN, Suit.DIAMONDS),
        ))

        val playableCards = player.playableCards(trick, Suit.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),
        ))
    }
}