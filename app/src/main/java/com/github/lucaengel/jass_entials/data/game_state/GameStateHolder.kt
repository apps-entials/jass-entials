package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * Holds the current game and betting state.
 */
class GameStateHolder {

    companion object {
        private val shuffledDeck = Deck.STANDARD_DECK.shuffled()
        private val playerData1 = PlayerData(
            PlayerId.PLAYER_1,
            "first_1",
            "second_1",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(4, 12)),
            0,
            "123"
        )
        private val playerData2 = PlayerData(
            PlayerId.PLAYER_2,
            "first_2",
            "second_2",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(12, 20)),
            0,
            "123"
        )
        private val playerData3 = PlayerData(
            PlayerId.PLAYER_3,
            "first_3",
            "second_3",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(20, 28)),
            0,
            "123"
        )
        private val playerData4 = PlayerData(
            PlayerId.PLAYER_4,
            "first_4",
            "second_4",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(28, 36)),
            0,
            "123"
        )

        /**
         * The current player datas.
         */
        var players = listOf(playerData1, playerData2, playerData3, playerData4)

        /**
         * The current game state.
         */
        var gameState: GameState = GameState(
            currentUserId = playerData1.id,
            playerEmails = listOf(),
            currentPlayerId = playerData1.id,
            startingPlayerId = playerData1.id,
            currentRound = 1,
            currentTrick = Trick(shuffledDeck.cards.subList(0, 4).mapIndexed { i, c -> Trick.TrickCard(c, players[i].id) }),
            currentRoundTrickWinners = listOf(),
            currentTrickNumber = 1,
            currentTrump = Trump.UNGER_UFE,
            winningBet = Bet(playerData2.id, Trump.CLUBS, BetHeight.FORTY),
            playerCards = Deck.STANDARD_DECK.dealCards(),
        )

        /**
         * The current betting state.
         */
        var bettingState: BettingState =
            BettingState(
                currentUserId = playerData1.id,
                playerEmails = listOf(),
                currentBetterId = playerData1.id,
                startingBetterId = playerData1.id,
                jassType = JassType.SCHIEBER,
                bets = listOf(),
                betActions = listOf(),
                gameState = GameState(),
            )

        /**
         * The current jass type.
         */
        var jassType: JassType = JassType.SIDI_BARAHNI

        // TODO: call this when a new game starts
        /*fun startNewGameBettingState(playerDatas: List<PlayerData>, currentPlayerData: PlayerData) {
            if (!playerDatas.contains(currentPlayerData)) throw IllegalArgumentException("Current player must be in playerDatas")

            bettingState = BettingState(
                0,
                listOf(playerData1, playerData2, playerData3, playerData4),
                playerData1,
                JassType.SIDI_BARAHNI,
                listOf(),
                GameState()
            )
        }*/

        /**
         * Updates the betting state to go to the next betting state round.
         *
         * @param startingBetter The player that starts the next betting round.
         */
        fun goToNextBettingStateRound(startingBetter: PlayerId) {
            bettingState = bettingState.nextBettingRound(startingBetter)
        }
    }
}