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

    // Betting with no previous information, and opponent betting
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
        val expectedBetHeights = listOf(
            BetHeight.FORTY,
            BetHeight.SIXTY,
            BetHeight.EIGHTY,
            BetHeight.HUNDRED,
        )

        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 3,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = expectedBetHeights,
            expectedTrump = Trump.HEARTS
        )

        // Betting after opponents bet
        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.FIFTY)
        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(2) } // start at 70
        addBetToBettingState(opponentBet, PlayerId.PLAYER_4)
        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 3,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = newExpectedBetHeights,
            expectedTrump = Trump.HEARTS
        )
    }

    @Test
    fun correctSpacingForNellBetting() {
        val trumpCards = listOf(
            Card(Suit.HEARTS, Rank.NINE),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.EIGHT),
            Card(Suit.HEARTS, Rank.SEVEN),
        )

        val fillUpCards = listOf(
            Card(Suit.SPADES, Rank.JACK),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.KING),

            Card(Suit.CLUBS, Rank.QUEEN),
            Card(Suit.CLUBS, Rank.KING),
        )
        val expectedBetHeights = listOf(
            BetHeight.FIFTY,
            BetHeight.SEVENTY,
            BetHeight.NINETY,
        )

        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 3,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = expectedBetHeights,
            expectedTrump = Trump.HEARTS
        )

        // Betting after opponents bet
        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.FIFTY)
        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(2) } // start at 70
        addBetToBettingState(opponentBet, PlayerId.PLAYER_4)
        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 3,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = newExpectedBetHeights,
            expectedTrump = Trump.HEARTS
        )
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
        val expectedBetHeights = listOf(
            BetHeight.FORTY,
            BetHeight.FIFTY,
            BetHeight.SIXTY,
            BetHeight.SEVENTY,
        )

        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 1,
            trumpableCards = aces,
            fillUpCards = fillUpCards,
            expectedBetHeights = expectedBetHeights,
            expectedTrump = Trump.OBE_ABE
        )

        // Betting after opponents bet
        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.FORTY)
        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(1) }
        addBetToBettingState(opponentBet, PlayerId.PLAYER_4)
        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 1,
            trumpableCards = aces,
            fillUpCards = fillUpCards,
            expectedBetHeights = newExpectedBetHeights,
            expectedTrump = Trump.OBE_ABE
        )
    }

    @Test
    fun correctSpacingForUngerUfeBetting() {
        val sixes = listOf(
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
        val expectedBetHeights = listOf(
            BetHeight.FORTY,
            BetHeight.FIFTY,
            BetHeight.SIXTY,
            BetHeight.SEVENTY,
        )

        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 1,
            trumpableCards = sixes,
            fillUpCards = fillUpCards,
            expectedBetHeights = expectedBetHeights,
            expectedTrump = Trump.UNGER_UFE
        )

        // Betting after opponents bet
        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.FORTY)
        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(1) }
        addBetToBettingState(opponentBet, PlayerId.PLAYER_4)
        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 1,
            trumpableCards = sixes,
            fillUpCards = fillUpCards,
            expectedBetHeights = newExpectedBetHeights,
            expectedTrump = Trump.UNGER_UFE
        )
    }

    // betting with information of teammate
