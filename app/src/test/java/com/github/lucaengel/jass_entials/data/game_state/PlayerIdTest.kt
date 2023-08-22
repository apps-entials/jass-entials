package com.github.lucaengel.jass_entials.data.game_state

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class PlayerIdTest {

    @Test
    fun teamReturnsCorrectTeam() {
        assertThat(PlayerId.PLAYER_1.teamId(), `is`(TeamId.TEAM_1))
        assertThat(PlayerId.PLAYER_2.teamId(), `is`(TeamId.TEAM_2))
        assertThat(PlayerId.PLAYER_3.teamId(), `is`(TeamId.TEAM_1))
        assertThat(PlayerId.PLAYER_4.teamId(), `is`(TeamId.TEAM_2))
    }

    @Test
    fun playerAtPositionReturnsCorrectPlayers() {
        assertThat(PlayerId.PLAYER_1.playerAtPosition(0), `is`(PlayerId.PLAYER_1))
        assertThat(PlayerId.PLAYER_1.playerAtPosition(1), `is`(PlayerId.PLAYER_2))
        assertThat(PlayerId.PLAYER_1.playerAtPosition(2), `is`(PlayerId.PLAYER_3))
        assertThat(PlayerId.PLAYER_1.playerAtPosition(3), `is`(PlayerId.PLAYER_4))
    }

    @Test
    fun teamMateReturnsPlayer2PositionsFurther() {
        assertThat(PlayerId.PLAYER_1.teamMate(), `is`(PlayerId.PLAYER_3))
        assertThat(PlayerId.PLAYER_2.teamMate(), `is`(PlayerId.PLAYER_4))
        assertThat(PlayerId.PLAYER_3.teamMate(), `is`(PlayerId.PLAYER_1))
        assertThat(PlayerId.PLAYER_4.teamMate(), `is`(PlayerId.PLAYER_2))
    }

    @Test
    fun nextPlayerReturnsPlayerOnTheRight() {
        assertThat(PlayerId.PLAYER_1.nextPlayer(), `is`(PlayerId.PLAYER_2))
        assertThat(PlayerId.PLAYER_2.nextPlayer(), `is`(PlayerId.PLAYER_3))
        assertThat(PlayerId.PLAYER_3.nextPlayer(), `is`(PlayerId.PLAYER_4))
        assertThat(PlayerId.PLAYER_4.nextPlayer(), `is`(PlayerId.PLAYER_1))
    }

    @Test
    fun trickPositionReturnsPlayersPositionInTheTrick() {
        assertThat(PlayerId.PLAYER_1.trickPosition(PlayerId.PLAYER_1), `is`(0))
        assertThat(PlayerId.PLAYER_1.trickPosition(PlayerId.PLAYER_2), `is`(3))
        assertThat(PlayerId.PLAYER_1.trickPosition(PlayerId.PLAYER_3), `is`(2))
        assertThat(PlayerId.PLAYER_1.trickPosition(PlayerId.PLAYER_4), `is`(1))
    }
}