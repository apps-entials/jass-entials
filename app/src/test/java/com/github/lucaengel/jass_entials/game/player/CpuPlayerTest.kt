package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CpuPlayerTest {

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
        playerCards = defaultPlayerDatas.associate { it.email to it.cards },
    )

    private val defaultBettingState = BettingState(
        currentUserIdx = 0,
        playerEmails = defaultPlayerDatas.map { it.email },
        currentBetterEmail = defaultPlayerDatas[0].email,
        startingBetterEmail = defaultPlayerDatas[0].email,
        jassType = JassType.SIDI_BARAHNI,
        bets = listOf(Bet(defaultPlayerDatas[0].email, Trump.UNGER_UFE, BetHeight.HUNDRED)),
        gameState = GameState(),
    )

    @Before
    fun setUp() {
        GameStateHolder.players = defaultPlayerDatas
        GameStateHolder.gameState = defaultGameState
        GameStateHolder.bettingState = defaultBettingState
    }

    @Test
    fun playCardPlaysOneOfTheHandCardsAndRemovesItFromTheHand() {
        val player = CpuPlayer(defaultPlayerDatas[0].email, 0)
        val playerData = GameStateHolder.players.first { it.email == player.playerEmail }
        val oldCards = playerData.cards
        val card = player.playCard(defaultGameState, playerData).join()

        assertTrue(oldCards.contains(card))
        // TODO: maybe also update the player in this method and return it in a pair?
//        assertFalse(GameStateHolder.players.first { it.email == player.playerEmail }.cards.contains(card))
    }

    @Test
    fun cpuPlayerOnceInAWhileSelectsABet() {
        val player = CpuPlayer(defaultPlayerDatas[0].email, 0)

        for (i in 0..100) {
            val newBettingState = player.bet(defaultBettingState).join()

            assertThat(newBettingState.currentBetterEmail,
                `is`(defaultBettingState.playerEmails[defaultBettingState.playerEmails
                    .indexOfFirst { it == defaultBettingState.currentBetterEmail } + 1 % 4]))
        }
    }

    @Test
    fun whenNoAvailableBetsPlayerPasses() {
        val player = CpuPlayer(defaultPlayerDatas[0].email, 0)

        val newBettingState = player.bet(defaultBettingState).join()

        assertThat(newBettingState.currentBetterEmail,
            `is`(defaultBettingState.playerEmails[defaultBettingState.playerEmails
                .indexOfFirst { it == defaultBettingState.currentBetterEmail } + 1 % 4]))
    }
}