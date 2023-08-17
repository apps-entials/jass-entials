package com.github.lucaengel.jass_entials.data.game_state

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class TeamIdTest {

    @Test
    fun otherTeamReturnsOtherTeam() {
        assertThat(TeamId.TEAM_1.otherTeam(), `is`(TeamId.TEAM_2))
        assertThat(TeamId.TEAM_2.otherTeam(), `is`(TeamId.TEAM_1))
    }
}