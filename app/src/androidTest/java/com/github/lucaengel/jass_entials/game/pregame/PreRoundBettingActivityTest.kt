package com.github.lucaengel.jass_entials.game.pregame

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.JassRoundActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
        jassType = JassType.SIDI_BARAHNI,
        bets = listOf(Bet(defaultPlayerDatas[1].id, Trump.UNGER_UFE, BetHeight.HUNDRED)),
        betActions = listOf(Bet.BetAction.BET),
        gameState = GameState(),
    )

    @Before
    fun setup() {
        GameStateHolder.players = defaultPlayerDatas
        GameStateHolder.bettingState = defaultBettingState
    }

    @Test
    fun inSidiBarahniPlaceBetAndStartGameButtonIsPresentIfCurrentPlayerPlacedLastBetAndItWasNotAMatch() {
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
            Intents.init()

            composeTestRule.onNodeWithText("Place Bet").assertExists()
            composeTestRule.onNodeWithText("Start Game")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))

            Intents.release()
        }
    }

    @Test
    fun inSidiBarahniOnlyStartGameButtonIsPresentIfTheyAreTheLastBetterAndBidMatch() {
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
            Intents.init()

            composeTestRule.onNodeWithText("Place Bet")
                .assertDoesNotExist() // since cannot bet any higher
            composeTestRule.onNodeWithText("Start Game")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))

            Intents.release()
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
            Intents.init()

            composeTestRule.onNodeWithContentDescription("Bet placing dropdown icon")
                .assertExists("trump dropdown not found")
                .performClick()

            composeTestRule.onNodeWithTag(Trump.HEARTS.toString(), useUnmergedTree = true)
                .assertExists("trump dropdown item not found")
                .performClick()

            composeTestRule.onNodeWithText("Start Game")
                .assertExists()
                .performClick()

            Intents.intended(IntentMatchers.hasComponent(
                JassRoundActivity::class.java.name,
            ))

            Intents.release()
        }
    }
}