package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.player.Player
import java.io.Serializable

data class GameState(
    val currentPlayerIdx: Int,
    val playerDatas: List<PlayerData> = listOf(),
    val currentPlayerData: PlayerData, // player that has to play the next card
    val startingPlayerData: PlayerData, // player that started the current trick
    val currentRound: Int,
    val currentTrick: Trick = Trick(),
    val currentRoundTrickWinners: Map<PlayerData, Trick> = mapOf(),
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
        currentRoundTrickWinners = mapOf(),
        currentTrickNumber = 0,
        currentTrump = Trump.CLUBS,
        playerCards = mapOf(),
    )

    fun isLastTrick(): Boolean {
        return currentTrickNumber == 9
    }

    fun nextTrick(): GameState {
        if (isLastTrick()) {
            return this.copy(
                currentTrick = Trick(),
                currentRoundTrickWinners = currentRoundTrickWinners + (currentTrick.winner(trump = currentTrump) to currentTrick),
            )
        }

        return this.copy(
            currentTrick = Trick(),
            currentTrickNumber = currentTrickNumber + 1,
            currentRoundTrickWinners = currentRoundTrickWinners + (currentTrick.winner(trump = currentTrump) to currentTrick),
        )
    }

    private fun nextRound(): GameState {
        return this.copy(
            currentRound = currentRound + 1,
            playerCards = Deck.STANDARD_DECK.shuffled().dealCards(playerDatas),
        )
    }

    fun playCard(playerData: PlayerData, card: Card): GameState {

        val newPlayer = playerData.copy(cards = playerData.cards.filter { c -> c != card })
        return this.copy(
            currentTrick = currentTrick.copy(playerToCard = currentTrick.playerToCard + (card to playerData)),
            playerDatas = playerDatas.map { if (it == playerData) newPlayer else it },
            playerCards = playerCards.map { if (it.key == playerData) newPlayer to newPlayer.cards else it.key to it.value }.toMap(),
        )
    }

    fun setTrump(trump: Trump): GameState {
        return this.copy(
            currentTrump = trump,
        )
    }
}