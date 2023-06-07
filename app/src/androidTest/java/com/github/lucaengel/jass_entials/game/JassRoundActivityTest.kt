package com.github.lucaengel.jass_entials.game

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
            composeTestRule.onNodeWithText("Jass Round Activity!")
                .assertExists()
        }
    }
}