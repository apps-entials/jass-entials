package com.github.lucaengel.jass_entials.game.postgame

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class CoiffeurPostRoundActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val defaultIntent = Intent(ApplicationProvider.getApplicationContext(), CoiffeurPostRoundActivity::class.java)


    @Before
    fun setUp() {
        GameStateHolder.prevRoundScores = listOf()
    }

    @After
    fun tearDown() {
        sleep(1000)
    }

    @Test
    fun correctInitialScreenContent() {
        GameStateHolder.prevRoundScores = listOf(
            Pair(Bet(PlayerId.PLAYER_1, Trump.CLUBS, BetHeight.MATCH), Score(100, 57, 100, 57)),
            Pair(Bet(PlayerId.PLAYER_3, Trump.UNGER_UFE, BetHeight.MATCH), Score(110, 47, 110, 47)),

            Pair(Bet(PlayerId.PLAYER_2, Trump.DIAMONDS, BetHeight.MATCH), Score(77, 80, 77, 80)),
            Pair(Bet(PlayerId.PLAYER_4, Trump.HEARTS, BetHeight.MATCH), Score(72, 85, 72, 85)),
        )

        ActivityScenario.launch<PostRoundActivity>(defaultIntent).use {
            Intents.init()

            for (trump in Trump.values()) {
                composeTestRule.onNodeWithContentDescription(trump.toString(), useUnmergedTree = true)
                    .assertExists()
            }

            composeTestRule.onNodeWithText("100", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onNodeWithText("110", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onNodeWithText("80", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onNodeWithText("85", useUnmergedTree = true)
                .assertExists()

            // total score:
            composeTestRule.onNodeWithText("210", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onNodeWithText("165", useUnmergedTree = true)
                .assertExists()


            composeTestRule.onNodeWithText("Start next round", useUnmergedTree = true)
                .assertExists()

            Intents.release()
        }
    }

    @Test
    fun whenGameIsOverUserCanGoToSelectGameActivity() {
        var pointsTeam1 = 0
        var pointsTeam2: Int
        var gamePointsTeam1 = 0
        var gamePointsTeam2 = 0
        GameStateHolder.prevRoundScores = Trump.values().map {trump ->
            listOf(
                Pair(Bet(PlayerId.PLAYER_1, trump, BetHeight.MATCH), Score(
                    pointsTeam1.let { pointsTeam1 =  100 + trump.ordinal; gamePointsTeam1 += pointsTeam1; pointsTeam2 = 157 - pointsTeam1; gamePointsTeam2 += pointsTeam2; pointsTeam1},
                    pointsTeam2,
                    gamePointsTeam1,
                    gamePointsTeam2)),
                Pair(Bet(PlayerId.PLAYER_2, trump, BetHeight.MATCH), Score(
                    pointsTeam2.let { pointsTeam2 =  80 + trump.ordinal; gamePointsTeam2 += pointsTeam2; pointsTeam1 = 157 - pointsTeam2; gamePointsTeam1 += pointsTeam1; pointsTeam1},
                    pointsTeam2,
                    gamePointsTeam1,
                    gamePointsTeam2)),
            )
        }.flatten()

        GameStateHolder.gameState = GameStateHolder.gameState.copy(
            roundState = GameStateHolder.gameState.roundState.copy(
                score = GameStateHolder.prevRoundScores.last().second
            )
        )

        ActivityScenario.launch<PostRoundActivity>(defaultIntent).use {
            Intents.init()

            composeTestRule.onNodeWithText("The game is over, ${PlayerId.PLAYER_1.teamId()} won!", useUnmergedTree = true)
                .assertExists()

            // Total score
            composeTestRule.onNodeWithText("${6*100 + 1+2+3+4+5}", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onNodeWithText("${6*80 + 1+2+3+4+5}", useUnmergedTree = true)
                .assertExists()

            composeTestRule.onNodeWithText("Choose a new Jass game", useUnmergedTree = true)
                .assertExists()
                .performClick()

            Intents.release()
        }
    }
}