package com.github.lucaengel.jass_entials.data.game_state

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class GameStateHolderTest {

    @Test
    fun goToNextBettingStateRoundDeals9CardsToEachPlayer() {
        val bettingState = GameStateHolder.bettingState

        GameStateHolder.goToNextBettingStateRound(bettingState.playerEmails[bettingState.currentUserIdx + 1 % 4])

        val newBettingState = GameStateHolder.bettingState
        for (email in newBettingState.playerEmails) {
            assertThat(GameStateHolder.players.first { it.email == email }.cards.size, `is`(9))
        }

        assertThat(newBettingState.currentBetterEmail,
            `is`(bettingState.playerEmails[bettingState.playerEmails.indexOfFirst { it == bettingState.currentBetterEmail } + 1 % 4]))
        assertThat(newBettingState.currentBetterEmail,
            `is`(bettingState.playerEmails[bettingState.currentUserIdx + 1 % 4]))
    }

    // TODO: add more tests?
}