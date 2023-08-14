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
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
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
    val bettingState = GameStateHolder.bettingState

    val players = GameStateHolder.players
    Column(
        // center children
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PlayerId.values()
            .map {
                val player = players.first { p -> p.id == it }
                Text(text = "${player.firstName} ${player.lastName}: ${gameState.points(it)}")
            }

        Button(
            onClick = {
                // have player to the right of the starting better of the last round start the next round
                GameStateHolder.goToNextBettingStateRound(bettingState.startingBetterId.nextPlayer())

                val intent = Intent(context, PreRoundBettingActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = "Start next round")
        }
    }
}