package com.github.lucaengel.jass_entials.game.pregame

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.lucaengel.jass_entials.game.SelectGameActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SelectGameActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val selectGameDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), SelectGameActivity::class.java)

    @Test
    fun sidiBarahniClickOpensTheSidiBarahniPregameActivity() {
        ActivityScenario.launch<SelectGameActivity>(selectGameDefaultIntent).use {
            Intents.init()
            composeTestRule.onNodeWithText("Sidi Barahni")
                .assertIsDisplayed()
                .performClick()

            Intents.intended(
                hasComponent(SidiBarahniPregameActivity::class.java.name)
            )

            Intents.release()
        }
    }
}