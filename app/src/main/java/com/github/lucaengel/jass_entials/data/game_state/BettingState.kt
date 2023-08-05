package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * Represents the state of the betting phase of a game.
 *
 * @param currentPlayerIdx the index of the current user in the [playerEmails] list
 * @param playerEmails the list of all players in the game (in order)
 * @param currentBetterEmail the player who is currently betting
 * @param jassType the type of the jass game
 * @param bets the list of all bets that have been placed
 * @param gameState the state of the game
 */
data class BettingState(
    val currentPlayerIdx: Int,
    val playerEmails: List<String>,
    val currentBetterEmail: String,
    val jassType: JassType,
    val bets: List<Bet>,
    val gameState: GameState,
){

    constructor(): this(
        0,
        playerEmails = listOf(),
        currentBetterEmail = "",
        jassType = JassType.SCHIEBER,
        bets = listOf(),
        gameState = GameState(),
    )

    /**
     * Returns the new betting state for the next betting round.
     *
     * @param startingBetterEmail the player who starts the next betting round
     * @return the new betting state
     */
    fun nextBettingRound(startingBetterEmail: String): BettingState {
        val dealtCards = Deck.STANDARD_DECK.shuffled().dealCards(playerEmails)
        GameStateHolder.players = GameStateHolder.players.map { it.copy(cards = dealtCards[it.email]!!) }

        return this.copy(
            // TODO: make sure every player has a different email!!!
            currentPlayerIdx = playerEmails.indexOfFirst { it == startingBetterEmail },
            currentBetterEmail = startingBetterEmail,
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

        val nextPlayerIdx = (playerEmails.indexOf(currentBetterEmail) + 1) % playerEmails.size
        return this.copy(
            currentBetterEmail = playerEmails[nextPlayerIdx],
            bets = if (placedBet != null) bets + placedBet else bets,
        )
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
            currentPlayerIdx = currentPlayerIdx,
            playerEmails = playerEmails,
            currentPlayerEmail = bets.last().playerEmail,
            startingPlayerEmail = bets.last().playerEmail,
            currentRound = 0,
            currentTrick = Trick(),
            currentRoundTrickWinners = listOf(),
            currentTrickNumber = 0,
            currentTrump = bets.last().suit,
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
data class Bet(val playerEmail: String, val suit: Trump, val bet: BetHeight)

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
