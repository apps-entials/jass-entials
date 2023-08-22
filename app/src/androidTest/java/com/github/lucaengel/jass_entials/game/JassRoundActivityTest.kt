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
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.game_state.Score
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


    private val shuffledDeck = Deck(Deck.STANDARD_DECK.cards.subList(4, 36)).shuffled()
    // every player gets 8 cards, 4 are already in the current trick
    private val playerData1 = PlayerData(
        PlayerId.PLAYER_1,
        "first_1",
        "second_1",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(0, 8)),
        0,
        "123"
    )
    private val playerData2 = PlayerData(
        PlayerId.PLAYER_2,
        "first_2",
        "second_2",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(8, 16)),
        0,
        "123"
    )
    private val playerData3 = PlayerData(
        PlayerId.PLAYER_3,
        "first_3",
        "second_3",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(16, 24)),
        0,
        "123"
    )
    private val playerData4 = PlayerData(
        PlayerId.PLAYER_4,
        "first_4",
        "second_4",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(24, 32)),
        0,
        "123"
    )
    private val players = listOf(playerData1, playerData2, playerData3, playerData4)


    private var gameState: GameState = GameState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentPlayerId = playerData1.id,
        startingPlayerId = playerData1.id,
        currentRound = 1,
        jassType = JassType.SIDI_BARAHNI,
        roundState = RoundState.initial(trump = Trump.UNGER_UFE, startingPlayerId = playerData1.id)
            .withCardPlayed(Deck.STANDARD_DECK.cards[0])
            .withCardPlayed(Deck.STANDARD_DECK.cards[1])
            .withCardPlayed(Deck.STANDARD_DECK.cards[2])
            .withCardPlayed(Deck.STANDARD_DECK.cards[3]),
        winningBet = Bet(playerData1.id, Trump.UNGER_UFE, BetHeight.SIXTY),
        playerCards = Deck.STANDARD_DECK.dealCards(),
    )

    private var bettingState: BettingState =
        BettingState(
            playerData1.id,
            listOf(),
            playerData1.id,
            playerData1.id,
            JassType.SIDI_BARAHNI,
            listOf(
                Bet(playerData2.id, Trump.CLUBS, BetHeight.FORTY)
            ),
            listOf(Bet.BetAction.BET),
            GameState(),
            score = Score.INITIAL,
        )

    @Before
    fun setup() {
        GameStateHolder.runCpuAsynchronously = false
        GameStateHolder.gameState = gameState
        GameStateHolder.bettingState = bettingState
        GameStateHolder.players = players
    }

    @Test
    fun correctInitialScreenContent() {
        ActivityScenario.launch<JassRoundActivity>(defaultJassRoundIntent).use {
            composeTestRule.onNodeWithText("first_2 second_2").assertExists()
            composeTestRule.onNodeWithText("first_3 second_3").assertExists()
            composeTestRule.onNodeWithText("first_4 second_4").assertExists()

            // current player's cards
            val currentPlayer = GameStateHolder.players[GameStateHolder.gameState.currentUserId.ordinal]
            currentPlayer.cards.forEach {
                composeTestRule.onNodeWithContentDescription(it.toString()).assertExists()
            }

            val trickCards = GameStateHolder.gameState.roundState.trick().cards
            trickCards.forEach {
                composeTestRule.onNodeWithContentDescription(it.toString()).assertExists()
            }
        }
    }

    @Test
    fun emptyingTrickWithHandCardsClickThenPlayingACardRemovesItFromThePlayersHand() {
        ActivityScenario.launch<JassRoundActivity>(defaultJassRoundIntent).use {
            val currentPlayer =
                GameStateHolder.players[GameStateHolder.gameState.currentUserId.ordinal]
            val cardToPlay = currentPlayer.cards[0]

            assertThat(GameStateHolder.gameState.currentPlayerId, `is`(currentPlayer.id))
            composeTestRule.onNodeWithContentDescription(cardToPlay.toString())
                .assertExists()
                .assertIsDisplayed()
                .performClick() // remove full trick
                .performClick() // play the card

            assertThat(GameStateHolder.players[GameStateHolder.gameState.currentUserId.ordinal].cards.contains(cardToPlay), `is`(false))
            assertThat(GameStateHolder.gameState.roundState.trick().cards.contains(cardToPlay), `is`(true))
        }
    }

    @Test
    fun emptyingTrickWithTrickClickThenPlayingACardRemovesItFromThePlayersHand() {
        ActivityScenario.launch<JassRoundActivity>(defaultJassRoundIntent).use {
            val currentPlayer =
                GameStateHolder.players[GameStateHolder.gameState.currentUserId.ordinal]
            val cardToPlay = currentPlayer.cards[0]

            assertThat(GameStateHolder.gameState.currentPlayerId, `is`(currentPlayer.id))

            // card played by the current user
            composeTestRule.onNodeWithContentDescription(Deck.STANDARD_DECK.cards[0].toString())
                .assertExists()
                .assertIsDisplayed()
                .performClick() // remove full trick

            composeTestRule.onNodeWithContentDescription(cardToPlay.toString())
                .assertExists()
                .assertIsDisplayed()
                .performClick() // play the card

            assertThat(GameStateHolder.players[GameStateHolder.gameState.currentUserId.ordinal].cards.contains(cardToPlay), `is`(false))
            assertThat(GameStateHolder.gameState.roundState.trick().cards.contains(cardToPlay), `is`(true))
        }
    }
}