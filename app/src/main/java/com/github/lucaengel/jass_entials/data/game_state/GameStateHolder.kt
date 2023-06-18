package com.github.lucaengel.jass_entials.data.game_state

class GameStateHolder {
    companion object {
        var gameState: GameState = GameState()
        var bettingState: BettingState = BettingState()
    }
}