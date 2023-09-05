package com.github.lucaengel.jass_entials.game.postgame

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
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
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.game_state.TeamId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.SelectGameActivity
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class SchieberPostRoundActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val postRoundDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), SchieberPostRoundActivity::class.java)

    private val shuffledDeck = Deck.STANDARD_DECK.shuffled()
    // every player gets 8 cards, 4 are already in the current trick
    private val playerData1 = PlayerData(
        PlayerId.PLAYER_1,
        "first_1",
        "second_1",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(4, 12)),
        0,
        "123"
    )

    private var gameState: GameState = GameState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentPlayerId= playerData1.id,
        startingPlayerId= playerData1.id,
        currentRound = 1,
        jassType = JassType.SIDI_BARRANI,
        roundState = RoundState.initial(trump = Trump.CLUBS, startingPlayerId = playerData1.id),
        winningBet = Bet(),
        playerCards = Deck.STANDARD_DECK.dealCards(),
    )

    @Before
    fun setup() {
        GameStateHolder.runCpuAsynchronously = false
        GameStateHolder.gameState = gameState

        Intents.init()
    }

    @After
    fun tearDown() {
        sleep(1000)

        Intents.release()
    }

    @Test
    fun correctInitialScreenContent() {
        GameStateHolder.gameState = gameState.copy(
            winningBet = Bet(
                PlayerId.PLAYER_1,
                Trump.CLUBS,
                BetHeight.MATCH
            ),
            roundState = gameState.roundState.copy(
                score = Score.INITIAL
                    .withPointsAdded(TeamId.TEAM_1, 100)
                    .withPointsAdded(TeamId.TEAM_2, 57)
                    .nextRound()
                    .withPointsAdded(TeamId.TEAM_1, 90)
                    .withPointsAdded(TeamId.TEAM_2, 67)
            )
        )
        ActivityScenario.launch<SchieberPostRoundActivity>(postRoundDefaultIntent).use {
            composeTestRule.onNodeWithText("Trump Bet", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("Played Points", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("Previous Points", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("Total Points", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("${gameState.currentUserId.teamId()}", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("${gameState.currentUserId.teamId().otherTeam()}", useUnmergedTree = true).assertExists()

            // played points
            composeTestRule.onNodeWithText("90", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("67", useUnmergedTree = true).assertExists()

            // previous points
            composeTestRule.onNodeWithText("100", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("57", useUnmergedTree = true).assertExists()

            // total points
            composeTestRule.onNodeWithText("190", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithText("124", useUnmergedTree = true).assertExists()
        }
    }

    @Test
    fun startNextRoundButtonOpensNextSidiBarraniBiddingActivity() {
        ActivityScenario.launch<SchieberPostRoundActivity>(postRoundDefaultIntent).use {

            composeTestRule.onNodeWithText("Start next round")
                .assertExists()
                .performClick()
            Intents.intended(
                IntentMatchers.hasComponent(PreRoundBettingActivity::class.java.name))

        }
    }

    @Test
    fun endScreenIsShownWhenRoundIsDoneForSchieber() {
        GameStateHolder.pointLimits += JassType.SCHIEBER to 500

        GameStateHolder.gameState = gameState.copy(
            roundState = gameState.roundState.copy(
                score = Score.INITIAL
                    .withPointsAdded(TeamId.TEAM_1, 1000)
                    .withPointsAdded(TeamId.TEAM_2, 0)
                    .nextRound()
            )
        )

        ActivityScenario.launch<SchieberPostRoundActivity>(postRoundDefaultIntent).use {

            composeTestRule.onNodeWithText("${TeamId.TEAM_1} won the game!", substring = true)
                .assertExists()

            composeTestRule.onNodeWithText("Choose a new Jass game")
                .assertExists()
                .performClick()

            Intents.intended(
                IntentMatchers.hasComponent(SelectGameActivity::class.java.name)
            )
        }
    }
}