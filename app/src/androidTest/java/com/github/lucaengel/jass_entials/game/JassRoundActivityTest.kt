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
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JassRoundActivityTest {


    private val defaultJassRoundIntent = Intent(ApplicationProvider.getApplicationContext(), JassRoundActivity::class.java)

    @get:Rule
    val composeTestRule = createEmptyComposeRule()


    private val shuffledDeck = Deck.STANDARD_DECK.shuffled()
    // every player gets 8 cards, 4 are already in the current trick
    private val playerData1 = PlayerData("email_1", 0, "first_1", "second_1", Deck.sortPlayerCards(shuffledDeck.cards.subList(4, 12)), 0, "123")
    private val playerData2 = PlayerData("email_2", 1, "first_2", "second_2", Deck.sortPlayerCards(shuffledDeck.cards.subList(12, 20)), 0, "123")
    private val playerData3 = PlayerData("email_3", 2, "first_3", "second_3", Deck.sortPlayerCards(shuffledDeck.cards.subList(20, 28)), 0, "123")
    private val playerData4 = PlayerData("email_4", 3, "first_4", "second_4", Deck.sortPlayerCards(shuffledDeck.cards.subList(28, 36)), 0, "123")
    private val players = listOf(playerData1, playerData2, playerData3, playerData4)


    private var gameState: GameState = GameState(
        0,
        players,
        playerData1,
        playerData1,
        1,
        Trick(shuffledDeck.cards.subList(0, 4).mapIndexed { index, card -> Pair(card, players[index]) }),
        listOf(),
        1,
        Trump.UNGER_UFE,
        Deck.STANDARD_DECK.dealCards(players),
    )

    private var bettingState: BettingState =
        BettingState(
            0,
            listOf(playerData1, playerData2, playerData3, playerData4),
            playerData1,
            JassType.SIDI_BARAHNI,
            listOf(
                Bet(playerData2, Trump.CLUBS, BetHeight.FORTY)
            ),
            GameState()
        )

    @Before
    fun setup() {
        GameStateHolder.gameState = gameState
        GameStateHolder.bettingState = bettingState
    }

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