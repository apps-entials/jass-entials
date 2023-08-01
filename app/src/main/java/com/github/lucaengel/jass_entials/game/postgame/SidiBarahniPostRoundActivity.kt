package com.github.lucaengel.jass_entials.game.postgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class SidiBarahniPostRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val gameState = GameStateHolder.gameState
                    Column {
                        gameState.playerDatas
                            .map {
                                Text(text = "${it.firstName} ${it.lastName}: ${gameState.points(it)}")
                            }
                    }
                }
            }
        }
    }
}