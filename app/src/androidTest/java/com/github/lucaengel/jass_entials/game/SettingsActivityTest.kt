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
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.lucaengel.jass_entials.data.cards.CardType
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.JassType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val settingsActivityDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), SettingsActivity::class.java)

    @Before
    fun setup() {
        GameStateHolder.runCpuAsynchronously = false
    }

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

    @Test
    fun settingAPointLimitForSchieberWorks() {
        GameStateHolder.pointLimits += JassType.SCHIEBER to 500
        ActivityScenario.launch<SettingsActivity>(settingsActivityDefaultIntent).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            assertThat(GameStateHolder.pointLimits[JassType.SCHIEBER], `is`(500))

            composeTestRule.onNodeWithText("Schieber: 500", substring = true)
                .assertIsDisplayed()

            composeTestRule.onNodeWithContentDescription("dropdown${JassType.SCHIEBER}", useUnmergedTree = true)
                .assertIsDisplayed()
                .performClick()

            device.waitForIdle()

            composeTestRule.onNodeWithText("2000")
                .assertIsDisplayed()
                .performClick()

            device.waitForIdle()

            assertThat(GameStateHolder.pointLimits[JassType.SCHIEBER], `is`(2000))

            composeTestRule.onNodeWithText("Schieber: 2000", substring = true)
                .assertIsDisplayed()
        }
    }

    @Test
    fun settingAPointLimitForSidiBarraniWorks() {
        GameStateHolder.pointLimits += JassType.SIDI_BARRANI to 1500
        ActivityScenario.launch<SettingsActivity>(settingsActivityDefaultIntent).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

            composeTestRule.onNodeWithText("Sidi Barrani: 1500", substring = true)
                .assertIsDisplayed()

            composeTestRule.onNodeWithContentDescription("dropdown${JassType.SIDI_BARRANI}", useUnmergedTree = true)
                .assertIsDisplayed()
                .performClick()

            device.waitForIdle()

            composeTestRule.onNodeWithText("2500")
                .assertIsDisplayed()
                .performClick()

            device.waitForIdle()

            assertThat(GameStateHolder.pointLimits[JassType.SIDI_BARRANI], `is`(2500))

            composeTestRule.onNodeWithText("Sidi Barrani: 2500", substring = true)
                .assertIsDisplayed()
        }
    }
}