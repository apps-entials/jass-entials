package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.betting.BettingLogic
import com.github.lucaengel.jass_entials.game.betting.SchieberBettingLogic
import com.github.lucaengel.jass_entials.game.betting.SidiBarahniBettingLogic

/**
 * Represents the state of the betting phase of a game.
 *
 * @param currentUserId the index of the current user in the [playerEmails] list
 * @param playerEmails the list of all players in the game (in order)
 * @param currentBetter the player who is currently betting
 * @param jassType the type of the jass game
 * @param bets the list of all bets that have been placed
 * @param betActions the list of all actions that have been performed (bet or pass)
 * @param gameState the state of the game
 */
data class BettingState(
    val currentUserId: PlayerId,
    val playerEmails: List<String>,
    val currentBetterId: PlayerId,
    val startingBetterId: PlayerId,
    val jassType: JassType,
    val bets: List<Bet>,
    val betActions: List<Bet.BetAction>,
    val gameState: GameState,
){

    private val bettingLogic: BettingLogic = when (jassType) {
        JassType.SCHIEBER -> {
            SchieberBettingLogic()
        }

        else -> {
            SidiBarahniBettingLogic()
        }
    }

    constructor(): this(
        currentUserId = PlayerId.PLAYER_1,
        playerEmails = listOf(),
        currentBetterId = PlayerId.PLAYER_1,
        startingBetterId = PlayerId.PLAYER_1,
        jassType = JassType.SCHIEBER,
        bets = listOf(),
        betActions = listOf(),
        gameState = GameState(),
    )

    /**
     * Returns the new betting state for the next betting round.
     *
     * @param startingBetter the player who starts the next betting round
     * @return the new betting state
     */
    fun nextBettingRound(startingBetter: PlayerId, jassType: JassType = this.jassType): BettingState {
        val dealtCards = Deck.STANDARD_DECK.shuffled().dealCards()
        GameStateHolder.players = GameStateHolder.players.map { it.copy(cards = dealtCards[it.id]!!) }

        return this.copy(
            // TODO: make sure every player has a different email!!!
            currentUserId = PlayerId.PLAYER_1,
            currentBetterId = startingBetter,
            startingBetterId = startingBetter,
            jassType = jassType,
            bets = listOf(),
        )
    }

    /**
     * Returns the new betting state where the next player can place a bet
     *
     * @param placedBet the bet that has been placed by current player (null if no bet has been placed)
     * @return the new betting state
     */
    fun nextPlayer(placedBet: Bet? = null): BettingState {

        val nextBetterId = bettingLogic.nextPlayer(currentBetterId, placedBet, this)
        return this.copy(
            currentBetterId = nextBetterId,
            bets = if (placedBet != null) bets + placedBet else bets,
            betActions = if (placedBet != null) betActions + Bet.BetAction.BET else betActions + Bet.BetAction.PASS,
        )
    }

    fun availableActions(): List<Bet.BetAction> {
        return bettingLogic.availableActions(currentBetterId, this)
    }

    /**
     * Returns the available bets for the current player.
     *
     * @return the available bets
     */
    fun availableBets(): List<BetHeight> {
        return BetHeight.values().filter {
            it > (bets.lastOrNull()
                ?.bet
                ?: BetHeight.NONE) }
    }

    /**
     * Returns the available trumps for the current player.
     *
     * @return the available trumps
     */
    fun availableTrumps(): List<Trump> {
        if (bets.lastOrNull()?.playerId == currentBetterId) {
            return Trump.values().filter { it != bets.last().trump }
        }

        return Trump.values().toList()
    }

    /**
     * Returns the GameState corresponding to the last bet.
     * Called at the end of the betting phase.
     *
     * @return the new betting state
     */
    fun startGame(): GameState { // TODO: make sure to have the same random seed for every player!!!
        if (bets.isEmpty()) // TODO: handle this case better! (restart betting phase)
            throw IllegalStateException("Cannot start game without bets")

        return GameState(
            currentUserId = currentUserId,
            playerEmails = playerEmails,
            currentPlayerId = if (jassType == JassType.SCHIEBER) startingBetterId else bets.last().playerId,
            startingPlayerId = if (jassType == JassType.SCHIEBER) startingBetterId else bets.last().playerId,
            currentRound = 0,
            roundState = RoundState.initial(
                startingPlayerId = if (jassType == JassType.SCHIEBER) startingBetterId else bets.last().playerId,
                trump = bets.last().trump,
            ),
            winningBet = bets.last(),
            playerCards = GameStateHolder.players.associate { it.id to it.cards },
        )
    }
}

/**
 * Represents a bet that has been placed by a player.
 *
 * @param playerId the player who placed the bet
 * @param trump the selected trump suit for the bet
 * @param bet the bet height
 */
data class Bet(val playerId: PlayerId, val trump: Trump, val bet: BetHeight) {

    constructor(): this(PlayerId.PLAYER_1, Trump.HEARTS, BetHeight.NONE)

    /**
     * Represents the action of a player during the betting phase.
     */
    enum class BetAction {
        BET,
        PASS,
        START_GAME,
        DOUBLE,
    }

    override fun toString(): String {
        val player = GameStateHolder.players.first { it.id == playerId }
        return "$trump${if (bet == BetHeight.NONE) "" else bet} by ${player.firstName} ${player.lastName}"
    }
}


/**
 * Represents the height of a bet.
 *
 * @param value the points the bet is worth
 */
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
        /**
         * Returns the BetHeight corresponding to the given string.
         *
         * @param string the string to convert
         * @return the corresponding BetHeight
         */
        fun fromString(string: String): BetHeight {
            return when (string) {
                "no bet" -> NONE
                "match" -> MATCH
                else -> BetHeight.values().first { it.value == string.toInt() }
            }
        }
    }
}
