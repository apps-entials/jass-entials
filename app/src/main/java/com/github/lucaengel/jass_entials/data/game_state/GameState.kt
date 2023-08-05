package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

/**
 * Represents the state of a game.
 *
 * @property currentPlayerIdx the index of the current user in the [playerEmails] list
 * @property playerEmails the list of all players
 * @property currentPlayerEmail the data of the player that has to play the next card
 * @property startingPlayerEmail the data of the player that started the current trick
 * @property currentRound the current round number
 * @property currentTrick the current trick
 * @property currentRoundTrickWinners the list of pairs of player data and tricks that were won by the players in the previous tricks of the current round
 * @property currentTrickNumber the current trick number
 * @property currentTrump the current trump
 * @property playerCards the map of player data to their cards
 */
data class GameState(
    val currentPlayerIdx: Int,
    val playerEmails: List<String> = listOf(),
    val currentPlayerEmail: String, // player that has to play the next card
    val startingPlayerEmail: String, // player that started the current trick
    val currentRound: Int,
    val currentTrick: Trick = Trick(),
    val currentRoundTrickWinners: List<Trick.TrickWinner> = listOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Trump = Trump.CLUBS,
    val playerCards: Map<String, List<Card>> = mapOf(),
) : Serializable {

    constructor() : this(
        currentPlayerIdx = 0,
        playerEmails = listOf(),
        currentPlayerEmail = "",
        startingPlayerEmail = "",
        currentRound = 0,
        currentTrick = Trick(),
        currentRoundTrickWinners = listOf(),
        currentTrickNumber = 0,
        currentTrump = Trump.CLUBS,
        playerCards = mapOf(),
    )

    /**
     * Returns true iff it is the last trick of the round (i.e., everyone has <= 1 card left).
     *
     * @return true iff it is the last trick of the round
     */
    fun isLastTrick(): Boolean {
        return currentTrickNumber == 9
    }

    /**
     * Updates the winner of the played trick, moves on to the next trick and returns the new game state.
     *
     * @return the new game state
     */
    fun nextTrick(): GameState {
        if (!currentTrick.isFull())
            throw IllegalStateException("Cannot move on to the next trick if the current trick is not full.")

        return this.copy(
            currentTrick = Trick(),
            currentTrickNumber = currentTrickNumber + 1,
            currentRoundTrickWinners = currentRoundTrickWinners + (currentTrick.winner(trump = currentTrump)),
            //TODO: adapt currentPlayer to be the one who won this round!!!
        )
    }

    /**
     * Calculates the points of the given player.
     *
     * @param playerEmail the player data whose points are to be calculated
     * @return the points of the given player
     */
    fun points(playerEmail: String): Int {

        // todo: do something about the emails that are not different for guests!!!
        // TODO: get rid of such magic numbers!!!
        val lastTrickBonus = if (currentTrickNumber >= 9 && currentRoundTrickWinners.last().playerEmail == playerEmail) 5 else 0

        return lastTrickBonus + currentRoundTrickWinners
            .filter { it.playerEmail == playerEmail }
            .sumOf { trickWinner -> trickWinner.trick.points(trump = currentTrump) }
    }

    /**
     * Returns the updated game state after the given player played the given card.
     *
     * @param playerEmail the player data of the player that played the card
     * @param card the card that was played
     * @return the updated game state
     */
    fun playCard(playerEmail: String, card: Card): GameState {
        val idx = GameStateHolder.players.indexOfFirst { it.email == playerEmail }

        if (idx == -1) throw IllegalArgumentException("Player $playerEmail is not in the game!")

        // update GameStateHolder.players at position idx
        val newPlayer = GameStateHolder.players[idx].withCardPlayed(card)

        GameStateHolder.players = GameStateHolder.players.map { if (it.email == playerEmail) newPlayer else it }

        return this.copy(
            currentTrick = currentTrick.copy(trickCards = currentTrick.trickCards + Trick.TrickCard(card, playerEmail)),
            playerCards = playerCards.plus(playerEmail to newPlayer.cards),
        )
    }
}