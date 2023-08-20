package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameStateTest {

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

    private val defaultGameState = GameState(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentPlayerId = defaultPlayerDatas[0].id,
        startingPlayerId = defaultPlayerDatas[0].id,
        currentRound = 0,
        jassType = JassType.SIDI_BARAHNI,
        roundState = RoundState.initial(Trump.SPADES, defaultPlayerDatas[0].id),
        winningBet = Bet(),
        playerCards = defaultPlayerDatas.associate { it.id to it.cards },
    )

    @Before
    fun setUp() {
        GameStateHolder.players = defaultPlayerDatas
        GameStateHolder.gameState = defaultGameState
    }

    @Test
    fun isLastTrickReturnsTrueOnlyWhenCurrentTrickNumberIsPastNine() {
        for (i in 1..10) {
            val gameState = GameState().copy(roundState = RoundState.initial(
                Trump.HEARTS,
                PlayerId.PLAYER_1
            ).copy(trickNumber = i))
            assertThat(gameState.isLastTrick(), `is`(i == 10))
        }
    }

    @Test
    fun nextTrickThrowsIfCurrentTrickIsNotFull() {
        val gameState = defaultGameState.copy(roundState = RoundState.initial(
            Trump.HEARTS,
            PlayerId.PLAYER_1
        ).copy(trick = Trick.initial(PlayerId.PLAYER_1, Trump.HEARTS)))

        assertThrows(IllegalStateException::class.java) {
            gameState.nextTrick()
        }
    }

    @Test
    fun nextTrickContainsNoCardsAndCorrectStartingPlayer() {
        val gameState = GameState().copy(
            roundState = RoundState.initial(Trump.HEARTS, PlayerId.PLAYER_1).copy(trick = Trick.initial(defaultPlayerDatas[0].id, Trump.OBE_ABE)
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.NINE))
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.EIGHT))
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.ACE))
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.SIX))
            ))

        val newGameState = gameState.nextTrick()
        assertThat(newGameState.roundState.trick().cards, `is`(listOf()))
        assertThat(newGameState.roundState.trick().startingPlayerId, `is`(PlayerId.PLAYER_3))
    }

    @Test
    fun pointsAreCalculatedCorrectly() {
        val gameState = GameState().copy(
            roundState = RoundState.initial(Trump.HEARTS, PlayerId.PLAYER_1).copy(trick = Trick.initial(defaultPlayerDatas[0].id, Trump.CLUBS)
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.NINE))
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.EIGHT))
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.ACE))
                    .withNewCardPlayed(Card(Suit.CLUBS, Rank.SIX))
            ),
        )

        val score = gameState.nextTrick().roundState.score()

        assertThat(score.roundPoints(TeamId.TEAM_1), `is`(25))
        assertThat(score.roundPoints(TeamId.TEAM_2), `is`(0))
    }


    @Test
    fun playCardReturnsNewGameStateWithCardPlayed() {
        val newGameState = defaultGameState.playCard(
            defaultPlayerDatas[0].id,
            defaultPlayerDatas[0].cards[0]
        )

        assertTrue(newGameState.roundState.trick().cards
            .contains(defaultPlayerDatas[0].cards[0])
        )

        assertFalse(newGameState.playerCards[PlayerId.PLAYER_1]!!.contains(defaultPlayerDatas[0].cards[0]))
    }
}