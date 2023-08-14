package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameStateTest {

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
    fun isLastTrickReturnsTrueOnlyWhenCurrentTrickNumberIsNine() {
        for (i in 0..9) {
            val gameState = GameState().copy(currentTrickNumber = i)
            assertThat(gameState.isLastTrick(), `is`(i == 9))
        }
    }

    @Test
    fun nextTrickThrowsIfCurrentTrickIsNotFull() {
        val gameState = defaultGameState.copy(currentTrick = Trick())

        assertThrows(IllegalStateException::class.java) {
            gameState.nextTrick()
        }
    }

    @Test
    fun nextTrick() {
        val gameState = GameState().copy(
            currentTrick = Trick(
                listOf(
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.NINE), defaultPlayerDatas[0].id),
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.EIGHT), defaultPlayerDatas[1].id),
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.ACE), defaultPlayerDatas[2].id),
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.SIX), defaultPlayerDatas[3].id),
                )
            ),
            currentTrump = Trump.CLUBS,
        )

        val newGameState = gameState.nextTrick()
        assertThat(newGameState.currentTrick, `is`(Trick()))
        assertThat(newGameState.currentTrickNumber, `is`(1))
        assertThat(newGameState.currentRoundTrickWinners, `is`(listOf(Trick.TrickWinner(defaultPlayerDatas[0].id, gameState.currentTrick))))
    }

    @Test
    fun pointsAreCalculatedCorrectly() {
        val gameState = GameState().copy(
            currentTrick = Trick(
                listOf(
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.NINE), defaultPlayerDatas[0].id),
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.EIGHT), defaultPlayerDatas[1].id),
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.ACE), defaultPlayerDatas[2].id),
                    Trick.TrickCard(Card(Suit.CLUBS, Rank.SIX), defaultPlayerDatas[3].id),
                )
            ),
            currentTrump = Trump.CLUBS,
        )

        assertThat(gameState.nextTrick().points(defaultPlayerDatas[0].id), `is`(25))
        assertThat(gameState.nextTrick().points(defaultPlayerDatas[2].id), `is`(0))
        assertThat(gameState.nextTrick().points(defaultPlayerDatas[1].id), `is`(0))
        assertThat(gameState.nextTrick().points(defaultPlayerDatas[3].id), `is`(0))
    }

    @Test
    fun pointsAreCalculatedCorrectlyAfterAllCardsArePlayed() {
        val trick = Trick(
            listOf(
                Trick.TrickCard(Card(Suit.CLUBS, Rank.NINE), defaultPlayerDatas[0].id),
                Trick.TrickCard(Card(Suit.CLUBS, Rank.EIGHT), defaultPlayerDatas[1].id),
                Trick.TrickCard(Card(Suit.CLUBS, Rank.ACE), defaultPlayerDatas[2].id),
                Trick.TrickCard(Card(Suit.CLUBS, Rank.SIX), defaultPlayerDatas[3].id),
            )
        )

        val gameState = GameState().copy(
            currentTrick = trick,
            currentTrump = Trump.CLUBS,
            currentTrickNumber = 8,
            currentRoundTrickWinners = (1 .. 8).map { Trick.TrickWinner(defaultPlayerDatas[0].id, trick) }
        )

        assertThat(gameState.nextTrick().points(defaultPlayerDatas[0].id), `is`(9*25 + 5)) // +5 for last trick
        assertThat(gameState.nextTrick().points(defaultPlayerDatas[2].id), `is`(0))
        assertThat(gameState.nextTrick().points(defaultPlayerDatas[1].id), `is`(0))
        assertThat(gameState.nextTrick().points(defaultPlayerDatas[3].id), `is`(0))
    }

    @Test
    fun playCardReturnsNewGameStateWithCardPlayed() {
        val newGameState = defaultGameState.playCard(defaultPlayerDatas[0].id, defaultPlayerDatas[0].cards[0], PlayerId.PLAYER_1)

        assertTrue(newGameState.currentTrick.trickCards
            .contains(Trick.TrickCard(defaultPlayerDatas[0].cards[0], defaultPlayerDatas[0].id))
        )

        assertFalse(newGameState.playerCards[PlayerId.PLAYER_1]!!.contains(defaultPlayerDatas[0].cards[0]))
    }
}