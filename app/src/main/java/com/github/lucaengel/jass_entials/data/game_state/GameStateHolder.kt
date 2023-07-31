package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump

class GameStateHolder {
    companion object {
        private val shuffledDeck = Deck.STANDARD_DECK.shuffled()
        private val player1 = Player("email_1", 0, "first_1", "second_1", Deck.sortPlayerCards(shuffledDeck.cards.subList(0, 9)), 0, "123")
        private val player2 = Player("email_2", 0, "first_2", "second_2", Deck.sortPlayerCards(shuffledDeck.cards.subList(9, 18)), 0, "123")
        private val player3 = Player("email_3", 0, "first_3", "second_3", Deck.sortPlayerCards(shuffledDeck.cards.subList(18, 27)), 0, "123")
        private val player4 = Player("email_4", 0, "first_4", "second_4", Deck.sortPlayerCards(shuffledDeck.cards.subList(27, 36)), 0, "123")
        private val players = listOf(player1, player2, player3, player4)

        var currentPlayer = player1
        var gameState: GameState = GameState(
            0,
            players,
            player1,
            player3,
            1,
            Trick(Deck.STANDARD_DECK.cards.subList(0, 2)),
            1,
            Trump.UNGER_UFE,
            Deck.STANDARD_DECK.dealCards(players),
        )
        //GameState()
        var bettingState: BettingState = BettingState()
    }
}