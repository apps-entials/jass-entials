package com.github.lucaengel.jass_entials.data.cards

import org.hamcrest.MatcherAssert.assertThat
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

        val playerData = listOf(
            PlayerData().copy(firstName = "player1"),
            PlayerData().copy(firstName = "player2"),
            PlayerData().copy(firstName = "player3"),
            PlayerData().copy(firstName = "player4")
        )

        deck.dealCards(playerData).toList().forEachIndexed { _, (playerData, cards) ->
            assertThat(cards.size, `is`(9))
            assertThat(cards, `is`(playerData.cards))
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