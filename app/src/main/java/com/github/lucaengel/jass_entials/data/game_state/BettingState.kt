package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.betting.BettingLogic
import com.github.lucaengel.jass_entials.game.betting.CoiffeurBettingLogic
import com.github.lucaengel.jass_entials.game.betting.SchieberBettingLogic
import com.github.lucaengel.jass_entials.game.betting.SidiBarraniBettingLogic

/**
 * Represents the state of the betting phase of a game.
 *
 * @param currentUserId the index of the current user in the [playerEmails] list
 * @param playerEmails the list of all players in the game (in order)
 * @param currentBetterId the player who is currently betting
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
    val score: Score,
    val cardDistributionsHandler: CardDistributionsHandler = CardDistributionsHandler()
){

    private val bettingLogic: BettingLogic = when (jassType) {
        JassType.SCHIEBER -> {
            SchieberBettingLogic()
        }

        JassType.COIFFEUR -> {
            CoiffeurBettingLogic()
        }

        JassType.SIDI_BARRANI -> {
            SidiBarraniBettingLogic()
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
        score = Score.INITIAL,
    )

    /**
     * Returns the new betting state for the next betting round.
     *
     * @param startingBetter the player who starts the next betting round
     * @return the new betting state
     */
    fun nextBettingRound(startingBetter: PlayerId, jassType: JassType = this.jassType, score: Score): BettingState {
        val dealtCards = Deck.STANDARD_DECK.shuffled().dealCards()
        GameStateHolder.players = GameStateHolder.players.map { it.copy(cards = dealtCards[it.id]!!) }

        return this.copy(
            // TODO: make sure every player has a different email!!!
            currentUserId = PlayerId.PLAYER_1,
            currentBetterId = startingBetter,
            startingBetterId = startingBetter,
            jassType = jassType,
            bets = listOf(),
            betActions = listOf(),
            score = score,
            cardDistributionsHandler = CardDistributionsHandler()
        )
    }

    fun withBetDoubled(doubledBet: Bet, doubledBy: PlayerId): BettingState? {
        if (bets.lastOrNull() != doubledBet) return null

        return this.copy(
            bets = bets.map { if (it == doubledBet) it.copy(doubledBy = doubledBy) else it }
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

//        // todo: not the nicest solution ever, maybe find a more abstract way to do this
//        if (placedBet != null && jassType == JassType.SIDI_BARRANI) {
//            // analyze who has which cards: only need last 4 bets (the ones before have already been analyzed)
//            val nbBetsInLastPass = newState.betActions.takeLast(3).count { it == Bet.BetAction.BET }
//            SidiBarraniBiddingCpu.extractKnowledgeFromBets(nbBetsInLastPass, bets)
//        }

        return copy(
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
        return bettingLogic.availableTrumps(currentBetterId, bets)
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

        val winningBet = bets.last()
        val winningTeam = winningBet.playerId.teamId()
        val startingPlayer = bettingLogic.gameStartingPlayerId(startingBetterId, winningBet)

        GameStateHolder.prevTrumpsByTeam += winningTeam to (GameStateHolder.prevTrumpsByTeam[winningBet.playerId.teamId()]?.plus(
            winningBet.trump
        ) ?: setOf(winningBet.trump))

        return GameState(
            currentUserId = currentUserId,
            playerEmails = playerEmails,
            currentPlayerId = startingPlayer,
            startingPlayerId = startingPlayer,
            currentRound = 0,
            jassType = jassType,
            roundState = RoundState.initial(
                trump = winningBet.trump,
                startingPlayerId = startingPlayer,
                score = score,
                cardDistributionsHandler = cardDistributionsHandler,
            ),
            winningBet = winningBet,
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
data class Bet(
    val playerId: PlayerId,
    val trump: Trump,
    val bet: BetHeight,
    val doubledBy: PlayerId? = null
) {

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
        val betString = "$trump${if (bet == BetHeight.NONE) "" else bet} by ${player.firstName} ${player.lastName}"
        return if (doubledBy != null) "$betString (doubled by ${GameStateHolder.players[doubledBy.ordinal].firstName})" else betString
    }
}


/**
 * Represents the height of a bet.
 *
 * @param value the points the bet is worth
 */
enum class BetHeight(val value: Int) {
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

    /**
     * Returns the value of this bet height as an integer.
     *
     * @return the value of this bet height
     */
    fun asInt(): Int {
        return value
    }

    fun isOdd(): Boolean {
        // since FORTY is even and ordinal 1, we check for == 0 for odd
        return ordinal % 2 == 0
    }

    fun firstEven(): BetHeight {
        return if (isOdd()) {
            nHigher(1)
        } else {
            this
        }
    }

    fun firstOdd(): BetHeight {
        return if (isOdd()) {
            this
        } else {
            nHigher(1)
        }
    }

    /**
     * Returns the BetHeight that is n higher than this one. (clamped between FORTY and MATCH)
     *
     * @param n the number of steps to go up
     * @return the new BetHeight
     */
    fun nHigher(n: Int): BetHeight {
        return if (n > 0) {
            values()[(this.ordinal + n).coerceAtMost(values().size - 1)]
        } else {
            values()[(this.ordinal + n).coerceAtLeast(1)]
        }
    }

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

        /**
         * Returns the bet height closest to the given number of points.
         *
         * @param points the number of points
         * @return the closest bet height
         */
        fun fromPoints(points: Int): BetHeight {
            return when {
                points < 40 -> NONE
                points < 50 -> FORTY
                points < 60 -> FIFTY
                points < 70 -> SIXTY
                points < 80 -> SEVENTY
                points < 90 -> EIGHTY
                points < 100 -> NINETY
                points < 110 -> HUNDRED
                points < 120 -> HUNDRED_TEN
                points < 130 -> HUNDRED_TWENTY
                points < 140 -> HUNDRED_THIRTY
                points < 150 -> HUNDRED_FORTY
                points < 157 -> HUNDRED_FIFTY
                points < 170 -> HUNDRED_FIFTY_SEVEN
                else -> MATCH
            }
        }
    }
}
