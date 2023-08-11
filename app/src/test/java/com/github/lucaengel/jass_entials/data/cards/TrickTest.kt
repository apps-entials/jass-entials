package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class TrickTest {

    private val defaultPlayerDatas = listOf(
        PlayerData().copy(
            email = "email1",
            firstName = "player1",
            cards = Deck.STANDARD_DECK.cards.subList(0, 9)),
        PlayerData().copy(
            email = "email2",
            firstName = "player2",
            cards = Deck.STANDARD_DECK.cards.subList(9, 18)),
        PlayerData().copy(
            email = "email3",
            firstName = "player3",
            cards = Deck.STANDARD_DECK.cards.subList(18, 27)),
        PlayerData().copy(
            email = "email4",
            firstName = "player4",
            cards = Deck.STANDARD_DECK.cards.subList(27, 36)),
    )

    private val defaultGameState = GameState(
        currentUserIdx = 0,
        playerEmails = defaultPlayerDatas.map { it.email },
        currentPlayerEmail = defaultPlayerDatas[0].email,
        startingPlayerEmail = defaultPlayerDatas[0].email,
        currentRound = 0,
        currentTrick = Trick(),
        currentRoundTrickWinners = listOf(),
        currentTrickNumber = 0,
        currentTrump = Trump.CLUBS,
        winningBet = Bet(),
        playerCards = defaultPlayerDatas.associate { it.email to it.cards },
    )

    @Before
    fun setUp() {
        GameStateHolder.players = defaultPlayerDatas
        GameStateHolder.gameState = defaultGameState
    }

    @Test
    fun isFullReturnsTrueOnlyForTrickWith4CardsInside() {
        var trick = Trick()
        for (i in 0..3) {
            trick = trick.copy(trickCards = trick.trickCards + Trick.TrickCard(
                Card(
                    Rank.SIX,
                    Suit.CLUBS
                ), "email_1"
            )
            )

            assertThat(trick.isFull(), `is`(i == 3))
        }
    }

    @Test
    fun winnerReturnsHighestCardPlayerOfStartingSuit() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.SEVEN, Suit.HEARTS), "1"),
            Trick.TrickCard(Card(Rank.ACE, Suit.DIAMONDS), "2"),
            Trick.TrickCard(Card(Rank.EIGHT, Suit.HEARTS), "3"),
            Trick.TrickCard(Card(Rank.SEVEN, Suit.CLUBS), "4"),
        ))
        assertThat(trick.winner(Trump.SPADES).playerEmail, `is`("3"))
    }

    @Test
    fun winnerReturnsHighestTrumpPlayer() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.SEVEN, Suit.CLUBS), "1"),
            Trick.TrickCard(Card(Rank.ACE, Suit.DIAMONDS), "2"),
            Trick.TrickCard(Card(Rank.EIGHT, Suit.HEARTS), "3"),
            Trick.TrickCard(Card(Rank.SEVEN, Suit.CLUBS), "4"),
        ))
        assertThat(trick.winner(Trump.HEARTS).playerEmail, `is`("3"))
    }

    @Test
    fun winnerReturnsCorrectPlayerForUngerUfe() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.ACE, Suit.CLUBS), "1"),
            Trick.TrickCard(Card(Rank.ACE, Suit.DIAMONDS), "2"),
            Trick.TrickCard(Card(Rank.EIGHT, Suit.HEARTS), "3"),
            Trick.TrickCard(Card(Rank.SIX, Suit.CLUBS), "4"),
        ))
        assertThat(trick.winner(Trump.UNGER_UFE).playerEmail, `is`("4"))
    }

    @Test
    fun trickWinnerConstructorThrowsWhenNon4CardsInTheTrick() {
        assertThrows(IllegalArgumentException::class.java) {
            Trick.TrickWinner(
                defaultPlayerDatas[0].email,
                Trick(listOf(
                Trick.TrickCard(Card(Rank.ACE, Suit.CLUBS), "1"),
                Trick.TrickCard(Card(Rank.ACE, Suit.DIAMONDS), "2"),
                Trick.TrickCard(Card(Rank.EIGHT, Suit.HEARTS), "3"),
                ))
            )
        }
    }

    @Test
    fun trickWinnerConstructorThrowsWhenNonExistentEmailIsPassed() {
        assertThrows(IllegalArgumentException::class.java) {
            Trick.TrickWinner(
                "nonExistentMail",
                Trick(listOf(
                    Trick.TrickCard(Card(Rank.ACE, Suit.CLUBS), "1"),
                    Trick.TrickCard(Card(Rank.ACE, Suit.DIAMONDS), "2"),
                    Trick.TrickCard(Card(Rank.EIGHT, Suit.HEARTS), "3"),
                    Trick.TrickCard(Card(Rank.NINE, Suit.HEARTS), "4"),
                ))
            )
        }
    }

    @Test
    fun trickWinnerWorksForLegalInputs() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Rank.ACE, Suit.CLUBS), "1"),
            Trick.TrickCard(Card(Rank.ACE, Suit.DIAMONDS), "2"),
            Trick.TrickCard(Card(Rank.EIGHT, Suit.HEARTS), "3"),
            Trick.TrickCard(Card(Rank.NINE, Suit.HEARTS), "4"),
        ))

        val trickWinner = Trick.TrickWinner(
            "1",
            trick
        )

        assertThat(trickWinner.playerEmail, `is`("1"))
        assertThat(trickWinner.trick, `is`(trick))
    }
}