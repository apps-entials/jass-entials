package com.github.lucaengel.jass_entials.game.pregame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.AUTOMOTIVE_1024p
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.JassComposables
import com.github.lucaengel.jass_entials.game.JassRoundActivity
import com.github.lucaengel.jass_entials.game.player.CpuPlayer
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

/**
 * Sidi Barahni pre-round activity (i.e., betting round).
 */
class SidiBarahniPreRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyPreview()
        }
    }
}

// Landscape mode:
@Preview(device = AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun MyPreview() {

    JassentialsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BettingRound()
        }
    }
}

@Composable
fun BettingRound() {
    val context = LocalContext.current

    var bettingState by remember { mutableStateOf(GameStateHolder.bettingState/*BettingState()*/) }
    val currentPlayerIdx by remember { mutableStateOf(bettingState.currentPlayerIdx) }
    val currentPlayerData by remember { mutableStateOf(bettingState.playerDatas[currentPlayerIdx]) }

    val opponents by remember {
        mutableStateOf(bettingState.playerDatas
            .filter { it != bettingState.playerDatas[bettingState.currentPlayerIdx] }
            .map { it to CpuPlayer(it) }
        )}

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayer = bettingState.playerDatas[(currentPlayerIdx + 2) % 4]

        JassComposables.PlayerBox(playerData = topPlayer, playerSpot = 2)

        Spacer(modifier = Modifier.weight(1f))

        if (bettingState.currentBetter == currentPlayerData) {
            val simulatePlayers: () -> Unit = {
                val opp0 = opponents[0].second
                    .bet(bettingState)
                    .thenAccept {
                        bettingState = it
//                        sleep(2000)
                    }

                val opp1 = opp0.thenCompose {
                    opponents[1].second
                        .bet(bettingState)
                }.thenAccept {
                    bettingState = it
//                    sleep(2000)
                }

                val opp2 = opp1.thenCompose {
                    opponents[2].second
                        .bet(bettingState)
                }

                opp2.thenApply {
                    bettingState = it
//                    sleep(2000)
                }
            }

            BettingRow(
                bettingState = bettingState,
                onBetPlace = { bet ->
                    bettingState = bettingState.nextPlayer(bet)
                    simulatePlayers()
                },
                onPass = {
                    bettingState = bettingState.nextPlayer()
                    simulatePlayers()
                },
                onStartGame = {
                    println("bets: ${bettingState.bets}")
                    val gameState = bettingState.startGame()
                    println("gamestate trump: ${gameState.currentTrump}")


                    println("gamestate      : ${gameState.playerDatas[currentPlayerIdx]}")
                    GameStateHolder.gameState = gameState
                    println("gamestate after: ${GameStateHolder.gameState.playerDatas[currentPlayerIdx]}")


                    //TODO: adapt to new betting state for the next potential round???
//                    GameStateHolder.bettingState = BettingState()

                    val intent = Intent(context, JassRoundActivity::class.java)
                    context.startActivity(intent)
                }
            )
        } else {

            MiddleRowInfo(bettingState = bettingState, currentPlayerIdx = currentPlayerIdx)

        }

        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(playerData = currentPlayerData)
    }
}

