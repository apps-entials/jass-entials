package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test

class SchieberBettingLogicTest {

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

    @Test
    fun ifCurrentPlayerPlacedABetCurrentPlayerIsReturnedToSignalThatTheBettingIsOver() {
        val bettingLogic = SchieberBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(),
        )

        val result = bettingLogic.nextPlayer(
            currentBetterEmail = defaultPlayerDatas[1].email,
            currentPlayerBet = Bet(defaultPlayerDatas[1].email, Trump.UNGER_UFE, BetHeight.HUNDRED),
            bettingState = bettingState,
            )
        assertThat(result, `is`(defaultPlayerDatas[1].email))
    }

    @Test // team partner sits in the after next position
    fun ifCurrentPlayerPassesTheTeamPartnerIsReturned() {
        val bettingLogic = SchieberBettingLogic()
        val result = bettingLogic.nextPlayer(
            currentBetterEmail = defaultPlayerDatas[1].email,
            currentPlayerBet = null,
            bettingState = defaultBettingState,
            )
        assertThat(result, `is`(defaultPlayerDatas[3].email))
    }

    @Test
    fun ifCurrentPlayerPassesTheTeamPartnerIsReturnedAlsoWorksWithListOverflow() {
        val bettingLogic = SchieberBettingLogic()
        val result = bettingLogic.nextPlayer(
            currentBetterEmail = defaultPlayerDatas[3].email,
            currentPlayerBet = null,
            bettingState = defaultBettingState,
        )
        assertThat(result, `is`(defaultPlayerDatas[1].email))
    }

    @Test
    fun availableActionsReturnsOnlyBetIfBothTeamMembersInitiallyPassed() {
        val bettingLogic = SchieberBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(Bet.BetAction.PASS, Bet.BetAction.PASS),
        )
        val result = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[1].email,
            bettingState = bettingState,
        )
        assertThat(result, Matchers.containsInAnyOrder(Bet.BetAction.BET))
    }

    @Test
    fun availableActionsBetAndPassIfNotBothMembersHavePassed() {
        val bettingLogic = SchieberBettingLogic()
        val noPassBettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(),
        )
        val noPassResult = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[1].email,
            bettingState = noPassBettingState,
        )

        assertThat(noPassResult, Matchers.containsInAnyOrder(
            Bet.BetAction.BET,
            Bet.BetAction.PASS
        ))

        val onePassBettingState = defaultBettingState.copy(
            betActions = listOf(Bet.BetAction.PASS),
        )

        val onePassResult = bettingLogic.availableActions(
            currentBetterEmail = defaultPlayerDatas[1].email,
            bettingState = onePassBettingState,
        )

        assertThat(onePassResult, Matchers.containsInAnyOrder(
            Bet.BetAction.BET,
            Bet.BetAction.PASS
        ))
    }
}