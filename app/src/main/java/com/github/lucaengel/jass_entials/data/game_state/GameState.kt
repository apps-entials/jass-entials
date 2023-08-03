package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable
import java.lang.IllegalStateException

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
        )
    }

    fun points(playerData: PlayerData): Int {

        // todo: do something about the emails that are not different for guests!!!
        // TODO: get rid of such magic numbers!!!
        val lastTrickBonus = if (currentTrickNumber >= 9 && currentRoundTrickWinners.last().first.email == playerData.email) 5 else 0

        return lastTrickBonus + currentRoundTrickWinners
            .filter { it.first.email == playerData.email }
            .sumOf { (_, trick) -> trick.playerToCard.sumOf { it.first.points(currentTrump) } }
    }

    fun playCard(playerData: PlayerData, card: Card): GameState {

        val newPlayer = playerData.copy(cards = playerData.cards.filter { c -> c != card })
        return this.copy(
            currentTrick = currentTrick.copy(playerToCard = currentTrick.playerToCard + (card to playerData)),
            playerDatas = playerDatas.map { if (it == playerData) newPlayer else it },
            playerCards = playerCards.map { if (it.key == playerData) newPlayer to newPlayer.cards else it.key to it.value }.toMap(),
        )
    }
}