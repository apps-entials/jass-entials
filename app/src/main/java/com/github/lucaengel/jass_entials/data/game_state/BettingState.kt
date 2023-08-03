package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump

data class BettingState(
    val currentPlayerIdx: Int,
    val playerDatas: List<PlayerData>,
    val currentBetter: PlayerData,
    val jassType: JassType,
    val bets: List<Bet>,
    val gameState: GameState,
){

    constructor(): this(
        0,
        playerDatas = listOf(PlayerData(), PlayerData(), PlayerData(), PlayerData()),
        currentBetter = PlayerData(),
        jassType = JassType.SCHIEBER,
        bets = listOf(),
        gameState = GameState(),
    )

    fun nextBettingRound(startingBetter: PlayerData): BettingState {
        val newPlayers = Deck.STANDARD_DECK.shuffled().dealCards(playerDatas).keys

        return this.copy(
            // TODO: make sure every player has a different email!!!
            currentPlayerIdx = newPlayers.indexOfFirst { it.email == startingBetter.email },
            playerDatas = newPlayers.toList(),
            currentBetter = newPlayers.first { it.email == startingBetter.email },
            bets = listOf(),
        )
    }

    fun nextPlayer(placedBet: Bet? = null): BettingState {

        val nextPlayerIdx = (playerDatas.indexOf(currentBetter) + 1) % playerDatas.size
        return this.copy(
            currentBetter = playerDatas[nextPlayerIdx],
            bets = if (placedBet != null) bets + placedBet else bets,
        )
    }

    fun availableBets(): List<BetHeight> {
        return BetHeight.values().filter { it > (bets.lastOrNull()?.bet ?: BetHeight.NONE) }
    }

    fun availableTrumps(): List<Trump> {
        if (bets.lastOrNull()?.playerData == currentBetter) {
            return Trump.values().filter { it != bets.last().suit }
        }

        return Trump.values().toList()
    }

    fun startGame(currentPlayerIdx: Int): GameState { // TODO: make sure to have the same random seed for every player!!!
        if (bets.isEmpty()) // TODO: handle this case better! (restart betting phase)
            throw IllegalStateException("Cannot start game without bets")

        return GameState(
            currentPlayerIdx = currentPlayerIdx,
            playerDatas = playerDatas,
            currentPlayerData = bets.last().playerData,
            startingPlayerData = bets.last().playerData,
            currentRound = 0,
            currentTrick = Trick(),
            currentTrickNumber = 0,
            currentTrump = bets.last().suit,
            playerCards = playerDatas.associateWith { it.cards },
        )
    }
}

data class Bet(val playerData: PlayerData, val suit: Trump, val bet: BetHeight)

enum class BetHeight(private val value: Int) {
    NONE(0),
    FORTY(40),
    FIFTY(50),
    SIXTY(60),
    SEVENTY(70),
    EIGHTY(80),
    NINETY(90),
    HUNDRED(100),
    HUNDRED_TEN(110),
    HUNDRED_TWENTY(120),
    HUNDRED_THIRTY(130),
    HUNDRED_FORTY(140),
    HUNDRED_FIFTY(150),
    HUNDRED_FIFTY_SEVEN(157),
    MATCH(257);

    override fun toString(): String {
        return when (this) {
            NONE -> "no bet"
            MATCH -> "match"
            else -> "$value"
        }
    }

    companion object {
        fun fromString(string: String): BetHeight {
            return when (string) {
                "no bet" -> NONE
                "match" -> MATCH
                else -> BetHeight.values().first { it.value == string.toInt() }
            }
        }
    }
}
