package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.game_state.Score
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
        jassType = JassType.SIDI_BARAHNI,
        roundState = RoundState.initial(Trump.CLUBS, defaultPlayerDatas[0].id),
        winningBet = Bet(),
        playerCards = defaultPlayerDatas.associate { it.id to it.cards },
        score = Score.INITIAL,
    )

    private val defaultBettingState = BettingState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentBetterId= defaultPlayerDatas[0].id,
        startingBetterId= defaultPlayerDatas[0].id,
        jassType = JassType.SIDI_BARAHNI,
        bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
        betActions = listOf(Bet.BetAction.BET),
        gameState = GameState(),
        score = Score.INITIAL,
    )

    @Before
    fun setUp() {
        GameStateHolder.players = defaultPlayerDatas
        GameStateHolder.gameState = defaultGameState
        GameStateHolder.bettingState = defaultBettingState
    }

    @Test
    fun playCardPlaysOneOfTheHandCardsAndRemovesItFromTheHand() {
        val player = CpuPlayer(defaultPlayerDatas[0].id, 0)
        val playerData = GameStateHolder.players.first { it.id == player.playerId }
        val oldCards = playerData.cards
        val card = player.cardToPlay(defaultGameState.roundState, playerData.cards).join()

        assertTrue(oldCards.contains(card))
        // TODO: maybe also update the player in this method and return it in a pair?
    }

    @Test
    fun cpuPlayerOnceInAWhileSelectsABet() {
        val player = CpuPlayer(defaultPlayerDatas[0].id)
        val bettingState = defaultBettingState.copy(
            bets = listOf(Bet(defaultPlayerDatas[3].id, Trump.UNGER_UFE, BetHeight.FIFTY)),
        )

        for (i in 0..100) {
            val newBettingState = player.bet(
                bettingState = bettingState,
                handCards = defaultPlayerDatas[0].cards
            ).join()

            assertThat(newBettingState.currentBetterId,
                `is`(bettingState.currentBetterId.nextPlayer()))
        }
    }

    @Test // i.e., new betting state has the same current better id as before to signal this
    fun whenPlayerCanOnlyStartTheyStart() {
        val player = CpuPlayer(defaultPlayerDatas[0].id, 0)

        // since last bet was match by player 0, player 0 can only start
        val newBettingState = player.bet(
            bettingState = defaultBettingState.copy(
                bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.MATCH)),
                betActions = listOf(Bet.BetAction.BET, Bet.BetAction.PASS, Bet.BetAction.PASS, Bet.BetAction.PASS),
            ),
            handCards = defaultPlayerDatas[0].cards
        ).join()

        assertThat(newBettingState.currentBetterId,
            `is`(defaultBettingState.currentBetterId))
    }
}