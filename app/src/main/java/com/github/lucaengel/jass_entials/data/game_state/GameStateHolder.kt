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
        private val playerData1 = PlayerData("email_1", 0, "first_1", "second_1", Deck.sortPlayerCards(shuffledDeck.cards.subList(0, 9)), 0, "123")
        private val playerData2 = PlayerData("email_2", 1, "first_2", "second_2", Deck.sortPlayerCards(shuffledDeck.cards.subList(9, 18)), 0, "123")
        private val playerData3 = PlayerData("email_3", 2, "first_3", "second_3", Deck.sortPlayerCards(shuffledDeck.cards.subList(18, 27)), 0, "123")
        private val playerData4 = PlayerData("email_4", 3, "first_4", "second_4", Deck.sortPlayerCards(shuffledDeck.cards.subList(27, 36)), 0, "123")
        private val players = listOf(playerData1, playerData2, playerData3, playerData4)

        /**
         * The current game state.
         */
        var gameState: GameState = GameState(
            0,
            players,
            playerData1,
            playerData1,
            1,
            Trick(shuffledDeck.cards.subList(0, 4).mapIndexed { index, card -> Pair(card, players[index]) }),
            listOf(),
            1,
            Trump.UNGER_UFE,
            Deck.STANDARD_DECK.dealCards(players),
        )

        /**
         * The current betting state.
         */
        var bettingState: BettingState =
            BettingState(
                0,
                listOf(playerData1, playerData2, playerData3, playerData4),
                playerData1,
                JassType.SIDI_BARAHNI,
                listOf(
                    Bet(playerData2, Trump.CLUBS, BetHeight.FORTY)
                ),
                GameState()
            )

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
        fun goToNextBettingStateRound(startingBetter: PlayerData) {
            bettingState = bettingState.nextBettingRound(startingBetter)
        }
    }
}