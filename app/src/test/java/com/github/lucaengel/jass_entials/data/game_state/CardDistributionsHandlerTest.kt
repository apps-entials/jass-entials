package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class CardDistributionsHandlerTest {


    private lateinit var distributionsHandler: CardDistributionsHandler

    @Before
    fun setUp() {
        distributionsHandler = CardDistributionsHandler()
    }

    private fun resetCardDistributions() {
        distributionsHandler = CardDistributionsHandler()
    }


    // ---------- extract knowledge from bets test -------------

    @Test
    fun jackWithOthersIsCorrectlyAnalyzed() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FORTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_2,
                bet = BetHeight.EIGHTY,
                trump = Trump.DIAMONDS
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1]!!.contains(Card(Suit.HEARTS, Rank.JACK)), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS]!!, `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_2]!!.contains(Card(Suit.DIAMONDS, Rank.JACK)), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_2]!![Suit.DIAMONDS]!!, `is`(4))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(Bet(
            playerId = PlayerId.PLAYER_1,
            bet = BetHeight.EIGHTY,
            trump = Trump.HEARTS
        )))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1]!!.contains(Card(Suit.HEARTS, Rank.JACK)), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS]!!, `is`(5))
    }

    @Test
    fun nellWithOthersIsCorrectlyAnalyzed() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FIFTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_2,
                bet = BetHeight.NINETY,
                trump = Trump.DIAMONDS
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1]!!.contains(Card(Suit.HEARTS, Rank.NINE)), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS]!!, `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_2]!!.contains(Card(Suit.DIAMONDS, Rank.NINE)), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_2]!![Suit.DIAMONDS]!!, `is`(4))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(Bet(
            playerId = PlayerId.PLAYER_1,
            bet = BetHeight.NINETY,
            trump = Trump.HEARTS
        )))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1]!!.contains(Card(Suit.HEARTS, Rank.NINE)), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS]!!, `is`(5))
    }

    @Test
    fun acesInObeAbeAreCorrectlyAnalyzed() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FORTY,
                trump = Trump.OBE_ABE
            ),
            Bet(
                playerId = PlayerId.PLAYER_2,
                bet = BetHeight.SEVENTY,
                trump = Trump.OBE_ABE
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()
        var aces = distributionsHandler.acesPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1].isNullOrEmpty(), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1].isNullOrEmpty(), `is`(true))
        assertThat(aces[PlayerId.PLAYER_1], `is`(1))

        assertThat(guaranteedCards[PlayerId.PLAYER_2].isNullOrEmpty(), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_2].isNullOrEmpty(), `is`(true))
        assertThat(aces[PlayerId.PLAYER_2], `is`(3))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FIFTY,
                trump = Trump.HEARTS
            ), Bet(
                playerId = PlayerId.PLAYER_2,
                bet = BetHeight.NINETY,
                trump = Trump.OBE_ABE
            )
        ))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()
        aces = distributionsHandler.acesPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_2]!!, containsInAnyOrder(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.CLUBS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE),
        ))
        assertThat(cardsPerSuit[PlayerId.PLAYER_2].isNullOrEmpty(), `is`(true))
        assertThat(aces[PlayerId.PLAYER_2], `is`(4))
    }

    @Test
    fun sixesInUngerUfeAreCorrectlyAnalyzed() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FORTY,
                trump = Trump.UNGER_UFE
            ),
            Bet(
                playerId = PlayerId.PLAYER_2,
                bet = BetHeight.SEVENTY,
                trump = Trump.UNGER_UFE
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()
        var sixes = distributionsHandler.sixesPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1].isNullOrEmpty(), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1].isNullOrEmpty(), `is`(true))
        assertThat(sixes[PlayerId.PLAYER_1], `is`(1))

        assertThat(guaranteedCards[PlayerId.PLAYER_2].isNullOrEmpty(), `is`(true))
        assertThat(cardsPerSuit[PlayerId.PLAYER_2].isNullOrEmpty(), `is`(true))
        assertThat(sixes[PlayerId.PLAYER_2], `is`(3))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FIFTY,
                trump = Trump.HEARTS
            ), Bet(
                playerId = PlayerId.PLAYER_2,
                bet = BetHeight.NINETY,
                trump = Trump.UNGER_UFE
            )
        ))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()
        sixes = distributionsHandler.sixesPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_2]!!, containsInAnyOrder(
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.DIAMONDS, Rank.SIX),
            Card(Suit.CLUBS, Rank.SIX),
            Card(Suit.SPADES, Rank.SIX),
        ))
        assertThat(cardsPerSuit[PlayerId.PLAYER_2].isNullOrEmpty(), `is`(true))
        assertThat(sixes[PlayerId.PLAYER_2], `is`(4))
    }

    @Test
    fun sayingNellAfterPartnerSayingTrumpJackFoundCorrectly() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FORTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.FIFTY,
                trump = Trump.HEARTS
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.JACK)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.NINE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(2))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.SIXTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.NINETY,
                trump = Trump.HEARTS
            ),
        ))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.JACK)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(4))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.NINE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(3))
    }

    @Test
    fun sayingJackAfterPartnerSayingNellFoundCorrectly() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FIFTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.SIXTY,
                trump = Trump.HEARTS
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.NINE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.JACK)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(1))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.SEVENTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.HUNDRED,
                trump = Trump.HEARTS
            ),
        ))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.NINE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(4))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.JACK)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(2))
    }

    @Test
    fun sayingAceAfterPartnerSayingNellFoundCorrectly() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FIFTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.SEVENTY,
                trump = Trump.HEARTS
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.NINE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.ACE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(3))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FIFTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.NINETY,
                trump = Trump.HEARTS
            ),
        ))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.NINE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.ACE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(4))
    }

    @Test
    fun sayingAceAfterPartnerSayingJackFoundCorrectly() {
        val bets = listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FORTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.SIXTY,
                trump = Trump.HEARTS
            ),
        )

        distributionsHandler.extractKnowledgeFromBets(2, bets)

        var guaranteedCards = distributionsHandler.guaranteedCards()
        var cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.JACK)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.ACE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(3))

        // try with five cards
        resetCardDistributions()

        distributionsHandler.extractKnowledgeFromBets(1, listOf(
            Bet(
                playerId = PlayerId.PLAYER_1,
                bet = BetHeight.FORTY,
                trump = Trump.HEARTS
            ),
            Bet(
                playerId = PlayerId.PLAYER_3,
                bet = BetHeight.HUNDRED,
                trump = Trump.HEARTS
            ),
        ))

        guaranteedCards = distributionsHandler.guaranteedCards()
        cardsPerSuit = distributionsHandler.cardsPerSuitPerPlayer()

        assertThat(guaranteedCards[PlayerId.PLAYER_1], containsInAnyOrder(Card(Suit.HEARTS, Rank.JACK)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_1]!![Suit.HEARTS], `is`(3))

        assertThat(guaranteedCards[PlayerId.PLAYER_3], containsInAnyOrder(Card(Suit.HEARTS, Rank.ACE)))
        assertThat(cardsPerSuit[PlayerId.PLAYER_3]!![Suit.HEARTS], `is`(5))
    }


    // TODO: add tests for update card distributions
    // ---------- update card distributions tests ------------


}