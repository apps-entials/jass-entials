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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.game_state.TeamId
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.SelectGameActivity
import com.github.lucaengel.jass_entials.game.betting.CoiffeurBettingLogic
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
@Preview(/*device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360*/)
@Composable
fun CoiffeurScoreSheet() {
    val context = LocalContext.current

    val gameState = GameStateHolder.gameState
    val roundScores = GameStateHolder.prevRoundScores// + (gameState.winningBet to gameState.roundState.score())
    val bettingState = GameStateHolder.bettingState

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val rowWidth = screenWidth
    val rowHeight = (screenHeight / 11).coerceAtMost(30.dp)
    val columnWidth = (rowWidth/* - (rowHeight * 3)*/) / 3 -2.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row (
            modifier = Modifier.width(rowWidth),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Trump",
                modifier = Modifier.width(columnWidth),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
            )

            Divider(modifier = Modifier
                .height(rowHeight)
                .width(3.dp)
                .align(Alignment.CenterVertically)
            )

            Text(
                text = gameState.currentUserId.teamId().toString(),
                modifier = Modifier.width(columnWidth),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
            )

            Divider(modifier = Modifier
                .height(rowHeight)
                .width(3.dp)
                .align(Alignment.CenterVertically)
            )

            Text(
                text = gameState.currentUserId.nextPlayer().teamId().toString(),
                modifier = Modifier.width(columnWidth),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Divider(modifier = Modifier.width(rowWidth).height(3.dp))

        Trump.values().map { trump ->

            TrumpScoreRow(
                rowWidth = rowWidth,
                rowHeight = rowHeight,
                columnWidth = columnWidth,
                trump = trump,
                leftTeam = gameState.currentUserId.teamId(),
                roundScores = roundScores.filter { it.first.trump == trump }
            )

            Divider(modifier = Modifier.width(rowWidth).height(if (trump == Trump.values().last()) 3.dp else 1.dp))
        }

        val totalScore = roundScores.foldRight(Score.INITIAL) { (bet, score), acc ->
            val teamId = bet.playerId.teamId()
            acc.withPointsAdded(teamId, score.roundPoints(teamId) * (bet.trump.ordinal + 1))
        }

        TrumpScoreRow(
            rowWidth = rowWidth,
            rowHeight = rowHeight,
            columnWidth = columnWidth,
            trump = Trump.HEARTS,
            leftTeam = gameState.currentUserId.teamId(),
            roundScores = listOf(Bet() to totalScore),
            isTotalPointsRow = true,
        )

//        Divider(modifier = Modifier.width(rowWidth))

        val isCoiffeurOver = GameStateHolder.prevRoundScores
                    .map { it.first.trump to it.first.playerId.teamId() }
                    .toSet()
                    .size == 2 * Trump.values().size

        if (isCoiffeurOver) {
            val winningTeamId = totalScore.winningTeam()
            val winnerText = if (winningTeamId != null) {
                "$winningTeamId won!"
            } else {
                "it was a draw!"
            }

            Text(text = "The game is over, $winnerText", maxLines = 2)
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
    roundScores: List<Pair<Bet, Score>>,
    isTotalPointsRow: Boolean = false,
) {
    Row (
        modifier = Modifier
            .width(rowWidth),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Row (
            modifier = Modifier
                .width(columnWidth)
                .height(rowHeight),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ){
            if (!isTotalPointsRow) {
                Box(modifier = Modifier
                    .width(rowHeight)
                    .height(rowHeight)
                    .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = trump.asPicture()),
                        contentDescription = trump.toString(),
                        modifier = Modifier
                            .height(rowHeight),
                        alignment = Alignment.Center,
                    )
                }

                Text(
                    text = " x ${CoiffeurBettingLogic.factorForTrump(trump)}",
                    modifier = Modifier
                        .padding(5.dp, 0.dp, 0.dp, 0.dp),
                )
            }
        }

        Divider(
            modifier = Modifier
                .height(rowHeight)
                .width(3.dp)
                .align(Alignment.CenterVertically)
        )

        val scoreLeftTeam = if (isTotalPointsRow) roundScores.firstOrNull()?.second else roundScores.firstOrNull { it.first.playerId.teamId() == leftTeam }?.second
        val defaultText = if (isTotalPointsRow) "0" else "---"

        Text(text = (if (isTotalPointsRow) scoreLeftTeam?.gamePoints(leftTeam) else scoreLeftTeam?.roundPoints(leftTeam))?.toString() ?: defaultText,
            modifier = Modifier.width(columnWidth),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Divider(
            modifier = Modifier
                .height(rowHeight)
                .width(3.dp)
                .align(Alignment.CenterVertically)
        )

        val rightTeam = leftTeam.otherTeam()
        val scoreRightTeam = if (isTotalPointsRow) roundScores.firstOrNull()?.second else roundScores.firstOrNull { it.first.playerId.teamId() == rightTeam }?.second
        Text(text = (if (isTotalPointsRow) scoreRightTeam?.gamePoints(rightTeam) else scoreRightTeam?.roundPoints(rightTeam))?.toString() ?: defaultText,
            modifier = Modifier.width(columnWidth),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}