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

        deck.dealCards().toList().forEachIndexed { _, (playerId, cards) ->
            assertThat(cards.size, `is`(9))
            assertThat(cards, containsInAnyOrder(*GameStateHolder.players.first { it.id == playerId }.cards.toTypedArray()))
        }
    }

    @Test
    fun sortPlayerCardsWorksForSingleColor() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.QUEEN),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.KING),
        )

        val expectedCards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.HEARTS, Rank.QUEEN),
        )

        val sortedCards = Deck.sortPlayerCards(cards)
        assertThat(sortedCards, `is`(expectedCards))
    }

    @Test
    fun sortPlayerCardsWorksForMultipleColorsAndSwitchesBetweenRedAndBlack() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.QUEEN),
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.CLUBS, Rank.SIX),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.CLUBS, Rank.KING),
            Card(Suit.DIAMONDS, Rank.NINE),
            Card(Suit.HEARTS, Rank.KING),
        )

        val expectedCards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.HEARTS, Rank.QUEEN),

            Card(Suit.CLUBS, Rank.KING),
            Card(Suit.CLUBS, Rank.SIX),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.NINE),
        )

        val sortedCards = Deck.sortPlayerCards(cards)
        assertThat(sortedCards, `is`(expectedCards))
    }

    @Test
    fun sortPlayerCardsWorksForMultipleColorsAndSwitchesBetweenRedAndBlackForAllColors() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.QUEEN),
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.SPADES, Rank.TEN),
            Card(Suit.CLUBS, Rank.SIX),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.CLUBS, Rank.KING),
            Card(Suit.DIAMONDS, Rank.NINE),
            Card(Suit.SPADES, Rank.NINE),
            Card(Suit.HEARTS, Rank.KING),
        )

        val expectedCards = listOf(
            Card(Suit.CLUBS, Rank.KING),
            Card(Suit.CLUBS, Rank.SIX),

            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.HEARTS, Rank.QUEEN),

            Card(Suit.SPADES, Rank.TEN),
            Card(Suit.SPADES, Rank.NINE),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.NINE),
        )

        val sortedCards = Deck.sortPlayerCards(cards)
        assertThat(sortedCards, `is`(expectedCards))
    }
}