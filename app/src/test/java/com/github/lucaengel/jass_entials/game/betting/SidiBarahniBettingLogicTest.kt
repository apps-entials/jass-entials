package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

class SidiBarahniBettingLogicTest {

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

    private val defaultBettingState = BettingState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentBetterId = defaultPlayerDatas[0].id,
        startingBetterId = defaultPlayerDatas[0].id,
        jassType = JassType.SIDI_BARAHNI,
        bets = listOf(Bet(defaultPlayerDatas[1].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
        betActions = listOf(Bet.BetAction.BET),
        gameState = GameState(),
        score = Score.INITIAL,
    )

    private lateinit var bettingLogic: BettingLogic

    @Before
    fun setUp() {
        bettingLogic = SidiBarahniBettingLogic()
    }

    @Test
    fun ifCurrentPlayerWasLastToBetAndSignalsWantingToStartWithNullBetCurrentPlayerIsReturned() {
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[1].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET, Bet.BetAction.PASS, Bet.BetAction.PASS, Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetter = defaultPlayerDatas[1].id,
            currentPlayerBet = null,
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[1].id))
    }

    @Test
    fun ifCurrentPlayerDoesNotSignalStartingThePlayerOnTheRightStarts() {
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetter = defaultPlayerDatas[1].id,
            currentPlayerBet = null,
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[2].id))
    }

    @Test
    fun ifCurrentPlayerDoesNotSignalStartingThePlayerOnTheRightStartsWrapsAroundList() {
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetter = defaultPlayerDatas[3].id,
            currentPlayerBet = Bet(defaultPlayerDatas[3].id, Trump.UNGER_UFE, BetHeight.HUNDRED),
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[0].id))
    }

    @Test
    fun whenCurrentPlayerBidsOverTheirLastBetThePlayerToTheirRightIsNext() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[2].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetter = defaultPlayerDatas[2].id,
            currentPlayerBet = Bet(defaultPlayerDatas[2].id, Trump.SPADES, BetHeight.HUNDRED_FIFTY),
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[3].id))
    }

    @Test
    fun availableActionsReturnsBetAndPassIfNoBetsPlacedPreviously() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(),
        )

        val result = bettingLogic.availableActions(
            currentBetter = defaultPlayerDatas[2].id,
            bettingState = bettingState,
        )
        assertThat(result, Matchers.containsInAnyOrder(
            Bet.BetAction.BET,
            Bet.BetAction.PASS
        ))
    }

    @Test
    fun ifCurrentBetterIsLastBetterAndHadBidAMatchInTheLastRoundCurrentBetterCanOnlyStartTheGame() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.MATCH)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetter = defaultPlayerDatas[0].id,
            bettingState = bettingState,
        )
        assertThat(result, Matchers.containsInAnyOrder(
            Bet.BetAction.START_GAME
        ))
    }

    @Test
    fun ifCurrentBetterIsLastBetterButDidNotBitAMatchTheyCanStartOrBid() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetter = defaultPlayerDatas[0].id,
            bettingState = bettingState,
        )
        assertThat(result, Matchers.containsInAnyOrder(
            Bet.BetAction.START_GAME,
            Bet.BetAction.BET,
        ))
    }

    @Test
    fun playerCanBetPassOrDoubleIfOpposingTeamMemberBidLast() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetter = defaultPlayerDatas[1].id,
            bettingState = bettingState,
        )
        assertThat(result, Matchers.containsInAnyOrder(
            Bet.BetAction.BET,
            Bet.BetAction.PASS,
            Bet.BetAction.DOUBLE,
        ))
    }

    @Test
    fun currentPlayerCanBetOrPassIfTeamMemberWasLastToBidNotMatch() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetter = defaultPlayerDatas[2].id,
            bettingState = bettingState,
        )
        assertThat(result, Matchers.containsInAnyOrder(
            Bet.BetAction.BET,
            Bet.BetAction.PASS,
        ))
    }
}