package com.github.lucaengel.jass_entials.data.game_state

enum class PlayerId {
    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;

    /**
     * Returns the team id of the current player.
     *
     * @return The team id of the current player.
     */
    fun team(): TeamId {
        return when (this) {
            PLAYER_1, PLAYER_3 -> TeamId.TEAM_1
            PLAYER_2, PLAYER_4 -> TeamId.TEAM_2
        }
    }

    /**
     * Returns the player [positions] to the right of the current player.
     *
     * @param positions The number of positions to the right.
     * @return The player [positions] to the right of the current player.
     */
    fun nextPlayer(positions: Int): PlayerId {
        return PlayerId.values()[(this.ordinal + positions) % PlayerId.values().size]
    }

    /**
     * Returns the player id of the team mate.
     *
     * @return The player id of the team mate.
     */
    fun teamMate(): PlayerId {
        return PlayerId.values()[(this.ordinal + 2) % PlayerId.values().size]
    }

    /**
     * Returns the next player (to the right).
     *
     * @return The next player.
     */
    fun nextPlayer(): PlayerId {
        return PlayerId.values()[(this.ordinal + 1) % PlayerId.values().size]
    }

    /**
     * Returns the players position in the trick.
     *
     * @param startingPlayerId The id of the player who started the trick.
     * @return The position of the player in the trick.
     */
    fun trickPosition(startingPlayerId: PlayerId): Int {
        return Math.floorMod(this.ordinal - startingPlayerId.ordinal, PlayerId.values().size)
    }
}