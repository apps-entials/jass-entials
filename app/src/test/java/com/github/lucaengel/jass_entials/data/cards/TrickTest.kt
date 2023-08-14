package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class TrickTest {

    private val defaultPlayerDatas = listOf(
        PlayerData().copy(
            id = PlayerId.PLAYER_1,
            firstName = "player1",
            cards = Deck.STANDARD_DECK.cards.subList(0, 9)),
        PlayerData().copy(
            id = PlayerId.PLAYER_2,
            firstName = "player2",
            cards = Deck.STANDARD_DECK.cards.subList(9, 18)),
        PlayerData().copy(
            id = PlayerId.PLAYER_3,
            firstName = "player3",
            cards = Deck.STANDARD_DECK.cards.subList(18, 27)),
        PlayerData().copy(
            id = PlayerId.PLAYER_4,
            firstName = "player4",
            cards = Deck.STANDARD_DECK.cards.subList(27, 36)),
    )

    private val defaultGameState = GameState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentPlayerId = defaultPlayerDatas[0].id,
        startingPlayerId = defaultPlayerDatas[0].id,
        currentRound = 0,
        currentTrick = Trick(),
        currentRoundTrickWinners = listOf(),
        currentTrickNumber = 0,
        currentTrump = Trump.CLUBS,
        winningBet = Bet(),
        playerCards = defaultPlayerDatas.associate { it.id to it.cards },
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
                    Suit.CLUBS,
                    Rank.SIX
                ), PlayerId.PLAYER_1
            )
            )

            assertThat(trick.isFull(), `is`(i == 3))
        }
    }

    @Test
    fun winnerReturnsHighestCardPlayerOfStartingSuit() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Suit.HEARTS, Rank.SEVEN), PlayerId.PLAYER_1),
            Trick.TrickCard(Card(Suit.DIAMONDS, Rank.ACE), PlayerId.PLAYER_2),
            Trick.TrickCard(Card(Suit.HEARTS, Rank.EIGHT), PlayerId.PLAYER_3),
            Trick.TrickCard(Card(Suit.CLUBS, Rank.SEVEN), PlayerId.PLAYER_4),
        ))
        assertThat(trick.winner(Trump.SPADES).playerId, `is`(PlayerId.PLAYER_3))
    }

    @Test
    fun winnerReturnsHighestTrumpPlayer() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Suit.CLUBS, Rank.SEVEN), PlayerId.PLAYER_1),
            Trick.TrickCard(Card(Suit.DIAMONDS, Rank.ACE), PlayerId.PLAYER_2),
            Trick.TrickCard(Card(Suit.HEARTS, Rank.EIGHT), PlayerId.PLAYER_3),
            Trick.TrickCard(Card(Suit.CLUBS, Rank.SEVEN), PlayerId.PLAYER_4),
        ))
        assertThat(trick.winner(Trump.HEARTS).playerId, `is`(PlayerId.PLAYER_3))
    }

    @Test
    fun winnerReturnsCorrectPlayerForUngerUfe() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Suit.CLUBS, Rank.ACE), PlayerId.PLAYER_1),
            Trick.TrickCard(Card(Suit.DIAMONDS, Rank.ACE), PlayerId.PLAYER_2),
            Trick.TrickCard(Card(Suit.HEARTS, Rank.EIGHT), PlayerId.PLAYER_3),
            Trick.TrickCard(Card(Suit.CLUBS, Rank.SIX), PlayerId.PLAYER_4),
        ))
        assertThat(trick.winner(Trump.UNGER_UFE).playerId, `is`(PlayerId.PLAYER_4))
    }

    @Test
    fun trickWinnerConstructorThrowsWhenNon4CardsInTheTrick() {
        assertThrows(IllegalArgumentException::class.java) {
            Trick.TrickWinner(
                defaultPlayerDatas[0].id,
                Trick(listOf(
                Trick.TrickCard(Card(Suit.CLUBS, Rank.ACE), PlayerId.PLAYER_1),
                Trick.TrickCard(Card(Suit.DIAMONDS, Rank.ACE), PlayerId.PLAYER_2),
                Trick.TrickCard(Card(Suit.HEARTS, Rank.EIGHT), PlayerId.PLAYER_3),
                ))
            )
        }
    }

    @Test
    fun trickWinnerWorksForLegalInputs() {
        val trick = Trick(listOf(
            Trick.TrickCard(Card(Suit.CLUBS, Rank.ACE), PlayerId.PLAYER_1),
            Trick.TrickCard(Card(Suit.DIAMONDS, Rank.ACE), PlayerId.PLAYER_2),
            Trick.TrickCard(Card(Suit.HEARTS, Rank.EIGHT), PlayerId.PLAYER_3),
            Trick.TrickCard(Card(Suit.HEARTS, Rank.NINE), PlayerId.PLAYER_4),
        ))

        val trickWinner = Trick.TrickWinner(
            PlayerId.PLAYER_1,
            trick
        )

        assertThat(trickWinner.playerId, `is`(PlayerId.PLAYER_1))
        assertThat(trickWinner.trick, `is`(trick))
    }
}