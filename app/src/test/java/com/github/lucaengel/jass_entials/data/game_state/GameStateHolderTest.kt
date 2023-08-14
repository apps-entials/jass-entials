package com.github.lucaengel.jass_entials.data.game_state

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class GameStateHolderTest {

    @Test
    fun goToNextBettingStateRoundDeals9CardsToEachPlayer() {
        val bettingState = GameStateHolder.bettingState

        GameStateHolder.goToNextBettingStateRound(bettingState.currentBetterId.nextPlayer())

        val newBettingState = GameStateHolder.bettingState
        for (id in PlayerId.values()) {
            assertThat(GameStateHolder.players.first { it.id == id }.cards.size, `is`(9))
        }

        assertThat(newBettingState.currentBetterId,
            `is`(bettingState.currentBetterId.nextPlayer()))
        assertThat(newBettingState.currentBetterId,
            `is`(bettingState.currentUserId.nextPlayer()))
    }

    // TODO: add more tests?
}