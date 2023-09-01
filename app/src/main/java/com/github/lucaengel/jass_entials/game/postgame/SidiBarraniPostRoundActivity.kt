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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.game_state.TeamId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.game.SelectGameActivity
import com.github.lucaengel.jass_entials.game.pregame.PreRoundBettingActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class SidiBarraniPostRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SidiBarraniScoreSheet()
                }
            }
        }
    }

    private enum class PointType {
        BETTING_POINTS,
        ROUND_POINTS,
        TOTAL_ROUND_POINTS,
        OLD_GAME_POINTS,
        GAME_POINTS,
    }

    /**
     * Composable for the Coiffeur score sheet.
     */
    @Preview(/*device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360*/)
    @Composable
    fun SidiBarraniScoreSheet() {
        val context = LocalContext.current

        val gameState = GameStateHolder.gameState
        val bettingState = GameStateHolder.bettingState

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        val rowWidth = screenWidth
        val rowHeight = (screenHeight / 11).coerceAtMost(45.dp) // 30.dp
        val columnWidth = (rowWidth/* - (rowHeight * 3)*/) / 3

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.width(rowWidth),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "",
                    modifier = Modifier.width(columnWidth),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )

                Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = gameState.currentUserId.teamId().toString(),
                    modifier = Modifier.width(columnWidth),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )

                Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = gameState.currentUserId.nextPlayer().teamId().toString(),
                    modifier = Modifier.width(columnWidth),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            Divider(modifier = Modifier.width(rowWidth))

            TrumpRow(
                leftTeam = gameState.currentUserId.teamId(),
                bet = gameState.winningBet,
                rowHeight = rowHeight,
                rowWidth = rowWidth,
                columnWidth = columnWidth,
            )

            Divider(modifier = Modifier.width(rowWidth))

            DoubledRow(
                leftTeam = gameState.currentUserId.teamId(),
                bet = gameState.winningBet,
                rowHeight = rowHeight,
                rowWidth = rowWidth,
                columnWidth = columnWidth,
            )

            Divider(modifier = Modifier.width(rowWidth))

            PointsRow(
                leftTeam = gameState.currentUserId.teamId(),
                bet = gameState.winningBet,
                score = gameState.roundState.score(),
                rowHeight = rowHeight,
                rowWidth = rowWidth,
                columnWidth = columnWidth,
                pointType = PointType.ROUND_POINTS,
            )

            Divider(modifier = Modifier.width(rowWidth))

            PointsRow(
                leftTeam = gameState.currentUserId.teamId(),
                bet = gameState.winningBet,
                score = gameState.roundState.score(),
                rowHeight = rowHeight,
                rowWidth = rowWidth,
                columnWidth = columnWidth,
                pointType = PointType.BETTING_POINTS,
            )

            Divider(modifier = Modifier.width(rowWidth))

            PointsRow(
                leftTeam = gameState.currentUserId.teamId(),
                bet = gameState.winningBet,
                score = gameState.roundState.score(),
                rowHeight = rowHeight,
                rowWidth = rowWidth,
                columnWidth = columnWidth,
                pointType = PointType.TOTAL_ROUND_POINTS,
            )

            Divider(modifier = Modifier.width(rowWidth))

            PointsRow(
                leftTeam = gameState.currentUserId.teamId(),
                bet = gameState.winningBet,
                score = gameState.roundState.score(),
                rowHeight = rowHeight,
                rowWidth = rowWidth,
                columnWidth = columnWidth,
                pointType = PointType.OLD_GAME_POINTS,
            )

            Divider(modifier = Modifier.width(rowWidth))

            PointsRow(
                leftTeam = gameState.currentUserId.teamId(),
                bet = gameState.winningBet,
                score = gameState.roundState.score(),
                rowHeight = rowHeight,
                rowWidth = rowWidth,
                columnWidth = columnWidth,
                pointType = PointType.GAME_POINTS,
            )

            val isSidiBarraniOver =
                (GameStateHolder.pointLimits[JassType.SIDI_BARRANI] ?: 1000) <= gameState.roundState.score().gamePoints(TeamId.TEAM_1)
                        || (GameStateHolder.pointLimits[JassType.SIDI_BARRANI] ?: 1000) <= gameState.roundState.score().gamePoints(TeamId.TEAM_2)

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

    @Composable
    private fun PointsRow(
        leftTeam: TeamId,
        bet: Bet,
        score: Score,
        rowHeight: Dp,
        rowWidth: Dp,
        columnWidth: Dp,
        pointType: PointType,
    ) {

        Row(
            modifier = Modifier
                .width(rowWidth)
                .height(rowHeight),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = when (pointType){
                    PointType.ROUND_POINTS -> "Played Points"
                    PointType.GAME_POINTS -> "Total Points"
                    PointType.BETTING_POINTS -> "Betting Points"
                    PointType.TOTAL_ROUND_POINTS -> "Round Points"
                    PointType.OLD_GAME_POINTS -> "Previous Points"
                },
                modifier = Modifier.width(columnWidth),
                textAlign = TextAlign.Center,
                maxLines = 2,
            )

            Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

            val bettingPointsLeftTeam =
                if (score.roundPoints(bet.playerId.teamId()) >= bet.bet.asInt() && bet.playerId.teamId() == leftTeam
                    || score.roundPoints(bet.playerId.teamId()) < bet.bet.asInt() && bet.playerId.teamId() != leftTeam
                ) {
                    bet.bet.asInt() * (if (bet.doubledBy != null) 2 else 1)
                } else {
                    0
                }
            Text(
                text = when (pointType) {
                    PointType.ROUND_POINTS -> "${score.roundPoints(leftTeam)}"
                    PointType.BETTING_POINTS -> "$bettingPointsLeftTeam"
                    PointType.TOTAL_ROUND_POINTS -> "${score.roundPoints(leftTeam) + bettingPointsLeftTeam}"
                    PointType.OLD_GAME_POINTS -> "${score.gamePoints(leftTeam) - (score.roundPoints(leftTeam) + bettingPointsLeftTeam)}"
                    PointType.GAME_POINTS -> "${score.gamePoints(leftTeam)}"
                },
                modifier = Modifier.width(columnWidth),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

            val bettingPointsRightTeam =
                if (score.roundPoints(bet.playerId.teamId()) >= bet.bet.asInt() && bet.playerId.teamId() != leftTeam
                || score.roundPoints(bet.playerId.teamId()) < bet.bet.asInt() && bet.playerId.teamId() == leftTeam
                    ) {
                    bet.bet.asInt() * (if (bet.doubledBy != null) 2 else 1)
                } else {
                    0
                }
            val rightTeam = leftTeam.otherTeam()
            Text(
                text = when (pointType) {
                    PointType.ROUND_POINTS -> "${score.roundPoints(rightTeam)}"
                    PointType.GAME_POINTS -> "${score.gamePoints(rightTeam)}"
                    PointType.TOTAL_ROUND_POINTS -> "${score.roundPoints(rightTeam) + bettingPointsRightTeam}"
                    PointType.OLD_GAME_POINTS -> "${score.gamePoints(rightTeam) - (score.roundPoints(rightTeam) + bettingPointsRightTeam)}"
                    PointType.BETTING_POINTS -> "$bettingPointsRightTeam"
                },
                modifier = Modifier.width(columnWidth),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.weight(1f))
        }

    }

    @Composable
    fun TrumpRow(
        leftTeam: TeamId,
        bet: Bet,
        rowHeight: Dp,
        rowWidth: Dp,
        columnWidth: Dp,
    ) {
        Row(
            modifier = Modifier
                .width(rowWidth),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Winning Bet",
                modifier = Modifier.width(columnWidth),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

            if (bet.playerId.teamId() == leftTeam) {
                BetBox(
                    bet = bet,
                    rowHeight = rowHeight,
                    columnWidth = columnWidth,
                )
            } else {
                Text(
                    text = "---",
                    modifier = Modifier.width(columnWidth),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }

            Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

            if (bet.playerId.teamId() != leftTeam) {
                BetBox(
                    bet = bet,
                    rowHeight = rowHeight,
                    columnWidth = columnWidth,
                )
            } else {
                Text(
                    text = "---",
                    modifier = Modifier.width(columnWidth),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    @Composable
    fun DoubledRow(
        leftTeam: TeamId,
        bet: Bet,
        rowHeight: Dp,
        rowWidth: Dp,
        columnWidth: Dp,
    ) {
        Row(
            modifier = Modifier
                .width(rowWidth)
                .height(rowHeight),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Doubled by",
                modifier = Modifier.width(columnWidth),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

            Text(
                text = if (bet.doubledBy?.teamId() == leftTeam) "${GameStateHolder.players[bet.doubledBy.ordinal].firstName} ${GameStateHolder.players[bet.doubledBy.ordinal].lastName}" else "---",
                modifier = Modifier.width(columnWidth),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            Divider(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )

            Text(
                text = if (bet.doubledBy?.teamId() == leftTeam.otherTeam()) "${GameStateHolder.players[bet.doubledBy.ordinal].firstName} ${GameStateHolder.players[bet.doubledBy.ordinal].lastName}" else "---",
                modifier = Modifier.width(columnWidth),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    @Composable
    fun BetBox(
        bet: Bet,
        rowHeight: Dp,
        columnWidth: Dp,
    ) {
        Row(
            modifier = Modifier
                .width(columnWidth),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(rowHeight)
                    .height(rowHeight),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = bet.trump.asPicture()),
                    contentDescription = bet.trump.toString(),
                    modifier = Modifier
                        .height(rowHeight.coerceAtMost(30.dp)),
                    alignment = Alignment.Center,
                )
            }

            Text(
                text = "${bet.bet}",
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp),
            )
        }
    }
}