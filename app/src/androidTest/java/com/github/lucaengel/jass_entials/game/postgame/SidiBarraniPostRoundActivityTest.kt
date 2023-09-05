package com.github.lucaengel.jass_entials.game.postgame

import android.content.Intent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
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
class SidiBarraniPostRoundActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val postRoundDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), SidiBarraniPostRoundActivity::class.java)

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
        GameStateHolder.gameState = gameState
        Intents.init()
    }

    @After
    fun tearDown() {
        sleep(1000)
        Intents.release()
    }

    @Test
    fun endScreenIsShownWhenRoundIsDoneForSidi() {
        GameStateHolder.pointLimits += JassType.SIDI_BARRANI to 500

        GameStateHolder.gameState = gameState.copy(
            roundState = gameState.roundState.copy(
                score = Score.INITIAL
                    .withPointsAdded(TeamId.TEAM_1, 600)
                    .withPointsAdded(TeamId.TEAM_2, 0)
                    .nextRound()
            ),
            jassType = JassType.SIDI_BARRANI
        )

        ActivityScenario.launch<SidiBarraniPostRoundActivity>(postRoundDefaultIntent).use {

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

    @Test
    fun correctInitialScreenContent() {
        GameStateHolder.gameState = gameState.copy(
            winningBet = Bet(playerId = PlayerId.PLAYER_1, trump = Trump.CLUBS, bet = BetHeight.HUNDRED_THIRTY, doubledBy = PlayerId.PLAYER_2),
            roundState = gameState.roundState.copy(
                score = Score.INITIAL
                    .withPointsAdded(TeamId.TEAM_1, 210)
                    .withPointsAdded(TeamId.TEAM_2, 47)
                    .withPointsAdded(TeamId.TEAM_1, 100)
                    .withPointsAdded(TeamId.TEAM_2, 57)
            ),
        )

        ActivityScenario.launch<SidiBarraniPostRoundActivity>(postRoundDefaultIntent).use {
            for (trump in Trump.values()) {
                composeTestRule.onNodeWithContentDescription(trump.toString(), useUnmergedTree = true)
                    .assertExists()
            }

            // winning bet:
            composeTestRule.onNodeWithContentDescription("${Trump.HEARTS}", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onAllNodesWithText("130", useUnmergedTree = true)
                .assertCountEquals(2) // winning bet and betting points
            composeTestRule.onAllNodesWithText("---", useUnmergedTree = true)
                .assertCountEquals(2) // winning bet and doubled by


            // doubled by:
            composeTestRule.onNodeWithText("first_2", useUnmergedTree = true, substring = true)
                .assertExists()

            // played points:
            composeTestRule.onAllNodesWithText("100", useUnmergedTree = true)
                .assertCountEquals(2)// played points and round points
            composeTestRule.onNodeWithText("57", useUnmergedTree = true)
                .assertExists()

            // betting points:
            composeTestRule.onNodeWithText("260", useUnmergedTree = true)
                .assertExists()

            // round points:
            composeTestRule.onNodeWithText("317", useUnmergedTree = true)
                .assertExists()

            // previous points:
            composeTestRule.onNodeWithText("210", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onNodeWithText("47", useUnmergedTree = true)

            // total points:
            composeTestRule.onNodeWithText("310", useUnmergedTree = true)
                .assertExists()
            composeTestRule.onNodeWithText("364", useUnmergedTree = true)
                .assertExists()


            composeTestRule.onNodeWithText("Start next round", useUnmergedTree = true)
                .assertExists()
                .performClick()

            Intents.intended(
                IntentMatchers.hasComponent(PreRoundBettingActivity::class.java.name)
            )
        }
    }
}