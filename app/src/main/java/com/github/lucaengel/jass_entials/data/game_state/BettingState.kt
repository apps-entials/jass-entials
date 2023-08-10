package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.betting.BettingLogic
import com.github.lucaengel.jass_entials.game.betting.SchieberBettingLogic
import com.github.lucaengel.jass_entials.game.betting.SidiBarahniBettingLogic

/**
 * Represents the state of the betting phase of a game.
 *
 * @param currentUserIdx the index of the current user in the [playerEmails] list
 * @param playerEmails the list of all players in the game (in order)
 * @param currentBetterEmail the player who is currently betting
 * @param jassType the type of the jass game
 * @param bets the list of all bets that have been placed
 * @param betActions the list of all actions that have been performed (bet or pass)
 * @param gameState the state of the game
 */
data class BettingState(
    val currentUserIdx: Int,
    val playerEmails: List<String>,
    val currentBetterEmail: String,
    val startingBetterEmail: String,
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
        currentUserIdx = 0,
        playerEmails = listOf(),
        currentBetterEmail = "",
        startingBetterEmail = "",
        jassType = JassType.SCHIEBER,
        bets = listOf(),
        betActions = listOf(),
        gameState = GameState(),
    )

    /**
     * Returns the new betting state for the next betting round.
     *
     * @param startingBetterEmail the player who starts the next betting round
     * @return the new betting state
     */
    fun nextBettingRound(startingBetterEmail: String, jassType: JassType = this.jassType): BettingState {
        val dealtCards = Deck.STANDARD_DECK.shuffled().dealCards(playerEmails)
        GameStateHolder.players = GameStateHolder.players.map { it.copy(cards = dealtCards[it.email]!!) }

        return this.copy(
            // TODO: make sure every player has a different email!!!
            currentUserIdx = 0,
            currentBetterEmail = startingBetterEmail,
            startingBetterEmail = startingBetterEmail,
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

        val nextBetterEmail = bettingLogic.nextPlayer(currentBetterEmail, placedBet, this)
        return this.copy(
            currentBetterEmail = nextBetterEmail,
            bets = if (placedBet != null) bets + placedBet else bets,
            betActions = if (placedBet != null) betActions + Bet.BetAction.BET else betActions + Bet.BetAction.PASS,
        )
    }

    fun availableActions(
        bettingState: BettingState
    ): List<Bet.BetAction> {
        return bettingLogic.availableActions(bettingState.currentBetterEmail, this)
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
        if (bets.lastOrNull()?.playerEmail == currentBetterEmail) {
            return Trump.values().filter { it != bets.last().suit }
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
            currentUserIdx = currentUserIdx,
            playerEmails = playerEmails,
            currentPlayerEmail = bets.last().playerEmail,
            startingPlayerEmail = bets.last().playerEmail,
            currentRound = 0,
            currentTrick = Trick(),
            currentRoundTrickWinners = listOf(),
            currentTrickNumber = 0,
            currentTrump = bets.last().suit,
            winningBet = bets.last(),
            playerCards = GameStateHolder.players.associate { it.email to it.cards },
        )
    }
}

/**
 * Represents a bet that has been placed by a player.
 *
 * @param playerEmail the player who placed the bet
 * @param suit the selected trump suit for the bet
 * @param bet the bet height
 */
data class Bet(val playerEmail: String, val suit: Trump, val bet: BetHeight) {

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
        val player = GameStateHolder.players.first { it.email == playerEmail }
        return "$suit${if (bet == BetHeight.NONE) "" else bet} by ${player.firstName} ${player.lastName}"
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
