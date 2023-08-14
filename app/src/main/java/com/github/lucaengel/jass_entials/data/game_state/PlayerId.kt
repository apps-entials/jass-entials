package com.github.lucaengel.jass_entials.data.game_state

enum class PlayerId {
    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;

    fun team(): TeamId {
        return when (this) {
            PLAYER_1, PLAYER_3 -> TeamId.TEAM_1
            PLAYER_2, PLAYER_4 -> TeamId.TEAM_2
        }
    }

    fun teamMate(): PlayerId {
        return PlayerId.values()[(this.ordinal + 2) % PlayerId.values().size]
    }

    /**
     * Returns the next player (to the right).
     */
    fun nextPlayer(): PlayerId {
        return PlayerId.values()[(this.ordinal + 1) % PlayerId.values().size]
    }
}