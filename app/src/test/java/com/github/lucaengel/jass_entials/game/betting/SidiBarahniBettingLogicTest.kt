package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
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

    private val defaultBettingState = BettingState(
        currentUserIdx = 0,
        playerEmails = defaultPlayerDatas.map { it.email },
        currentBetterEmail = defaultPlayerDatas[0].email,
        startingBetterEmail = defaultPlayerDatas[0].email,
        jassType = JassType.SIDI_BARAHNI,
        bets = listOf(Bet(defaultPlayerDatas[1].email, Trump.UNGER_UFE, BetHeight.HUNDRED)),
        betActions = listOf(Bet.BetAction.BET),
        gameState = GameState(),
    )

    private lateinit var bettingLogic: BettingLogic

    @Before
    fun setUp() {
        bettingLogic = SidiBarahniBettingLogic()
    }

    @Test
    fun ifCurrentPlayerWasLastToBetAndSignalsWantingToStartWithNullBetCurrentPlayerIsReturned() {
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[1].email, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET, Bet.BetAction.PASS, Bet.BetAction.PASS, Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetterEmail = defaultPlayerDatas[1].email,
            currentPlayerBet = null,
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[1].email))
    }

    @Test
    fun ifCurrentPlayerDoesNotSignalStartingThePlayerOnTheRightStarts() {
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetterEmail = defaultPlayerDatas[1].email,
            currentPlayerBet = null,
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[2].email))
    }

    @Test
    fun ifCurrentPlayerDoesNotSignalStartingThePlayerOnTheRightStartsWrapsAroundList() {
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetterEmail = defaultPlayerDatas[3].email,
            currentPlayerBet = Bet(defaultPlayerDatas[3].email, Trump.UNGER_UFE, BetHeight.HUNDRED),
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[0].email))
    }

    @Test
    fun whenCurrentPlayerBidsOverTheirLastBetThePlayerToTheirRightIsNext() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[2].email, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.PASS),
        )

        val result = bettingLogic.nextPlayer(
            currentBetterEmail = defaultPlayerDatas[2].email,
            currentPlayerBet = Bet(defaultPlayerDatas[2].email, Trump.SPADES, BetHeight.HUNDRED_FIFTY),
            bettingState = bettingState,
        )
        assertThat(result, CoreMatchers.`is`(defaultPlayerDatas[3].email))
    }

    @Test
    fun availableActionsReturnsBetAndPassIfNoBetsPlacedPreviously() {
        val bettingLogic = SidiBarahniBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(),
        )

        val result = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[2].email,
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
            bets = listOf(Bet(defaultPlayerDatas[0].email, Trump.UNGER_UFE, BetHeight.MATCH)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[0].email,
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
            bets = listOf(Bet(defaultPlayerDatas[0].email, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[0].email,
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
            bets = listOf(Bet(defaultPlayerDatas[0].email, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[1].email,
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
            bets = listOf(Bet(defaultPlayerDatas[0].email, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(Bet.BetAction.BET),
        )

        val result = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[2].email,
            bettingState = bettingState,
        )
        assertThat(result, Matchers.containsInAnyOrder(
            Bet.BetAction.BET,
            Bet.BetAction.PASS,
        ))
    }
}