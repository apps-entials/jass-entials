package com.github.lucaengel.jass_entials.game

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JassRoundActivityTest {


    private val defaultJassRoundIntent = Intent(ApplicationProvider.getApplicationContext(), JassRoundActivity::class.java)

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun correctInitialScreenContent() {
        ActivityScenario.launch<JassRoundActivity>(defaultJassRoundIntent).use {
            composeTestRule.onNodeWithText("first_2 second_2").assertExists()
            composeTestRule.onNodeWithText("first_3 second_3").assertExists()
            composeTestRule.onNodeWithText("first_4 second_4").assertExists()

            // current player's cards
            val currentPlayer = GameStateHolder.gameState.playerDatas[GameStateHolder.gameState.currentPlayerIdx]
            currentPlayer.cards.forEach {
                composeTestRule.onNodeWithContentDescription(it.toString()).assertExists()
            }

            val trickCards = GameStateHolder.gameState.currentTrick.playerToCard.map { it.first }
            trickCards.forEach {
                composeTestRule.onNodeWithContentDescription(it.toString()).assertExists()
            }
        }
    }

    @Test
    fun playingACardRemovesItFromThePlayersHand() {
        ActivityScenario.launch<JassRoundActivity>(defaultJassRoundIntent).use {
            val currentPlayer =
                GameStateHolder.gameState.playerDatas[GameStateHolder.gameState.currentPlayerIdx]
            val cardToPlay = currentPlayer.cards[0]

            assertThat(GameStateHolder.gameState.currentPlayerData, `is`(currentPlayer))
            composeTestRule.onNodeWithContentDescription(cardToPlay.toString())
                .assertExists()
                .assertIsDisplayed()
                .performClick() // remove the full trick
                .performClick() // play the card

            assertThat(GameStateHolder.gameState.playerDatas[GameStateHolder.gameState.currentPlayerIdx].cards.contains(cardToPlay), `is`(false))
            assertThat(GameStateHolder.gameState.currentTrick.playerToCard.map { it.first }.contains(cardToPlay), `is`(true))
        }
    }
}