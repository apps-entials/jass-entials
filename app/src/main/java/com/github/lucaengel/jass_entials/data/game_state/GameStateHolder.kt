package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump

class GameStateHolder {
    companion object {
        private val shuffledDeck = Deck.STANDARD_DECK//.shuffled()
        private val playerData1 = PlayerData("email_1", 0, "first_1", "second_1", Deck.sortPlayerCards(shuffledDeck.cards.subList(6, 9)), 0, "123")
        private val playerData2 = PlayerData("email_2", 0, "first_2", "second_2", Deck.sortPlayerCards(shuffledDeck.cards.subList(9, 18)), 0, "123")
        private val playerData3 = PlayerData("email_3", 0, "first_3", "second_3", Deck.sortPlayerCards(shuffledDeck.cards.subList(18, 27)), 0, "123")
        private val playerData4 = PlayerData("email_4", 0, "first_4", "second_4", Deck.sortPlayerCards(shuffledDeck.cards.subList(27, 36)), 0, "123")
        private val players = listOf(playerData1, playerData2, playerData3, playerData4)


        var gameState: GameState = GameState(
            0,
            players,
            playerData1,
            playerData3,
            1,
            Trick(Deck.STANDARD_DECK.cards.subList(0, 2).mapIndexed { index, card -> Pair(card, players[index]) }),
            listOf(),
            1,
            Trump.UNGER_UFE,
            Deck.STANDARD_DECK.dealCards(players),
        )
        //GameState()
        var bettingState: BettingState = BettingState()
    }
}