//    @Test  // TODO: is evalueation good like the cpu does it or prefer my evaluation???
//    fun correctSpacingForJackBettingAfterTeammateBetNell() {
//        val trumpCards = listOf(
//            Card(Suit.HEARTS, Rank.JACK),
//            Card(Suit.HEARTS, Rank.ACE),
//            Card(Suit.HEARTS, Rank.TEN),
//        )
//
//        val fillUpCards = listOf(
//            Card(Suit.SPADES, Rank.JACK),
//            Card(Suit.SPADES, Rank.SEVEN),
//            Card(Suit.SPADES, Rank.SIX),
//
//            Card(Suit.DIAMONDS, Rank.ACE),
//            Card(Suit.DIAMONDS, Rank.KING),
//
//            Card(Suit.CLUBS, Rank.QUEEN),
//            Card(Suit.CLUBS, Rank.NINE),
//            Card(Suit.CLUBS, Rank.SIX),
//        )
//        val expectedBetHeights = listOf(
//            BetHeight.SIXTY,
//            BetHeight.EIGHTY,
//            BetHeight.HUNDRED,
//        )
//
//        val teamMateBet = Bet(PlayerId.PLAYER_3, Trump.HEARTS, BetHeight.FIFTY)
//        addBetToBettingState(listOf(teamMateBet, null), PlayerId.PLAYER_3)
//
//        checkForCorrectBetPlaced(
//            startingNbTrumpableCards = 1,
//            trumpableCards = trumpCards,
//            fillUpCards = fillUpCards,
//            expectedBetHeights = expectedBetHeights,
//            expectedTrump = Trump.HEARTS
//        )
//
//        // Betting after opponents bet
////        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.FIFTY)
////        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(2) } // start at 70
////        addBetToBettingState(opponentBet, PlayerId.PLAYER_4)
////        checkForCorrectBetPlaced(
////            startingNbTrumpableCards = 1,
////            trumpableCards = trumpCards,
////            fillUpCards = fillUpCards,
////            expectedBetHeights = newExpectedBetHeights,
////            expectedTrump = Trump.HEARTS
////        )
//    }

    @Test
    fun correctSpacingForJackBettingAfterTeammateBetNell() {
        val trumpCards = listOf(
            Card(Suit.HEARTS, Rank.JACK),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.TEN),
        )

        val fillUpCards = listOf(
            Card(Suit.SPADES, Rank.JACK),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.DIAMONDS, Rank.KING),
            Card(Suit.DIAMONDS, Rank.TEN),

            Card(Suit.CLUBS, Rank.QUEEN),
            Card(Suit.CLUBS, Rank.NINE),
            Card(Suit.CLUBS, Rank.SIX),
        )
        val expectedBetHeights = listOf(
            BetHeight.SIXTY,
            BetHeight.EIGHTY,
            BetHeight.HUNDRED,
        )

        val teamMateBet = Bet(PlayerId.PLAYER_3, Trump.HEARTS, BetHeight.FIFTY)
        addBetToBettingState(listOf(teamMateBet, null), PlayerId.PLAYER_3)

        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 1,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = expectedBetHeights,
            expectedTrump = Trump.HEARTS
        )

        // Betting after opponents bet
        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.SIXTY)
        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(2) } // start at 70
        addBetToBettingState(listOf(teamMateBet, opponentBet), PlayerId.PLAYER_3)
        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 1,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = newExpectedBetHeights,
            expectedTrump = Trump.HEARTS
        )
    }

