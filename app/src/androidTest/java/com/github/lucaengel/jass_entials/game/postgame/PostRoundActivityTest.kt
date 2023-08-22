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
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.game_state.TeamId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostRoundActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val postRoundDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), PostRoundActivity::class.java)

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
//    private val playerData2 = PlayerData(
//        PlayerId.PLAYER_2,
//        "first_2",
//        "second_2",
//        Deck.sortPlayerCards(shuffledDeck.cards.subList(12, 20)),
//        0,
//        "123"
//    )
//    private val playerData3 = PlayerData(
//        PlayerId.PLAYER_3,
//        "first_3",
//        "second_3",
//        Deck.sortPlayerCards(shuffledDeck.cards.subList(20, 28)),
//        0,
//        "123"
//    )
//    private val playerData4 = PlayerData(
//        PlayerId.PLAYER_4,
//        "first_4",
//        "second_4",
//        Deck.sortPlayerCards(shuffledDeck.cards.subList(28, 36)),
//        0,
//        "123"
//    )


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
    }

    @Test
    fun correctInitialScreenContent() {
        ActivityScenario.launch<PostRoundActivity>(postRoundDefaultIntent).use {
            composeTestRule.onNodeWithText("This round", substring = true).assertExists()
            composeTestRule.onNodeWithText("Total points", substring = true).assertExists()
        }
    }

    @Test
    fun correctInitialScreenContentForCoiffeur() {
        GameStateHolder.gameState = gameState.copy(jassType = JassType.COIFFEUR)

        ActivityScenario.launch<PostRoundActivity>(postRoundDefaultIntent).use {
            composeTestRule.onNodeWithText("This round", substring = true).assertExists()
            composeTestRule.onNodeWithText("Total points", substring = true).assertExists()
        }

        // TODO: complete this test once Coiffeur post round screen is implemented
    }

    @Test
    fun correctPointsAttributedToEachTeam() {
        GameStateHolder.gameState = gameState.copy(
            roundState = gameState.roundState.copy(
                score = Score.INITIAL
                    .withPointsAdded(TeamId.TEAM_1, 100)
                    .withPointsAdded(TeamId.TEAM_2, 200)
                    .nextRound()
            )
        )

        ActivityScenario.launch<PostRoundActivity>(postRoundDefaultIntent).use {

            composeTestRule.onNodeWithText("Your Team: 0").assertExists()
            composeTestRule.onNodeWithText("Other Team: 0").assertExists()
            composeTestRule.onNodeWithText("Your Team: 100").assertExists()
            composeTestRule.onNodeWithText("Other Team: 200").assertExists()
        }
    }

    @Test
    fun startNextRoundButtonOpensNextSidiBarraniBiddingActivity() {
        ActivityScenario.launch<PostRoundActivity>(postRoundDefaultIntent).use {
            Intents.init()

            composeTestRule.onNodeWithText("Start next round")
                .assertExists()
                .performClick()
            Intents.intended(
                IntentMatchers.hasComponent(PreRoundBettingActivity::class.java.name))

            Intents.release()
        }
    }
}