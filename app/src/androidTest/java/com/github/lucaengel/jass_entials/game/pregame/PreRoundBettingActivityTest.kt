package com.github.lucaengel.jass_entials.game.pregame

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.JassRoundActivity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class PreRoundBettingActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val preRoundDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), PreRoundBettingActivity::class.java)

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
        jassType = JassType.SIDI_BARRANI,
        bets = listOf(Bet(defaultPlayerDatas[1].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
        betActions = listOf(Bet.BetAction.BET),
        gameState = GameState(),
        score = Score.INITIAL,
    )

    @Before
    fun setup() {
        GameStateHolder.runCpuAsynchronously = false
        GameStateHolder.players = defaultPlayerDatas
        GameStateHolder.bettingState = defaultBettingState
        GameStateHolder.prevTrumpsByTeam = mapOf()

        Intents.init()
    }

    @After
    fun tearDown() {
        sleep(1000)

        Intents.release()
    }

    @Test
    fun inSidiBarraniPlaceBetAndStartGameButtonIsPresentIfCurrentPlayerPlacedLastBetAndItWasNotAMatch() {
        GameStateHolder.bettingState = defaultBettingState.copy(
            currentBetterId = defaultPlayerDatas[0].id,
            bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(
                Bet.BetAction.BET,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS
            ),
        )

        ActivityScenario.launch<PreRoundBettingActivity>(preRoundDefaultIntent).use {

            composeTestRule.onNodeWithText("Place Bet").assertExists()
            composeTestRule.onNodeWithText("Start Game")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))
        }
    }

    @Test
    fun inSidiBarraniOnlyStartGameButtonIsPresentIfTheyAreTheLastBetterAndBidMatch() {
        GameStateHolder.bettingState = defaultBettingState.copy(
            currentBetterId = defaultPlayerDatas[0].id,
            bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.MATCH)),
            betActions = listOf(
                Bet.BetAction.BET,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS
            ),
        )

        ActivityScenario.launch<PreRoundBettingActivity>(preRoundDefaultIntent).use {

            composeTestRule.onNodeWithText("Place Bet")
                .assertDoesNotExist() // since cannot bet any higher, can only start the game
            composeTestRule.onNodeWithText("Start Game")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))
        }
    }

    @Test
    fun heartsBettingSimulationSchieber() {
        GameStateHolder.bettingState = defaultBettingState.copy(
            currentBetterId = defaultPlayerDatas[0].id,
            jassType = JassType.SCHIEBER,
            bets = listOf(Bet(defaultPlayerDatas[0].id, Trump.UNGER_UFE, BetHeight.MATCH)),
            betActions = listOf(
                Bet.BetAction.BET,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS
            ),
        )

        ActivityScenario.launch<PreRoundBettingActivity>(preRoundDefaultIntent).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

            composeTestRule.onNodeWithContentDescription("Bet placing dropdown icon", useUnmergedTree = true)
                .assertExists("trump dropdown not found")
                .performClick()

            device.waitForIdle()

            composeTestRule.onNodeWithContentDescription(label = Trump.HEARTS.toString(), useUnmergedTree = true)
                .assertExists("trump dropdown item not found")
                .performClick()

            device.waitForIdle()

            composeTestRule.onNodeWithText("Start Game")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))
        }
    }

    @Test
    fun heartsBettingSimulationCoiffeur() {
        GameStateHolder.bettingState = defaultBettingState.copy(
            currentBetterId = defaultPlayerDatas[0].id,
            jassType = JassType.COIFFEUR,
            bets = listOf(),
            betActions = listOf(
                Bet.BetAction.PASS,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS
            ),
        )

        ActivityScenario.launch<PreRoundBettingActivity>(preRoundDefaultIntent).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

            composeTestRule.onNodeWithContentDescription("Bet placing dropdown icon", useUnmergedTree = true)
                .assertExists("trump dropdown not found")
                .performClick()

            device.waitForIdle()

            composeTestRule.onNodeWithContentDescription(label = Trump.HEARTS.toString(), useUnmergedTree = true)
                .assertExists("trump not found")
                .performClick()

            device.waitForIdle()

            composeTestRule.onNodeWithText("Start Game")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))
        }
    }

    @Test
    fun doublingInSidiBarraniSetsTheDoubledFlagInTheBetAndStartsTheGame() {
        GameStateHolder.bettingState = defaultBettingState.copy(
            currentBetterId = defaultPlayerDatas[0].id,
            jassType = JassType.SIDI_BARRANI,
            bets = listOf(Bet(defaultPlayerDatas[3].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
            betActions = listOf(
                Bet.BetAction.PASS,
                Bet.BetAction.PASS,
                Bet.BetAction.PASS,
                Bet.BetAction.BET
            ),
        )

        ActivityScenario.launch<PreRoundBettingActivity>(preRoundDefaultIntent).use {

            composeTestRule.onNodeWithText("Double")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))

            composeTestRule.onNodeWithText("(doubled by ", substring = true)
                .assertExists()

            assertThat(GameStateHolder.gameState.winningBet.doubledBy, `is`(defaultPlayerDatas[0].id))
            assertThat(GameStateHolder.gameState.roundState.trick().trump, `is`(Trump.UNGER_UFE))
            assertThat(GameStateHolder.gameState.roundState.trick().startingPlayerId, `is`(defaultPlayerDatas[3].id))
        }
    }
}