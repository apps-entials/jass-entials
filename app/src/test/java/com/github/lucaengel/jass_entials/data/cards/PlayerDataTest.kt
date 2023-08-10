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

        assertThat(playableCards, Matchers.containsInAnyOrder(*defaultPlayerData.cards.toTypedArray()))
    }

    @Test
    fun playableCardsShowsTrumpCardsAndPlayableSuitCardsWhenJustASuitHasBeenPlayed() {
        val trick = Trick(listOf(Trick.TrickCard(Card(Rank.SEVEN, Suit.HEARTS), "")))

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
            Trick.TrickCard(Card(Rank.SEVEN, Suit.DIAMONDS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.HEARTS), "email_2"),
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
            Trick.TrickCard(Card(Rank.SEVEN, Suit.DIAMONDS), "email_1"),
            Trick.TrickCard(Card(Rank.ACE, Suit.HEARTS), "email_2"),
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
            Trick.TrickCard(Card(Rank.SEVEN, Suit.DIAMONDS), "email_1"),
            Trick.TrickCard(Card(Rank.NINE, Suit.HEARTS), "email_2"),
        ))

        val playableCards = player.playableCards(trick, Trump.HEARTS)

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
            Trick.TrickCard(Card(Rank.TEN, Suit.HEARTS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
        ))

        val playableCards = player.playableCards(trick, Trump.HEARTS)

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
            Trick.TrickCard(Card(Rank.TEN, Suit.HEARTS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
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
            Trick.TrickCard(Card(Rank.TEN, Suit.CLUBS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
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
            Trick.TrickCard(Card(Rank.TEN, Suit.HEARTS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
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
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.CLUBS), "email_2"),
        ))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }

    @Test
    fun trumpJackDoesNotHaveToBePlayedWhenTrumpIsOutAndJackIsTheOnlyTrumpCard() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),

            Card(Rank.JACK, Suit.CLUBS),
        ))

        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.TEN, Suit.CLUBS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
        ))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        // All cards should be able to be played since the current player only has the trump jack
        // of the trump suit that is out
        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }

    @Test
    fun playerMustPlayTheJackIfItIsTheOnlyCardToHoldSuitWithAndItIsNotTrump() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),

            Card(Rank.JACK, Suit.CLUBS),
        ))

        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.TEN, Suit.CLUBS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
        ))

        val playableCards = player.playableCards(trick, Trump.UNGER_UFE)

        // All cards should be able to be played since the current player only has the trump jack
        // of the trump suit that is out
        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.JACK, Suit.CLUBS)
        ))
    }

    @Test
    fun playerMustHoldSuitIfTheyCanAndTheyHaveNoTrumpCards() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),

            Card(Rank.TEN, Suit.CLUBS),
        ))

        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.TEN, Suit.CLUBS), "email_1"),
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
        ))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        // All cards should be able to be played since the current player only has the trump jack
        // of the trump suit that is out
        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Rank.TEN, Suit.CLUBS)
        ))
    }

    @Test
    fun allCardsArePlayableIfASuitIsOutThatThePlayerDoesNotHaveAndThePlayerHasTrumpAndNonTrumpCards() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.SIX, Suit.HEARTS),
            Card(Rank.NINE, Suit.HEARTS),

            Card(Rank.ACE, Suit.SPADES),
            Card(Rank.SEVEN, Suit.SPADES),
            Card(Rank.SIX, Suit.SPADES),

            Card(Rank.JACK, Suit.CLUBS),
            Card(Rank.TEN, Suit.CLUBS),
        ))

        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.TEN, Suit.DIAMONDS), "email_2"),
            Trick.TrickCard(Card(Rank.TEN, Suit.CLUBS), "email_1"),
        ))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        // All cards should be able to be played since the current player does not have any diamonds
        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }
}
