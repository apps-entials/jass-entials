package com.github.lucaengel.jass_entials.game.postgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.game.pregame.SidiBarahniPreRoundActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

/**
 * Activity for the Sidi Barahni post round screen.
 */
class SidiBarahniPostRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ScoreSheet()
                }
            }
        }
    }
}

/**
 * Composable for the score sheet.
 */
@Composable
fun ScoreSheet() {
    val context = LocalContext.current

    val gameState = GameStateHolder.gameState
    Column(
        // center children
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        gameState.playerEmails
            .map {
                val player = GameStateHolder.players.first { p -> p.email == it }
                Text(text = "${player.firstName} ${player.lastName}: ${gameState.points(it)}")
            }

        Button(
            onClick = {
                GameStateHolder.goToNextBettingStateRound(gameState.playerEmails[gameState.currentPlayerIdx])

                val intent = Intent(context, SidiBarahniPreRoundActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = "Start next round")
        }
    }
}