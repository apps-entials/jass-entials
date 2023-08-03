package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
        currentPlayerIdx = 0,
        playerDatas = defaultPlayerDatas,
        currentPlayerData = defaultPlayerDatas[0],
        startingPlayerData = defaultPlayerDatas[0],
        currentRound = 0,
        currentTrick = Trick(),
        currentRoundTrickWinners = listOf(),
        currentTrickNumber = 0,
        currentTrump = Trump.CLUBS,
        playerCards = defaultPlayerDatas.associateWith { it.cards },
    )

    private val defaultBettingState = BettingState(
        currentPlayerIdx = 0,
        playerDatas = defaultPlayerDatas,
        currentBetter = defaultPlayerDatas[0],
        jassType = JassType.SIDI_BARAHNI,
        bets = listOf(Bet(defaultPlayerDatas[0], Trump.UNGER_UFE, BetHeight.HUNDRED)),
        gameState = GameState(),
    )

    @Test
    fun playCardPlaysOneOfTheHandCardsAndRemovesItFromTheHand() {
        val player = CpuPlayer(defaultPlayerDatas[0])
        val oldCards = player.playerData.cards
        val card = player.playCard(defaultGameState).join()

        assertTrue(oldCards.contains(card))
        // player now shouldn't have the card anymore
        assertFalse(player.playerData.cards.contains(card))
    }

    @Test
    fun cpuPlayerOnceInAWhileSelectsABet() {
        val player = CpuPlayer(defaultPlayerDatas[0])

        for (i in 0..100) {
            val newBettingState = player.bet(defaultBettingState).join()

            assertThat(newBettingState.currentBetter.email,
                `is`(defaultBettingState.playerDatas[defaultBettingState.playerDatas
                    .indexOfFirst { it == defaultBettingState.currentBetter } + 1 % 4]
                    .email))
        }
    }

    @Test
    fun whenNoAvailableBetsPlayerPasses() {
        val oldBettingState = defaultBettingState.copy(bets = listOf(Bet(defaultPlayerDatas[3], Trump.UNGER_UFE, BetHeight.MATCH)))

        val player = CpuPlayer(defaultPlayerDatas[0])

        val newBettingState = player.bet(defaultBettingState).join()

        assertThat(newBettingState.currentBetter.email,
            `is`(defaultBettingState.playerDatas[defaultBettingState.playerDatas
                .indexOfFirst { it == defaultBettingState.currentBetter } + 1 % 4]
                .email))
    }
}