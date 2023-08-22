package com.github.lucaengel.jass_entials.data.game_state

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class ScoreTest {

    @Test
    fun initialInitializesEverythingTo0() {
        assertThat(Score.INITIAL.roundPoints(TeamId.TEAM_1), `is`(0))
        assertThat(Score.INITIAL.roundPoints(TeamId.TEAM_2), `is`(0))
        assertThat(Score.INITIAL.gamePoints(TeamId.TEAM_1), `is`(0))
        assertThat(Score.INITIAL.gamePoints(TeamId.TEAM_2), `is`(0))
    }

    @Test
    fun withPointsAddedAddsThePointsToTheCorrectTeam() {
        val score = Score.INITIAL.withPointsAdded(TeamId.TEAM_1, 100)
        assertThat(score.roundPoints(TeamId.TEAM_1), `is`(100))
        assertThat(score.roundPoints(TeamId.TEAM_2), `is`(0))

        val score2 = score.withPointsAdded(TeamId.TEAM_2, 200)
        assertThat(score2.roundPoints(TeamId.TEAM_1), `is`(100))
        assertThat(score2.roundPoints(TeamId.TEAM_2), `is`(200))
    }

    @Test
    fun addRoundPointsToGamePointsDoesThat() {
        val score = Score.INITIAL
            .withPointsAdded(TeamId.TEAM_1, 100)
            .withPointsAdded(TeamId.TEAM_2, 200)

        assertThat(score.roundPoints(TeamId.TEAM_1), `is`(100))
        assertThat(score.roundPoints(TeamId.TEAM_2), `is`(200))
        assertThat(score.gamePoints(TeamId.TEAM_1), `is`(100))
        assertThat(score.gamePoints(TeamId.TEAM_2), `is`(200))
    }

    @Test
    fun nextRoundResetsRoundPoints() {
        val score = Score.INITIAL
            .withPointsAdded(TeamId.TEAM_1, 100)
            .withPointsAdded(TeamId.TEAM_2, 200)
            .nextRound()

        assertThat(score.roundPoints(TeamId.TEAM_1), `is`(0))
        assertThat(score.roundPoints(TeamId.TEAM_2), `is`(0))
    }
}