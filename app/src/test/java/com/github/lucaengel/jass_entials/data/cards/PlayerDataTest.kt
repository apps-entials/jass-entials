package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert.assertThrows
import org.junit.Test
import java.lang.IllegalStateException

class PlayerDataTest {

    private val defaultPlayerData = PlayerData(
        id = PlayerId.PLAYER_1,
        firstName = "",
        lastName = "",
        cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.SIX),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.TEN),

            Card(Suit.CLUBS, Rank.ACE),
            Card(Suit.CLUBS, Rank.SIX),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ),
        teamNb = 0,
        token = "",
    )

    @Test
    fun playableCardsShowsAllCardsWithEmptyTrick() {
        val emptyTrick = Trick.initial(defaultPlayerData.id, Trump.HEARTS)

        val playableCards = defaultPlayerData.playableCards(emptyTrick, Trump.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(*defaultPlayerData.cards.toTypedArray()))
    }

    @Test
    fun playableCardsShowsTrumpCardsAndPlayableSuitCardsWhenJustASuitHasBeenPlayed() {
        val trick = Trick.initial(PlayerId.PLAYER_1, Trump.CLUBS)
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.SEVEN))

        val playableCards = defaultPlayerData.playableCards(trick, Trump.CLUBS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.SIX),

            Card(Suit.CLUBS, Rank.ACE),
            Card(Suit.CLUBS, Rank.SIX),
        ))
    }

    @Test
    fun cannotUndertrump() {
        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.CLUBS)
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.SEVEN))
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.TEN))

        val playableCards = defaultPlayerData.playableCards(trick, Trump.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.TEN),

            Card(Suit.HEARTS, Rank.ACE),
        )) // no six since that would be undertrumping
    }

    @Test
    fun ifAceOfTrumpPlayedAndNoUnderTrumpingNellAndJackStillPlayable() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),
            Card(Suit.HEARTS, Rank.JACK),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.HEARTS)
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.SEVEN))
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.ACE))

        val playableCards = player.playableCards(trick, Trump.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.HEARTS, Rank.NINE),
            Card(Suit.HEARTS, Rank.JACK),
        ))
    }

    @Test
    fun ifNellOfTrumpPlayedAndNoUnderTrumpingNellAndJackStillPlayable() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.JACK),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.HEARTS)
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.SEVEN))
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.NINE))

        val playableCards = player.playableCards(trick, Trump.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.HEARTS, Rank.JACK),
        ))
    }

    @Test
    fun ifTrumpPlayedFirstAllTrumpsAllowed() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.HEARTS)
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.HEARTS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),
        ))
    }

    @Test
    fun playableCardsForUngerUfeAreOnlyCardsWithTheSameSuit() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.UNGER_UFE)
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.UNGER_UFE)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),
        ))
    }

    @Test
    fun ifUserHasNoTrumpCardsAndTrumpWasPlayedTheUserCanPlayAnyCard() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.CLUBS)
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }

    @Test
    fun noUnderTrumpingAllowed() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.SIX),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.DIAMONDS)
            .withNewCardPlayed(Card(Suit.HEARTS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.DIAMONDS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            // not the six of diamonds as this would be under trumping
            Card(Suit.DIAMONDS, Rank.ACE),
        ))
    }

    @Test // no trump cards and no first card suit cards in the hand
    fun ifNoCardCanBePlayedAllCardsCanBePlayed() {

        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.CLUBS)
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }

    @Test
    fun trumpJackDoesNotHaveToBePlayedWhenTrumpIsOutAndJackIsTheOnlyTrumpCard() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.CLUBS, Rank.JACK),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.CLUBS)
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        // All cards should be able to be played since the current player only has the trump jack
        // of the trump suit that is out
        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }

    @Test
    fun playerMustPlayTheJackIfItIsTheOnlyCardToHoldSuitWithAndItIsNotTrump() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.CLUBS, Rank.JACK),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.HEARTS)
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.UNGER_UFE)

        // All cards should be able to be played since the current player only has the trump jack
        // of the trump suit that is out
        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.CLUBS, Rank.JACK)
        ))
    }

    @Test
    fun playerMustHoldSuitIfTheyCanAndTheyHaveNoTrumpCards() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.CLUBS, Rank.TEN),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.HEARTS)
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        // All cards should be able to be played since the current player only has the trump jack
        // of the trump suit that is out
        assertThat(playableCards, Matchers.containsInAnyOrder(
            Card(Suit.CLUBS, Rank.TEN)
        ))
    }

    @Test
    fun allCardsArePlayableIfASuitIsOutThatThePlayerDoesNotHaveAndThePlayerHasTrumpAndNonTrumpCards() {
        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.CLUBS, Rank.JACK),
            Card(Suit.CLUBS, Rank.TEN),
        ))

        val trick = Trick.initial(PlayerId.PLAYER_3, Trump.CLUBS)
            .withNewCardPlayed(Card(Suit.DIAMONDS, Rank.TEN))
            .withNewCardPlayed(Card(Suit.CLUBS, Rank.TEN))

        val playableCards = player.playableCards(trick, Trump.CLUBS)

        // All cards should be able to be played since the current player does not have any diamonds
        assertThat(playableCards, Matchers.containsInAnyOrder(
            *player.cards.toTypedArray()
        ))
    }
    @Test
    fun withCaredPlayedReturnsPlayerWithoutTheGivenCard() {
        val expectedCards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),

            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.SIX),

            Card(Suit.CLUBS, Rank.JACK),
            Card(Suit.CLUBS, Rank.TEN),
            )
        val cardToPlay = Card(Suit.DIAMONDS, Rank.TEN)

        val player = defaultPlayerData.copy(cards = listOf(
            *expectedCards.toTypedArray(),
            cardToPlay,
        ))
        val newPlayer = player.withCardPlayed(cardToPlay)

        assertThat(newPlayer.cards, Matchers.containsInAnyOrder(
            *expectedCards.toTypedArray()
        ))
    }

    @Test
    fun withCardPlayedThrowsIfTheCardIsNotInThePlayersHand() {

        val inExistentCard = Card(Suit.DIAMONDS, Rank.TEN)

        val player = defaultPlayerData.copy(cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),
            Card(Suit.HEARTS, Rank.NINE),
        ))

        assertThrows(IllegalStateException::class.java) {
            player.withCardPlayed(inExistentCard)
        }
    }
}
