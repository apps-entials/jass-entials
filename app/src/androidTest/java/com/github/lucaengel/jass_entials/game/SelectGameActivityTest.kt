package com.github.lucaengel.jass_entials.game

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lucaengel.jass_entials.SignInActivity
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SelectGameActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val selectGameDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), SelectGameActivity::class.java)

    @Before
    fun setup() {
        GameStateHolder.runCpuAsynchronously = false
    }

    @Test
    fun sidiBarraniClickOpensTheSidiBarraniPregameActivity() {
        ActivityScenario.launch<SelectGameActivity>(selectGameDefaultIntent).use {
            Intents.init()

            composeTestRule.onNodeWithText("Sidi Barrani")
                .assertIsDisplayed()
                .performClick()

            Intents.intended(
                hasComponent(PreRoundBettingActivity::class.java.name)
            )

            Intents.release()
        }
    }

    @Test
    fun schieberClickOpensTheSchieberPregameActivity() {
        ActivityScenario.launch<SelectGameActivity>(selectGameDefaultIntent).use {
            Intents.init()

            composeTestRule.onNodeWithText("Schieber")
                .assertIsDisplayed()
                .performClick()

            Intents.intended(
                hasComponent(PreRoundBettingActivity::class.java.name)
            )

            Intents.release()
        }
    }

    @Test
    fun coiffeurClickOpensThePreGameActivity() {
        ActivityScenario.launch<SelectGameActivity>(selectGameDefaultIntent).use {
            Intents.init()

            composeTestRule.onNodeWithText("Coiffeur")
                .assertIsDisplayed()
                .performClick()

            Intents.intended(
                hasComponent(PreRoundBettingActivity::class.java.name)
            )

            Intents.release()
        }
    }

    @Test
    fun backButtonClosesTheActivity() {
        val signInActivityIntent = Intent(ApplicationProvider.getApplicationContext(), SignInActivity::class.java)
        ActivityScenario.launch<SignInActivity>(signInActivityIntent).use {
            Intents.init()

            composeTestRule.onNodeWithText(text = "Continue as guest")
                .assertExists()
                .performClick()

            Intents.intended(
                hasComponent(SelectGameActivity::class.java.name)
            )

            composeTestRule.onNodeWithTag(SelectGameActivity.TestTags.Buttons.BACK)
                .assertIsDisplayed()
                .performClick()

            composeTestRule.onNodeWithText(text = "Continue as guest")
                .assertIsDisplayed()

            Intents.release()
        }
    }
}