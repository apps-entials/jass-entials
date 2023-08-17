package com.github.lucaengel.jass_entials.game

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lucaengel.jass_entials.data.cards.CardType
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val settingsActivityDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)

    @Test
    fun settingsActivityAllowsCardTypeSwitching() {
        GameStateHolder.cardType = CardType.FRENCH

        ActivityScenario.launch<SettingsActivity>(settingsActivityDefaultIntent).use {
            Intents.init()

            composeTestRule.onNodeWithText(CardType.GERMAN.toString())
                .assertIsDisplayed()
                .performClick()

            assertThat(GameStateHolder.cardType, `is`(CardType.GERMAN))

            composeTestRule.onNodeWithText(CardType.FRENCH.toString())
                .assertIsDisplayed()
                .performClick()

            assertThat(GameStateHolder.cardType, `is`(CardType.FRENCH))

            Intents.release()
        }
    }

    @Test
    fun backButtonClosesActivity() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SelectGameActivity::class.java)
        ActivityScenario.launch<SelectGameActivity>(intent).use {
            Intents.init()

            composeTestRule.onNodeWithContentDescription("Settings")
                .assertExists()
                .performClick()

            Intents.intended(
                IntentMatchers.hasComponent(SettingsActivity::class.java.name)
            )

            composeTestRule.onNodeWithContentDescription("Settings")
                .assertDoesNotExist()

            composeTestRule.onNodeWithContentDescription("Back")
                .assertExists()
                .performClick()

            composeTestRule.onNodeWithContentDescription("Settings")
                .assertExists()

            Intents.release()
        }
    }
}