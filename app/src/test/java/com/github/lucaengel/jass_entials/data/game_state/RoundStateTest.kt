package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThrows
import org.junit.Test

class RoundStateTest {

    @Test
    fun gettersReturnRespectiveValuesForInitial() {
        val roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)

        assertThat(roundState.score(), `is`(Score.INITIAL))
        assertThat(roundState.unplayedCards(), `is`(Deck.STANDARD_DECK.cards))
        assertThat(roundState.trick(), `is`(Trick.initial(PlayerId.PLAYER_1, Trump.CLUBS)))
        assertThat(roundState.nextPlayer(), `is`(PlayerId.PLAYER_1))
    }

    @Test
    fun withCardPlayedUpdatesUnplayedCards() {
        val roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
        val card = roundState.unplayedCards().first()

        val newRoundState = roundState.withCardPlayed(card)

        assertThat(newRoundState.unplayedCards(), `is`(roundState.unplayedCards() - card))
    }

    @Test
    fun withCardPlayedThrowsOnCardThatHasAlreadyBeenPlayed() {
        val roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
        val card = roundState.unplayedCards().first()
        val newRoundState = roundState.withCardPlayed(card)

        assertThrows(IllegalStateException::class.java) {
            newRoundState.withCardPlayed(card)
        }
    }

    @Test
    fun withTrickCollectedThrowsOnNotFullTrick() {
        val roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
        val card = roundState.unplayedCards().first()
        val newRoundState = roundState.withCardPlayed(card)

        assertThrows(IllegalStateException::class.java) {
            newRoundState.withTrickCollected()
        }
    }

    @Test
    fun withTrickCollectedCorrectlyGoesToNextTrick() {
        val roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.ACE))
            .withCardPlayed(Card(Suit.CLUBS, Rank.KING))
            .withCardPlayed(Card(Suit.CLUBS, Rank.QUEEN))
            .withCardPlayed(Card(Suit.CLUBS, Rank.JACK)) // winner
            .withTrickCollected()

        assertThat(roundState.score().roundPoints(PlayerId.PLAYER_1.teamId()), `is`(0))
        assertThat(roundState.score().roundPoints(PlayerId.PLAYER_2.teamId()), `is`(38))
        assertThat(roundState.score().roundPoints(PlayerId.PLAYER_3.teamId()), `is`(0))
        assertThat(roundState.score().roundPoints(PlayerId.PLAYER_4.teamId()), `is`(38))

        assertThat(roundState.unplayedCards(),
            `is`(Deck.STANDARD_DECK.cards
                - Card(Suit.CLUBS, Rank.ACE)
                - Card(Suit.CLUBS, Rank.KING)
                - Card(Suit.CLUBS, Rank.QUEEN)
                - Card(Suit.CLUBS, Rank.JACK)))

        assertThat(roundState.trick(), `is`(Trick.initial(PlayerId.PLAYER_4, Trump.CLUBS)))
    }

    @Test
    fun nextPlayerReturnsNextPlayerInTrick() {
        val roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.ACE))
            .withCardPlayed(Card(Suit.CLUBS, Rank.KING))
            .withCardPlayed(Card(Suit.CLUBS, Rank.QUEEN))

        assertThat(roundState.nextPlayer(), `is`(PlayerId.PLAYER_4))
    }

    @Test
    fun isRoundOverOnlyReturnsTrueAfterNinthRound() {
        var roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)

        for (card in Deck.STANDARD_DECK.cards) {

            roundState = roundState.withCardPlayed(card)

            assertThat(roundState.isRoundOver(), `is`(false))

            if (roundState.trick().isFull())
                roundState = roundState.withTrickCollected()
        }

        assertThat(roundState.isRoundOver(), `is`(true))
    }
}