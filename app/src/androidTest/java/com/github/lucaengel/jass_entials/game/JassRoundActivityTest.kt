package com.github.lucaengel.jass_entials.game

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

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

            sleep(3000)
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
}