@Composable
private fun MiddleRowInfo(bettingState: BettingState, currentPlayerIdx: Int) {
    Row {

        val leftPlayer = bettingState.playerDatas[(currentPlayerIdx + 3) % 4]
//        Box(
//            modifier = Modifier
//                .fillMaxHeight(0.5F)
//                .fillMaxWidth(0.25F)
//                .weight(1f),
//            contentAlignment = Alignment.Center
//        ) {
        JassComposables.PlayerBox(playerData = leftPlayer, playerSpot = 3,
            Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25f)
                .weight(1f))
//        }

        Spacer(modifier = Modifier.weight(0.1f))

        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(5.dp)
                .fillMaxWidth(0.5F)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {

            if (bettingState.bets.isEmpty()) {
                Text(
                    text = "No bets yet",
                    textAlign = TextAlign.Center
                )
            } else {
                val lastBet = bettingState.bets.last()

                Text(
                    text = "${lastBet.bet} ${lastBet.suit} by\n" +
                        "${lastBet.playerData.firstName} ${lastBet.playerData.lastName}",
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayer = bettingState.playerDatas[(currentPlayerIdx + 1) % 4]

        JassComposables.PlayerBox(playerData = rightPlayer, playerSpot = 1, Modifier
            .fillMaxHeight(0.5F)
            .fillMaxWidth(0.25F)
            .weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BettingRow(
    bettingState: BettingState,
    onBetPlace: (Bet) -> Unit = {},
    onPass: () -> Unit = {},
    onStartGame: () -> Unit = {},
) {
    var isBetDropdownExpanded by remember { mutableStateOf(false) }
    var isTrumpDropdownExpanded by remember { mutableStateOf(false) }
    var selectedBet by remember { mutableStateOf(BetHeight.NONE) }
    var selectedTrump: Trump? by remember { mutableStateOf(null) }

    val icon = if (isBetDropdownExpanded || isTrumpDropdownExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .fillMaxHeight(0.5F)
    ) {

        Spacer(modifier = Modifier.weight(1f))

        if (bettingState.bets.isEmpty()) {
            Text(
                text = "You start!",
                textAlign = TextAlign.Center
            )
        } else {
            val lastBet = bettingState.bets.last()

            Text(
                text = "${lastBet.bet} ${lastBet.suit} by\n" +
                        "${lastBet.playerData.firstName} ${lastBet.playerData.lastName}",
                textAlign = TextAlign.Center,
                maxLines = 3,
                )
        }

        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.Center,
        ) {

            TextField(
                modifier = Modifier.wrapContentWidth(),
                value = if (selectedBet == BetHeight.NONE || selectedTrump == null) "" else "$selectedBet ${selectedTrump!!}",
                onValueChange = { selectedBet = BetHeight.fromString(it) },
                readOnly = true,
                placeholder = { Text("Select bet") },
                trailingIcon = {
                    Icon(icon, contentDescription = "Expand dropdown",
                        Modifier.clickable {
                            if (isBetDropdownExpanded || isTrumpDropdownExpanded) {
                                isBetDropdownExpanded = false
                                isTrumpDropdownExpanded = false
                            } else {
                                isBetDropdownExpanded = true
                                isTrumpDropdownExpanded = true
                            }
                        }
                    )
                }
            )

            Row {
                DropdownMenu(
                    expanded = isBetDropdownExpanded,
                    onDismissRequest = { isBetDropdownExpanded = false }
                ) {
                    bettingState.availableBets().forEach { bet ->
                        DropdownMenuItem(
                            text = { Text(text = bet.toString()) },
                            onClick = {
                                selectedBet = bet
                                isBetDropdownExpanded = false
                            })
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))

                DropdownMenu(
                    expanded = isTrumpDropdownExpanded,
                    onDismissRequest = { isTrumpDropdownExpanded = false }
                ) {
                    bettingState.availableTrumps().forEach { trump ->
                        DropdownMenuItem(
                            text = { Text(text = trump.toString()) },
                            onClick = {
                                selectedTrump = trump
                                isTrumpDropdownExpanded = false
                            })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (selectedBet != BetHeight.NONE && selectedTrump != null) {
                    onBetPlace(Bet(bettingState.currentBetter, selectedTrump!!, selectedBet))

                    selectedTrump = null
                    selectedBet = BetHeight.NONE
                }
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(5.dp)
        ) {
            Text(text = "Place Bet")
        }

        Button(
            onClick = {
                if (bettingState.bets.lastOrNull()?.playerData == bettingState.currentBetter) {
                    onStartGame()

                    selectedTrump = null
                    selectedBet = BetHeight.NONE
                }
                else {
                    onPass()

                    selectedTrump = null
                    selectedBet = BetHeight.NONE
                }
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(5.dp, 5.dp, 20.dp, 5.dp),
        ) {
            if (bettingState.bets.lastOrNull()?.playerData == bettingState.currentBetter)
                Text(text = "Start Game")
            else
                Text(text = "Pass")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

