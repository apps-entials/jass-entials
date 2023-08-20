package com.github.lucaengel.jass_entials.data.game_state

/**
 * Represents the score of a game.
 *
 * @param team1 The points of team 1 of the current round.
 * @param team2 The points of team 2 of the current round.
 * @param gamePointsTeam1 The points of team 1 in the current game.
 * @param gamePointsTeam2 The points of team 2 in the current game.
 */
data class Score(
    private val team1: Int,
    private val team2: Int,
    private val gamePointsTeam1: Int,
    private val gamePointsTeam2: Int,
) {

    /**
     * Returns the points of the given team in the current round.
     *
     * @param teamId The id of the team.
     */
    fun roundPoints(teamId: TeamId): Int {
        return if (teamId == TeamId.TEAM_1) {
            this.team1
        } else {
            this.team2
        }
    }

    /**
     * Returns the points of the given team in the current game.
     *
     * @param teamId The id of the team.
     */
    fun gamePoints(teamId: TeamId): Int {
        return if (teamId == TeamId.TEAM_1) {
            this.gamePointsTeam1
        } else {
            this.gamePointsTeam2
        }
    }

    /**
     * Returns the points of the given team in the current game.
     *
     * @param teamId The id of the team.
     * @param points The points to add.
     */
    fun withPointsAdded(teamId: TeamId, points: Int): Score {
        return if (teamId == TeamId.TEAM_1) {
            this.copy(
                team1 = this.team1 + points,
                gamePointsTeam1 = this.gamePointsTeam1 + points
            )
        } else{
            this.copy(
                team2 = this.team2 + points,
                gamePointsTeam2 = this.gamePointsTeam2 + points
            )
        }
    }

    /**
     * Returns the Score of the next round.
     *
     * @return The Score of the next round.
     */
    fun nextRound(): Score {
        return this.copy(
            team1 = 0,
            team2 = 0
        )
    }

//    /**
//     * Returns the Score of the next round with the current points added to the game points.
//     *
//     * @return The Score of the next round.
//     */
//    fun nextRound(): Score {
//        return this.copy(
//            team1 = 0,
//            team2 = 0,
//            gamePointsTeam1 = this.gamePointsTeam1 + this.team1,
//            gamePointsTeam2 = this.gamePointsTeam2 + this.team2
//        )
//    }

    companion object {

        /**
         * The initial score.
         */
        val INITIAL = Score(
            team1 = 0,
            team2 = 0,
            gamePointsTeam1 = 0,
            gamePointsTeam2 = 0
        )
    }
}
