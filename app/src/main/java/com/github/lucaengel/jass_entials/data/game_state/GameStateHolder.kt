package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.jass.Trump

class GameStateHolder {
    companion object {
        private val player1 = Player("email_1", 0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123")
        private val player2 = Player("email_2", 0, "first_2", "second_2", Deck.STANDARD_DECK.cards.subList(9, 18), 0, "123")
        private val player3 = Player("email_3", 0, "first_3", "second_3", Deck.STANDARD_DECK.cards.subList(18, 27), 0, "123")
        private val player4 = Player("email_4", 0, "first_4", "second_4", Deck.STANDARD_DECK.cards.subList(27, 36), 0, "123")
        private val players = listOf(player1, player2, player3, player4)

        var currentPlayer = player1
        var gameState: GameState = GameState(
            players,
            player1,
            player3,
            1,
            players/*.subList(0,2)*/.mapIndexed { index, player ->
                player to Deck.STANDARD_DECK.cards[index]
            }.toMap(),
            1,
            Trump.UNGER_UFE,
            Deck.STANDARD_DECK.dealCards(players),
        )
        //GameState()
        var bettingState: BettingState = BettingState()
    }
}