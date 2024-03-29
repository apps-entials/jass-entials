package com.github.lucaengel.jass_entials.data.game_state

enum class TeamId {
    TEAM_1,
    TEAM_2;

    fun otherTeam(): TeamId {
        return when (this) {
            TEAM_1 -> TEAM_2
            TEAM_2 -> TEAM_1
        }
    }

    override fun toString(): String {
        return if (this == GameStateHolder.gameState.currentUserId.teamId()) {
            "Your Team"
        } else {
            "Other Team"
        }
    }
}