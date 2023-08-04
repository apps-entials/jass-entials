package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable
import java.lang.IllegalStateException

/**
 * Represents the state of a game.
 *
 * @property currentPlayerIdx the index of the current user in the [playerDatas] list
 * @property playerDatas the list of all players
 * @property currentPlayerData the data of the player that has to play the next card
 * @property startingPlayerData the data of the player that started the current trick
 * @property currentRound the current round number
 * @property currentTrick the current trick
 * @property currentRoundTrickWinners the list of pairs of player data and tricks that were won by the players in the previous tricks of the current round
 * @property currentTrickNumber the current trick number
 * @property currentTrump the current trump
 * @property playerCards the map of player data to their cards
 */
data class GameState(
    val currentPlayerIdx: Int,
    val playerDatas: List<PlayerData> = listOf(),
    val currentPlayerData: PlayerData, // player that has to play the next card
    val startingPlayerData: PlayerData, // player that started the current trick
    val currentRound: Int,
    val currentTrick: Trick = Trick(),
    val currentRoundTrickWinners: List<Pair<PlayerData, Trick>> = listOf(),
    val currentTrickNumber: Int = 0,
    val currentTrump: Trump = Trump.CLUBS,
    val playerCards: Map<PlayerData, List<Card>> = mapOf(),
) : Serializable {

    constructor() : this(
        currentPlayerIdx = 0,
        playerDatas = listOf(),
        currentPlayerData = PlayerData(),
        startingPlayerData = PlayerData(),
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
            currentRoundTrickWinners = currentRoundTrickWinners + (currentTrick.winner(trump = currentTrump) to currentTrick),
            //TODO: adapt currentPlayer to be the one who won this round!!!
        )
    }

    /**
     * Calculates the points of the given player.
     *
     * @param playerData the player data whose points are to be calculated
     * @return the points of the given player
     */
    fun points(playerData: PlayerData): Int {

        // todo: do something about the emails that are not different for guests!!!
        // TODO: get rid of such magic numbers!!!
        val lastTrickBonus = if (currentTrickNumber >= 9 && currentRoundTrickWinners.last().first.email == playerData.email) 5 else 0

        return lastTrickBonus + currentRoundTrickWinners
            .filter { it.first.email == playerData.email }
            .sumOf { (_, trick) -> trick.playerToCard.sumOf { it.first.points(currentTrump) } }
    }

    /**
     * Returns the updated game state after the given player played the given card.
     *
     * @param playerData the player data of the player that played the card
     * @param card the card that was played
     * @return the updated game state
     */
    fun playCard(playerData: PlayerData, card: Card): GameState {

        val newPlayer = playerData.copy(cards = playerData.cards.filter { c -> c != card })
        return this.copy(
            currentTrick = currentTrick.copy(playerToCard = currentTrick.playerToCard + (card to playerData)),
            playerDatas = playerDatas.map { if (it == playerData) newPlayer else it },
            playerCards = playerCards.map { if (it.key == playerData) newPlayer to newPlayer.cards else it.key to it.value }.toMap(),
        )
    }
}