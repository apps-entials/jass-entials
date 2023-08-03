package com.github.lucaengel.jass_entials.data.game_state

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class GameStateHolderTest {

    @Test
    fun goToNextBettingStateRoundDeals9CardsToEachPlayer() {
        val bettingState = GameStateHolder.bettingState

        GameStateHolder.goToNextBettingStateRound(bettingState.playerDatas[bettingState.currentPlayerIdx + 1 % 4])

        val newBettingState = GameStateHolder.bettingState
        for (playerData in newBettingState.playerDatas) {
            assertThat(playerData.cards.size, `is`(9))
        }

        assertThat(newBettingState.currentPlayerIdx,
            `is`(bettingState.currentPlayerIdx + 1 % 4))
        assertThat(newBettingState.currentBetter.email,
            `is`(bettingState.playerDatas[bettingState.currentPlayerIdx + 1 % 4].email))
    }

    // TODO: add more tests?
}