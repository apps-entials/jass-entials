package com.github.lucaengel.jass_entials.game.betting

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test

class SchieberBettingLogicTest {

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
    )

    @Test
    fun ifCurrentPlayerPlacedABetCurrentPlayerIsReturnedToSignalThatTheBettingIsOver() {
        val bettingLogic = SchieberBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(),
        )

        val result = bettingLogic.nextPlayer(
            currentBetter = defaultPlayerDatas[1].id,
            currentPlayerBet = Bet(defaultPlayerDatas[1].id, Trump.UNGER_UFE, BetHeight.HUNDRED),
            bettingState = bettingState,
            )
        assertThat(result, `is`(defaultPlayerDatas[1].id))
    }

    @Test // team partner sits in the after next position
    fun ifCurrentPlayerPassesTheTeamPartnerIsReturned() {
        val bettingLogic = SchieberBettingLogic()
        val result = bettingLogic.nextPlayer(
            currentBetter = defaultPlayerDatas[1].id,
            currentPlayerBet = null,
            bettingState = defaultBettingState,
            )
        assertThat(result, `is`(defaultPlayerDatas[3].id))
    }

    @Test
    fun ifCurrentPlayerPassesTheTeamPartnerIsReturnedAlsoWorksWithListOverflow() {
        val bettingLogic = SchieberBettingLogic()
        val result = bettingLogic.nextPlayer(
            currentBetter = defaultPlayerDatas[3].id,
            currentPlayerBet = null,
            bettingState = defaultBettingState,
        )
        assertThat(result, `is`(defaultPlayerDatas[1].id))
    }

    @Test
    fun availableActionsReturnsOnlyBetIfBothTeamMembersInitiallyPassed() {
        val bettingLogic = SchieberBettingLogic()
        val bettingState = defaultBettingState.copy(
            bets = listOf(),
            betActions = listOf(Bet.BetAction.PASS, Bet.BetAction.PASS),
        )
        val result = bettingLogic.availableActions(
            currentBetter = defaultPlayerDatas[1].id,
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
            currentBetter = defaultPlayerDatas[1].id,
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
            currentBetter = defaultPlayerDatas[1].id,
            bettingState = onePassBettingState,
        )

        assertThat(onePassResult, Matchers.containsInAnyOrder(
            Bet.BetAction.BET,
            Bet.BetAction.PASS
        ))
    }
}