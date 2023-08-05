package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.`is`
import org.junit.Test

class DeckTest {

    @Test
    fun shuffledReturnsDeckWithSameAmountOfCardsInside() {
        val deck = Deck.STANDARD_DECK.shuffled().cards

        assertThat(deck.size, `is`(36))
    }

    @Test
    fun dealCardsDeals9CardsToEachPersonWithAFullDeck() {
        val deck = Deck.STANDARD_DECK

        val playerDatas = listOf(
            PlayerData().copy(email = "email_1", firstName = "player1"),
            PlayerData().copy(email = "email_2", firstName = "player2"),
            PlayerData().copy(email = "email_3", firstName = "player3"),
            PlayerData().copy(email = "email_4", firstName = "player4")
        )

        deck.dealCards(playerDatas.map { it.email }).toList().forEachIndexed { _, (email, cards) ->
            assertThat(cards.size, `is`(9))
            assertThat(cards, containsInAnyOrder(*GameStateHolder.players.first { it.email == email }.cards.toTypedArray()))
        }
    }

    @Test
    fun sortPlayerCardsWorksForSingleColor() {
        val cards = listOf(
            Card(Rank.QUEEN, Suit.HEARTS),
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.KING, Suit.HEARTS),
        )

        val expectedCards = listOf(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.KING, Suit.HEARTS),
            Card(Rank.QUEEN, Suit.HEARTS),
        )

        val sortedCards = Deck.sortPlayerCards(cards)
        assertThat(sortedCards, `is`(expectedCards))
    }

    @Test
    fun sortPlayerCardsWorksForMultipleColorsAndSwitchesBetweenRedAndBlack() {
        val cards = listOf(
            Card(Rank.QUEEN, Suit.HEARTS),
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.SIX, Suit.CLUBS),
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.KING, Suit.CLUBS),
            Card(Rank.NINE, Suit.DIAMONDS),
            Card(Rank.KING, Suit.HEARTS),
        )

        val expectedCards = listOf(
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.KING, Suit.HEARTS),
            Card(Rank.QUEEN, Suit.HEARTS),

            Card(Rank.KING, Suit.CLUBS),
            Card(Rank.SIX, Suit.CLUBS),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.NINE, Suit.DIAMONDS),
        )

        val sortedCards = Deck.sortPlayerCards(cards)
        assertThat(sortedCards, `is`(expectedCards))
    }

    @Test
    fun sortPlayerCardsWorksForMultipleColorsAndSwitchesBetweenRedAndBlackForAllColors() {
        val cards = listOf(
            Card(Rank.QUEEN, Suit.HEARTS),
            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.TEN, Suit.SPADES),
            Card(Rank.SIX, Suit.CLUBS),
            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.KING, Suit.CLUBS),
            Card(Rank.NINE, Suit.DIAMONDS),
            Card(Rank.NINE, Suit.SPADES),
            Card(Rank.KING, Suit.HEARTS),
        )

        val expectedCards = listOf(
            Card(Rank.KING, Suit.CLUBS),
            Card(Rank.SIX, Suit.CLUBS),

            Card(Rank.ACE, Suit.HEARTS),
            Card(Rank.KING, Suit.HEARTS),
            Card(Rank.QUEEN, Suit.HEARTS),

            Card(Rank.TEN, Suit.SPADES),
            Card(Rank.NINE, Suit.SPADES),

            Card(Rank.ACE, Suit.DIAMONDS),
            Card(Rank.NINE, Suit.DIAMONDS),
        )

        val sortedCards = Deck.sortPlayerCards(cards)
        assertThat(sortedCards, `is`(expectedCards))
    }
}