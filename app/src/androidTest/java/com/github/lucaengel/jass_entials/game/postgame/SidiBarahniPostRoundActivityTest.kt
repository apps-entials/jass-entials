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
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SidiBarahniPostRoundActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val postRoundDefaultIntent = Intent(ApplicationProvider.getApplicationContext(), SidiBarahniPostRoundActivity::class.java)

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
    private val playerData2 = PlayerData(
        PlayerId.PLAYER_2,
        "first_2",
        "second_2",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(12, 20)),
        0,
        "123"
    )
    private val playerData3 = PlayerData(
        PlayerId.PLAYER_3,
        "first_3",
        "second_3",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(20, 28)),
        0,
        "123"
    )
    private val playerData4 = PlayerData(
        PlayerId.PLAYER_4,
        "first_4",
        "second_4",
        Deck.sortPlayerCards(shuffledDeck.cards.subList(28, 36)),
        0,
        "123"
    )
    private val players = listOf(playerData1, playerData2, playerData3, playerData4)


    private var gameState: GameState = GameState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentPlayerId= playerData1.id,
        startingPlayerId= playerData1.id,
        currentRound = 1,
        currentTrick = Trick(shuffledDeck.cards.subList(0, 4).mapIndexed { index, card -> Trick.TrickCard(card, players[index].id) }),
        currentRoundTrickWinners = listOf(
            Trick.TrickWinner(playerData1.id,
            Trick(listOf(
                Trick.TrickCard(Card(Suit.HEARTS, Rank.ACE), playerData1.id),
                Trick.TrickCard(Card(Suit.HEARTS, Rank.KING), playerData2.id),
                Trick.TrickCard(Card(Suit.HEARTS, Rank.SIX), playerData3.id),
                Trick.TrickCard(Card(Suit.HEARTS, Rank.TEN), playerData4.id))))),
        currentTrickNumber = 1,
        currentTrump = Trump.OBE_ABE,
        winningBet = Bet(),
        playerCards = Deck.STANDARD_DECK.dealCards(),
    )

    @Before
    fun setup() {
        GameStateHolder.gameState = gameState
    }

    @Test
    fun correctInitialScreenContent() {
        ActivityScenario.launch<SidiBarahniPostRoundActivity>(postRoundDefaultIntent).use {
            players.forEach {
                composeTestRule.onNodeWithText("${it.firstName} ${it.lastName}", substring = true).assertExists()
            }
        }
    }

    @Test
    fun correctPointsAttributedToEachPerson() {
        ActivityScenario.launch<SidiBarahniPostRoundActivity>(postRoundDefaultIntent).use {

            composeTestRule.onNodeWithText("${playerData1.firstName} ${playerData1.lastName}: 25").assertExists()
            composeTestRule.onNodeWithText("${playerData2.firstName} ${playerData2.lastName}: 0").assertExists()
            composeTestRule.onNodeWithText("${playerData3.firstName} ${playerData3.lastName}: 0").assertExists()
            composeTestRule.onNodeWithText("${playerData4.firstName} ${playerData4.lastName}: 0").assertExists()
        }
    }

    @Test
    fun startNextRoundButtonOpensNextSidiBarahniBiddingActivity() {
        ActivityScenario.launch<SidiBarahniPostRoundActivity>(postRoundDefaultIntent).use {
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