//    @Test // TODO: is evalueation good like the cpu does it or prefer my evaluation???
//    fun correctSpacingForNellBettingAfterTeammateBetJack() {
//        val trumpCards = listOf(
//            Card(Suit.HEARTS, Rank.NINE),
//            Card(Suit.HEARTS, Rank.ACE),
//            Card(Suit.HEARTS, Rank.TEN),
//        )
//
//        val fillUpCards = listOf(
//            Card(Suit.SPADES, Rank.JACK),
//            Card(Suit.SPADES, Rank.SEVEN),
//            Card(Suit.SPADES, Rank.SIX),
//
//            Card(Suit.DIAMONDS, Rank.KING),
//            Card(Suit.DIAMONDS, Rank.TEN),
//
//            Card(Suit.CLUBS, Rank.QUEEN),
//            Card(Suit.CLUBS, Rank.NINE),
//            Card(Suit.CLUBS, Rank.SIX),
//        )
//        val expectedBetHeights = listOf(
//            BetHeight.FIFTY,
//            BetHeight.SEVENTY,
//            BetHeight.NINETY,
//        )
//
//        val teamMateBet = Bet(PlayerId.PLAYER_3, Trump.HEARTS, BetHeight.FORTY)
//        addBetToBettingState(listOf(teamMateBet, null), PlayerId.PLAYER_3)
//
//        checkForCorrectBetPlaced(
//            startingNbTrumpableCards = 2,
//            trumpableCards = trumpCards,
//            fillUpCards = fillUpCards,
//            expectedBetHeights = expectedBetHeights,
//            expectedTrump = Trump.HEARTS
//        )
//
//        // Betting after opponents bet
//        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.SIXTY)
//        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(2) } // start at 70
//        addBetToBettingState(listOf(teamMateBet, opponentBet), PlayerId.PLAYER_3)
//        checkForCorrectBetPlaced(
//            startingNbTrumpableCards = 2,
//            trumpableCards = trumpCards,
//            fillUpCards = fillUpCards,
//            expectedBetHeights = newExpectedBetHeights,
//            expectedTrump = Trump.HEARTS
//        )
//    }
    @Test
    fun correctSpacingForNellBettingAfterTeammateBetJack() {
        val trumpCards = listOf(
            Card(Suit.HEARTS, Rank.NINE),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.TEN),
        )

        val fillUpCards = listOf(
            Card(Suit.SPADES, Rank.JACK),
            Card(Suit.SPADES, Rank.SEVEN),

            Card(Suit.DIAMONDS, Rank.KING),
            Card(Suit.DIAMONDS, Rank.TEN),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.CLUBS, Rank.QUEEN),
            Card(Suit.CLUBS, Rank.NINE),
            Card(Suit.CLUBS, Rank.SIX),
        )
        val expectedBetHeights = listOf(
            BetHeight.FIFTY,
            BetHeight.SEVENTY,
            BetHeight.NINETY,
        )

        val teamMateBet = Bet(PlayerId.PLAYER_3, Trump.HEARTS, BetHeight.FORTY)
        addBetToBettingState(listOf(teamMateBet, null), PlayerId.PLAYER_3)

        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 2,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = expectedBetHeights,
            expectedTrump = Trump.HEARTS
        )

        // Betting after opponents bet
        val opponentBet = Bet(PlayerId.PLAYER_4, Trump.UNGER_UFE, BetHeight.SIXTY)
        val newExpectedBetHeights = expectedBetHeights.map { it.nHigher(2) } // start at 70
        addBetToBettingState(listOf(teamMateBet, opponentBet), PlayerId.PLAYER_3)
        checkForCorrectBetPlaced(
            startingNbTrumpableCards = 2,
            trumpableCards = trumpCards,
            fillUpCards = fillUpCards,
            expectedBetHeights = newExpectedBetHeights,
            expectedTrump = Trump.HEARTS
        )
    }

    // TODO:
    //  - ope abe and unger ufe after teammate bet is not tested yet
    //  - also not the case where current cpu should not bet...





    private fun addBetToBettingState(
        bets: Bet,
        firstBetPlacingPlayerId: PlayerId
    ) {
        return addBetToBettingState(listOf(bets), firstBetPlacingPlayerId)
    }

    private fun addBetToBettingState(
        bets: List<Bet?>,
        firstBetPlacingPlayerId: PlayerId
    ) {
        var tempBettingState = defaultBettingState
            .copy(
                startingBetterId = firstBetPlacingPlayerId,
                currentBetterId = firstBetPlacingPlayerId,
            )
        for (bet in bets) tempBettingState = tempBettingState.nextPlayer(bet)

        defaultBettingState = tempBettingState
    }

    /**
     * Checks if the correct bet is placed for a given number of trumpable cards.
     *
     * @param startingNbTrumpableCards the number of trumpable cards to start with (i.e. so the bet is placed)
     * @param trumpableCards the trumpable cards
     * @param fillUpCards the fill up cards
     * @param expectedBetHeights the expected bet heights
     * @param expectedTrump the expected trump
     */
    private fun checkForCorrectBetPlaced(
        startingNbTrumpableCards: Int,
        trumpableCards: List<Card>,
        fillUpCards: List<Card>,
        expectedBetHeights: List<BetHeight>,
        expectedTrump: Trump
    ) {
        // otherwise we will get an index out of bounds exception
        //  --> adapt trumpableCards size to how many bet heights
        //      you want to test
        assertThat(trumpableCards.size - startingNbTrumpableCards <= expectedBetHeights.size, `is`(true))

        for (i in startingNbTrumpableCards..trumpableCards.size) {

            // saying trump with 'i' trumpable cards
            val handCards = trumpableCards.take(i) + fillUpCards.take(9 - i)

            assertThat(handCards.size, `is`(9))

            val expectedBet =
                Bet(PlayerId.PLAYER_1, expectedTrump, expectedBetHeights[i - startingNbTrumpableCards])
            assertThat(bettingCpu.bet(defaultBettingState, handCards), `is`(expectedBet))
        }
    }
}