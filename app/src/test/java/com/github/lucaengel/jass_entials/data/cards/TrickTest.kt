package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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
        roundState = RoundState.initial(defaultPlayerDatas[0].id, Trump.SPADES),
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
        var trick = Trick.initial(PlayerId.PLAYER_1, Trump.SPADES)
        for (i in 0..3) {
            trick = trick.copy(cards = trick.cards + Card(
                    Suit.CLUBS,
                    Rank.SIX)
            )

            assertThat(trick.isFull(), `is`(i == 3))
        }
    }

    @Test
    fun winnerReturnsHighestCardPlayerOfStartingSuit() {
        val trick = Trick.initial(PlayerId.PLAYER_1, Trump.HEARTS)
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.SEVEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.ACE))
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.EIGHT))
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.SEVEN))

        assertThat(trick.winner(), `is`(PlayerId.PLAYER_3))
    }

    @Test
    fun winnerReturnsHighestTrumpPlayer() {
        val trick = Trick.initial(PlayerId.PLAYER_1, Trump.HEARTS)
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.SEVEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.ACE))
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.EIGHT))
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.SEVEN))

        assertThat(trick.winner(), `is`(PlayerId.PLAYER_3))
    }

    @Test
    fun winnerReturnsCorrectPlayerForUngerUfe() {
        val trick = Trick.initial(PlayerId.PLAYER_1, Trump.UNGER_UFE)
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.ACE))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.ACE))
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.EIGHT))
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.SIX))

        assertThat(trick.winner(), `is`(PlayerId.PLAYER_4))
    }

    @Test
    fun trickWinnerWorksForLegalInputs() {
        val trick = Trick.initial(PlayerId.PLAYER_1, Trump.HEARTS)
             .withNewCardPlayed(Card(Suit.CLUBS, Rank.ACE))
             .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.ACE))
             .withNewCardPlayed(Card(Suit.HEARTS, Rank.EIGHT))
             .withNewCardPlayed(Card(Suit.HEARTS, Rank.NINE))

        assertThat(trick.winner(), `is`(PlayerId.PLAYER_4))
    }
}