package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class BettingStateTest {

    private val defaultPlayerDatas = listOf(
        PlayerData().copy(email = "email1", firstName = "player1"),
        PlayerData().copy(email = "email2", firstName = "player2"),
        PlayerData().copy(email = "email3", firstName = "player3"),
        PlayerData().copy(email = "email4", firstName = "player4")
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
    fun nextBettingRoundUpdatesTheWantedElements() {
        val newBettingState = defaultBettingState.nextBettingRound(defaultPlayerDatas[1])

        assertThat(newBettingState.currentPlayerIdx, `is`(1))

        newBettingState.playerDatas.forEach {
            assertThat(it.cards.size, `is`(9))
        }

        assertThat(newBettingState.currentBetter.firstName, `is`(defaultPlayerDatas[1].firstName))
        assertTrue(newBettingState.bets.isEmpty())
    }

    @Test
    fun nextPlayerWorksForNullBet() {
        val newBettingState = defaultBettingState.nextPlayer()

        assertThat(newBettingState.currentBetter.firstName, `is`(defaultPlayerDatas[1].firstName))
        assertThat(newBettingState.bets.size, `is`(defaultBettingState.bets.size))
    }

    @Test
    fun nextPlayerWorksForNonNullBet() {
        val newBet = Bet(defaultPlayerDatas[1], Trump.OBE_ABE, BetHeight.HUNDRED_TEN)
        val newBettingState = defaultBettingState.nextPlayer(newBet)

        assertThat(newBettingState.currentBetter.firstName, `is`(defaultPlayerDatas[1].firstName))
        assertThat(newBettingState.bets.size, `is`(defaultBettingState.bets.size + 1))
        assertThat(newBettingState.bets.last(), `is`(newBet))
    }

    @Test
    fun availableBetsAreAllHigherThanTheLastBet() {
        val bettingState = defaultBettingState.copy(bets = listOf(Bet(defaultPlayerDatas[0], Trump.UNGER_UFE, BetHeight.SEVENTY)))

        assertThat(bettingState.availableBets(), `is`(BetHeight.values().toList().subList(BetHeight.SEVENTY.ordinal + 1, BetHeight.values().size)))
    }

    @Test
    fun noBetsAllowsForAllPossibleBetsExceptNone() {
        val bettingState = defaultBettingState.copy(bets = listOf())

        assertThat(bettingState.availableBets(), `is`(BetHeight.values().toList().subList(1, BetHeight.values().size)))
    }

    @Test
    fun availableTrumpsContainsAllTrumpsIfLastBetNotByCurrentPlayer() {
        val emptyBetsBettingState = defaultBettingState.copy(currentBetter = defaultPlayerDatas[0], bets = listOf())
        val nonEmptyBetsBettingState = defaultBettingState.copy(currentBetter = defaultPlayerDatas[0], bets = listOf(Bet(defaultPlayerDatas[1], Trump.UNGER_UFE, BetHeight.SEVENTY)))

        val bettingStates = listOf(emptyBetsBettingState, nonEmptyBetsBettingState)

        bettingStates.forEach {
            assertThat(it.availableTrumps(), `is`(Trump.values().toList()))
        }
    }

    @Test
    fun availableTrumpsDoesNotContainTheTrumpOfLastBetIfCurrentBetterPlacedThatBet() {
        val bettingState = defaultBettingState.copy(currentBetter = defaultPlayerDatas[0], bets = listOf(Bet(defaultPlayerDatas[0], Trump.UNGER_UFE, BetHeight.SEVENTY)))

        assertThat(bettingState.availableTrumps(), `is`(Trump.values().toList().filter { it != Trump.UNGER_UFE }))
    }

    @Test
    fun startGameThrowsWhenBetsAreEmpty() {
        val bettingState = defaultBettingState.copy(bets = listOf())

        assertThrows(IllegalStateException::class.java) {
            bettingState.startGame()
        }
    }

    @Test
    fun startGameReturnsGameStateWithNecessaryInformation() {
        val bettingState = defaultBettingState.copy(bets = listOf(Bet(defaultPlayerDatas[0], Trump.UNGER_UFE, BetHeight.SEVENTY)))

        val expectedGameState = GameState(
            currentPlayerIdx = 0,
            playerDatas = defaultPlayerDatas,
            currentPlayerData = defaultPlayerDatas[0],
            startingPlayerData = defaultPlayerDatas[0],
            currentRound = 0,
            currentTrick = Trick(),
            currentTrickNumber = 0,
            currentTrump = Trump.UNGER_UFE,
            playerCards = defaultPlayerDatas.associateWith { it.cards }
        )

        assertThat(bettingState.startGame(), `is`(expectedGameState))
    }

    @Test
    fun betHeightToStringWorksForNumbersNoBetAndMatch() {
        assertThat(
            BetHeight.values().map { it.toString() },
            `is`(listOf("no bet", "40", "50", "60", "70", "80", "90", "100", "110", "120", "130", "140", "150", "157", "match"))
        )
    }

    @Test
    fun betHeightFromStringConvertsToCorrectBetHeight() {
        val betStrings = listOf("no bet", "40", "50", "60", "70", "80", "90", "100", "110", "120", "130", "140", "150", "157", "match")
        betStrings.map { BetHeight.fromString(it) }.forEachIndexed { index, betHeight ->
            assertThat(betHeight, `is`(BetHeight.values()[index]))
        }
    }
}