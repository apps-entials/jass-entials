package com.github.lucaengel.jass_entials.game.postgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.TeamId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.SelectGameActivity
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

/**
 * Activity for the Sidi Barrani post round screen.
 */
class PostRoundActivity : ComponentActivity() {

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

    Column(
        // center children
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Row {
            Spacer(modifier = Modifier.weight(1f))

            Text(text = "This round")

            Spacer(modifier = Modifier.weight(1f))

            Column {
                TeamId.values().map { teamId ->
                    Text(text = "$teamId: ${gameState.roundState.score().roundPoints(teamId)}")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.weight(1f))

        Row {
            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Total points")

            Spacer(modifier = Modifier.weight(1f))

            Column {
                TeamId.values().map { teamId ->
                    Text(text = "$teamId: ${gameState.roundState.score().gamePoints(teamId)}")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.weight(1f))

        if (gameState.jassType == JassType.COIFFEUR
            && (GameStateHolder.prevTrumpsByTeam[TeamId.TEAM_1]?.size ?: 0) == Trump.values().size
            && (GameStateHolder.prevTrumpsByTeam[TeamId.TEAM_2]?.size ?: 0) == Trump.values().size
        ) {
            val winningTeamId = gameState.roundState.winningTeam()
            val winnerText = if (winningTeamId != null) {
                "$winningTeamId won the game!"
            } else {
                "it was a draw!"
            }

            Text(text = "The game is over, $winnerText")
            Button(
                onClick = {
                    // have player to the right of the starting better of the last round start the next round

                    val intent = Intent(context, SelectGameActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Text(text = "Choose a new Jass game")
            }

        } else {
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


        Spacer(modifier = Modifier.weight(1f))
    }
}