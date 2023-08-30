package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.CardDistributionsHandler
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class SidiBarraniBiddingCpuTest {

    lateinit var defaultBettingState: BettingState
    lateinit var bettingCpu: SidiBarraniBiddingCpu

    private val defaultPlayerDatas = listOf(
        PlayerData().copy(
            id = PlayerId.PLAYER_1,
            firstName = "player1",
            cards = Deck.STANDARD_DECK.cards.subList(0, 9)),
        PlayerData().copy(
            id = PlayerId.PLAYER_2,
            firstName = "player2",
            cards = Deck.STANDARD_DECK.cards.subList(9, 18)),
        PlayerData().copy(
            id = PlayerId.PLAYER_3,
            firstName = "player3",
            cards = Deck.STANDARD_DECK.cards.subList(18, 27)),
        PlayerData().copy(
            id = PlayerId.PLAYER_4,
            firstName = "player4",
            cards = Deck.STANDARD_DECK.cards.subList(27, 36)),
    )

    @Before
    fun setUp() {
        defaultBettingState = BettingState(
            currentUserId = PlayerId.PLAYER_1,
            playerEmails = listOf(),
            currentBetterId= defaultPlayerDatas[0].id,
            startingBetterId= defaultPlayerDatas[0].id,
            jassType = JassType.SIDI_BARRANI,
            bets = listOf(),
            betActions = listOf(),
            gameState = GameState(),
            score = Score.INITIAL,
            cardDistributionsHandler = CardDistributionsHandler()
        )
        bettingCpu = SidiBarraniBiddingCpu(PlayerId.PLAYER_1)
    }

    fun resetCardDistributionsHandler() {
        defaultBettingState = defaultBettingState.copy(cardDistributionsHandler = CardDistributionsHandler())
    }

    // Betting with no previous information
    @Test
    fun correctSpacingForJackBetting() {
        val trumpCards = listOf(
            Card(Suit.HEARTS, Rank.JACK),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.EIGHT),
            Card(Suit.HEARTS, Rank.SEVEN),
            Card(Suit.HEARTS, Rank.SIX),
        )

        val fillUpCards = listOf(
            Card(Suit.SPADES, Rank.JACK),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.KING),

            Card(Suit.CLUBS, Rank.QUEEN),
            Card(Suit.CLUBS, Rank.KING),
        )
        val expectedBetheights = listOf(
            BetHeight.FORTY,
            BetHeight.SIXTY,
            BetHeight.EIGHTY,
            BetHeight.HUNDRED,
        )

        for (i in 3..6) {
            resetCardDistributionsHandler()
            // saying trump with jack with 'i' cards
            val handCards = trumpCards.take(i) + fillUpCards.take(9-i)

            val expectedBet = Bet(PlayerId.PLAYER_1, Trump.HEARTS, expectedBetheights[i-3])
            assertThat(bettingCpu.bet(defaultBettingState, handCards), `is`(expectedBet))
        }
    }

    @Test
    fun correctSpacingForNellBetting() {
        val trumpCards = listOf(
            Card(Suit.HEARTS, Rank.NINE),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.EIGHT),
            Card(Suit.HEARTS, Rank.SEVEN),
            Card(Suit.HEARTS, Rank.SIX),
        )

        val fillUpCards = listOf(
            Card(Suit.SPADES, Rank.JACK),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.KING),

            Card(Suit.CLUBS, Rank.QUEEN),
            Card(Suit.CLUBS, Rank.KING),
        )
        val expectedBetheights = listOf(
            BetHeight.FIFTY,
            BetHeight.SEVENTY,
            BetHeight.NINETY,
        )

        for (i in 3..5) {
            resetCardDistributionsHandler()

            // saying trump with jack with 'i' cards
            val handCards = trumpCards.take(i) + fillUpCards.take(9-i)

            val expectedBet = Bet(PlayerId.PLAYER_1, Trump.HEARTS, expectedBetheights[i-3])
            assertThat(bettingCpu.bet(defaultBettingState, handCards), `is`(expectedBet))
        }
    }

    @Test
    fun correctSpacingForObeAbeBetting() {
        val aces = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.CLUBS, Rank.ACE),
        )

        val fillUpCards = listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.HEARTS, Rank.QUEEN),
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.EIGHT),
            Card(Suit.HEARTS, Rank.SEVEN),

            Card(Suit.SPADES, Rank.KING),

            Card(Suit.DIAMONDS, Rank.KING),

            Card(Suit.CLUBS, Rank.KING),
        )
        val expectedBetheights = listOf(
            BetHeight.FORTY,
            BetHeight.FIFTY,
            BetHeight.SIXTY,
            BetHeight.SEVENTY,
        )

        for (i in 1..4) {
            resetCardDistributionsHandler()

            // saying trump with jack with 'i' cards
            var handCards = aces.take(i) + fillUpCards.take(9-i)

            val expectedBet = Bet(PlayerId.PLAYER_1, Trump.OBE_ABE, expectedBetheights[i-1])
            assertThat(bettingCpu.bet(defaultBettingState, handCards), `is`(expectedBet))
        }
    }

    @Test
    fun correctSpacingForUngerUfeBetting() {
        val aces = listOf(
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.DIAMONDS, Rank.SIX),
            Card(Suit.CLUBS, Rank.SIX),
        )

        val fillUpCards = listOf(
            Card(Suit.HEARTS, Rank.SEVEN),
            Card(Suit.HEARTS, Rank.EIGHT),
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.QUEEN),
            Card(Suit.HEARTS, Rank.KING),

            Card(Suit.SPADES, Rank.SEVEN),

            Card(Suit.DIAMONDS, Rank.TEN),

            Card(Suit.CLUBS, Rank.NINE),
        )
        val expectedBetheights = listOf(
            BetHeight.FORTY,
            BetHeight.FIFTY,
            BetHeight.SIXTY,
            BetHeight.SEVENTY,
        )

        for (i in 1..4) {
            resetCardDistributionsHandler()

            // saying trump with jack with 'i' cards
            val handCards = aces.take(i) + fillUpCards.take(9-i)

            val expectedBet = Bet(PlayerId.PLAYER_1, Trump.UNGER_UFE, expectedBetheights[i-1])
            assertThat(bettingCpu.bet(defaultBettingState, handCards), `is`(expectedBet))
        }
    }
}