package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class BettingStateTest {

    private val defaultPlayerDatas = listOf(
        PlayerData().copy(id = PlayerId.PLAYER_1, firstName = "player1"),
        PlayerData().copy(id = PlayerId.PLAYER_2, firstName = "player2"),
        PlayerData().copy(id = PlayerId.PLAYER_3, firstName = "player3"),
        PlayerData().copy(id = PlayerId.PLAYER_4, firstName = "player4")
    )

    private val defaultBettingState = BettingState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentBetterId = defaultPlayerDatas[0].id,
        startingBetterId = defaultPlayerDatas[0].id,
        jassType = JassType.SIDI_BARRANI,
        bets = listOf(Bet(defaultPlayerDatas[1].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
        betActions = listOf(Bet.BetAction.BET),
        gameState = GameState(),
        score = Score.INITIAL,
    )

    @Before
    fun setUp() {
        GameStateHolder.players = defaultPlayerDatas
        GameStateHolder.bettingState = defaultBettingState
    }

    @Test
    fun nextBettingRoundUpdatesTheWantedElements() {
        val newBettingState = defaultBettingState.nextBettingRound(defaultPlayerDatas[1].id, score = Score.INITIAL)

        PlayerId.values().forEach {
            assertThat(GameStateHolder.players.first { p -> p.id == it }.cards.size, `is`(9))
        }

        assertThat(newBettingState.currentBetterId, `is`(defaultPlayerDatas[1].id))
        assertTrue(newBettingState.bets.isEmpty())
    }

    @Test
    fun nextPlayerWorksForNullBet() {
        val newBettingState = defaultBettingState.nextPlayer()

        assertThat(newBettingState.currentBetterId, `is`(defaultPlayerDatas[1].id))
        assertThat(newBettingState.bets.size, `is`(defaultBettingState.bets.size))
    }

    @Test
    fun nextPlayerWorksForNonNullBet() {
        val newBet = Bet(defaultPlayerDatas[1].id, Trump.OBE_ABE, BetHeight.HUNDRED_TEN)
        val newBettingState = defaultBettingState.nextPlayer(newBet)

        assertThat(newBettingState.currentBetterId, `is`(defaultPlayerDatas[1].id))
        assertThat(newBettingState.bets.size, `is`(defaultBettingState.bets.size + 1))
        assertThat(newBettingState.bets.last(), `is`(newBet))
    }

    @Test
    fun availableBetsAreAllHigherThanTheLastBet() {
        val bettingState = defaultBettingState.copy(bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.SEVENTY)))

        assertThat(bettingState.availableBets(), `is`(BetHeight.values().toList().subList(BetHeight.SEVENTY.ordinal + 1, BetHeight.values().size)))
    }

    @Test
    fun noBetsAllowsForAllPossibleBetsExceptNone() {
        val bettingState = defaultBettingState.copy(bets = listOf())

        assertThat(bettingState.availableBets(), `is`(BetHeight.values().toList().subList(1, BetHeight.values().size)))
    }

    @Test
    fun availableTrumpsContainsAllTrumpsIfLastBetNotByCurrentPlayer() {
        val emptyBetsBettingState = defaultBettingState.copy(currentBetterId = defaultPlayerDatas[0].id, bets = listOf())
        val nonEmptyBetsBettingState = defaultBettingState.copy(currentBetterId = defaultPlayerDatas[0].id, bets = listOf(Bet(defaultPlayerDatas[1].id, Trump.UNGER_UFE, BetHeight.SEVENTY)))

        val bettingStates = listOf(emptyBetsBettingState, nonEmptyBetsBettingState)

        bettingStates.forEach {
            assertThat(it.availableTrumps(), `is`(Trump.values().toList()))
        }
    }

    @Test
    fun availableTrumpsDoesNotContainTheTrumpOfLastBetIfCurrentBetterPlacedThatBet() {
        val bettingState = defaultBettingState.copy(currentBetterId = defaultPlayerDatas[0].id, bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.SEVENTY)))

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
        val winningBet = Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.SEVENTY)
        val bettingState = defaultBettingState.copy(bets = listOf(winningBet))

        val expectedGameState = GameState(
            currentUserId = PlayerId.PLAYER_1,
            playerEmails = listOf(),
            currentPlayerId = defaultPlayerDatas[0].id,
            startingPlayerId = defaultPlayerDatas[0].id,
            currentRound = 0,
            jassType = JassType.SIDI_BARRANI,
            roundState = RoundState.initial(winningBet.trump, defaultPlayerDatas[0].id),
            winningBet = winningBet,
            playerCards = defaultPlayerDatas.associate { it.id to it.cards },
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

    @Test
    fun availableBetsReturnsAllBetsForEmptyBetsList() {
        val bettingState = BettingState()
        assertThat(bettingState.availableBets(), `is`(BetHeight.values().toList().subList(1, BetHeight.values().size)))
    }

    @Test
    fun availableBetsReturnsAllBetsHigherThanTheLastOne() {
        val bettingState = BettingState().copy(bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.SEVENTY)))

        assertThat(bettingState.availableBets(), `is`(BetHeight.values().toList().subList(BetHeight.SEVENTY.ordinal + 1, BetHeight.values().size)))
    }

    @Test
    fun availableBetsReturnsEmptyListAfterMatchHasBeenBid() {
        val bettingState = BettingState().copy(bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.MATCH)))

        assertThat(bettingState.availableBets(), `is`(listOf()))
    }
}