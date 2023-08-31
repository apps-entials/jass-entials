package com.github.lucaengel.jass_entials.game.postgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.game_state.TeamId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.SelectGameActivity
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class CoiffeurPostRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CoiffeurScoreSheet()
                }
            }
        }
    }
}

/**
 * Composable for the Coiffeur score sheet.
 */
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun CoiffeurScoreSheet() {
    val context = LocalContext.current

    val gameState = GameStateHolder.gameState
    val roundScores = GameStateHolder.prevRoundScores + (gameState.winningBet to gameState.roundState.score())
    val bettingState = GameStateHolder.bettingState

    val columnWidth = 100.dp
    val rowHeight = 20.dp
    val rowWidth = 300.dp + (rowHeight * 2)
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row (
            modifier = Modifier.width(rowWidth),
            horizontalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Trump",
                modifier = Modifier.width(columnWidth),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

//            Spacer(modifier = Modifier.weight(1f))

            Divider(modifier = Modifier
                .width(rowHeight)
                .rotate(90f)
                .align(Alignment.CenterVertically)
            )

//            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = gameState.currentUserId.teamId().name,
                modifier = Modifier.width(columnWidth),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

//            Spacer(modifier = Modifier.weight(1f))

            Divider(modifier = Modifier
                .width(rowHeight)
                .rotate(90f)
                .align(Alignment.CenterVertically)
            )

//            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = gameState.currentUserId.nextPlayer().teamId().name,
                modifier = Modifier.width(columnWidth),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

//            Spacer(modifier = Modifier.weight(1f))
        }

        Divider(modifier = Modifier.width(rowWidth))

        Trump.values().map { trump ->

            TrumpScoreRow(
                rowWidth = rowWidth,
                rowHeight = rowHeight,
                columnWidth = columnWidth,
                trump = trump,
                leftTeam = gameState.currentUserId.teamId(),
                roundScores = roundScores.filter { it.first.trump == trump }
            )

            Divider(modifier = Modifier.width(rowWidth))
        }

        val isSidiBarraniOver = gameState.jassType == JassType.SIDI_BARRANI && (
                (GameStateHolder.pointLimits[JassType.SIDI_BARRANI] ?: 1000) <= gameState.roundState.score().gamePoints(TeamId.TEAM_1)
                        || (GameStateHolder.pointLimits[JassType.SIDI_BARRANI] ?: 1000) <= gameState.roundState.score().gamePoints(TeamId.TEAM_2)
                )

        if (isSidiBarraniOver) {
            val winningTeamId = gameState.roundState.winningTeam()
            val winnerText = if (winningTeamId != null) {
                "$winningTeamId won the game!"
            } else {
                "it was a draw!"
            }

            Text(text = "The game is over, $winnerText")
            Button(
                onClick = {
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
    }
}

/**
 * Composable for the score sheet.
 */
@Composable
fun TrumpScoreRow(
    rowWidth: Dp,
    rowHeight: Dp,
    columnWidth: Dp,
    trump: Trump,
    leftTeam: TeamId,
    roundScores: List<Pair<Bet, Score>>
) {
    Row (
        modifier = Modifier
            .width(rowWidth),
        horizontalArrangement = Arrangement.Center,
    ) {

        Box(modifier = Modifier
            .width(columnWidth)
            .height(rowHeight)
            .align(Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = trump.asPicture()),
                contentDescription = trump.toString(),
                modifier = Modifier
                    .height(rowHeight)
                    .align(Alignment.Center),
//                    .align(Alignment.CenterVertically),
                alignment = Alignment.Center,
            )
        }

        Divider(modifier = Modifier
            .width(rowHeight)
            .rotate(90f)
            .align(Alignment.CenterVertically)
        )

        Text(text = roundScores.firstOrNull { it.first.playerId.teamId() == leftTeam }?.second?.gamePoints(leftTeam)?.toString() ?: "---",
            modifier = Modifier.width(columnWidth),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Divider(modifier = Modifier
            .width(rowHeight)
            .rotate(90f)
            .align(Alignment.CenterVertically)
        )

        Text(text = roundScores.firstOrNull { it.first.playerId.teamId() != leftTeam }?.second?.gamePoints(leftTeam.otherTeam())?.toString() ?: "---",
            modifier = Modifier.width(columnWidth),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}