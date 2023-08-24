package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

/**
 * Represents the state of a game.
 *
 * @property currentUserId the index of the current user in the [playerEmails] list
 * @property playerEmails the list of all players
 * @property currentPlayerId the id of the player that has to play the next card
 * @property startingPlayerId the id of the player that started the current trick
 * @property currentRound the current round number
 * @property playerCards the map of player data to their cards
 */
data class GameState(
    val currentUserId: PlayerId,
    val playerEmails: List<String>,
    val currentPlayerId: PlayerId, // player that has to play the next card
    val startingPlayerId: PlayerId, // player that started the current trick
    val currentRound: Int,
    val jassType: JassType,
    val roundState: RoundState,
    val winningBet: Bet,
    val playerCards: Map<PlayerId, List<Card>>,
) : Serializable {

    constructor() : this(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentPlayerId = PlayerId.PLAYER_1,
        startingPlayerId = PlayerId.PLAYER_1,
        currentRound = 0,
        jassType = JassType.SIDI_BARRANI,
        roundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1),
        winningBet = Bet(PlayerId.PLAYER_1, Trump.CLUBS, BetHeight.NONE),
        playerCards = mapOf(),
    )

    /**
     * Returns true iff it is the last trick of the round (i.e., everyone has <= 1 card left).
     *
     * @return true iff it is the last trick of the round
     */
    fun isLastTrick(): Boolean {
        return roundState.isRoundOver()
    }

    /**
     * Updates the winner of the played trick, moves on to the next trick and returns the new game state.
     *
     * @return the new game state
     */
    fun nextTrick(): GameState {
        val newRoundState = roundState.withTrickCollected()
        return this.copy(
            roundState = newRoundState,
            startingPlayerId = newRoundState.trick().startingPlayerId,
            currentPlayerId = newRoundState.trick().startingPlayerId,
        )
    }

    /**
     * Returns the updated game state after the given player played the given card.
     *
     * @param playerId the player data of the player that played the card
     * @param card the card that was played
     * @return the updated game state
     */
    fun playCard(playerId: PlayerId, card: Card): GameState {
        // TODO: maybe update playerData as well
        val newPlayer = GameStateHolder.players[currentPlayerId.ordinal].withCardPlayed(card)

        val newGameState = this.copy(
            roundState = roundState.withCardPlayed(card),
            playerCards = playerCards.plus(playerId to newPlayer.cards),
        )

        return if (newGameState.roundState.trick().isFull()) {
            // if full, wait for the current user to click it away
            newGameState.copy(currentPlayerId = this.currentUserId)
        } else {
            newGameState.copy(currentPlayerId = playerId.nextPlayer())
        }
    }
}