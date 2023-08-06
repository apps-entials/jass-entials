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
import com.github.lucaengel.jass_entials.data.cards.PlayerData
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

/**
 * Betting round composable.
 */
@Composable
fun BettingRound() {
    val context = LocalContext.current

    var bettingState by remember { mutableStateOf(GameStateHolder.bettingState/*BettingState()*/) }
    var players by remember { mutableStateOf(GameStateHolder.players/*BettingState()*/) }

    val currentUserIdx by remember { mutableStateOf(bettingState.currentUserIdx) }
    val currentUserEmail by remember { mutableStateOf(bettingState.playerEmails[currentUserIdx]) }

    val opponents by remember {
        mutableStateOf(bettingState.playerEmails
            .filter { it != bettingState.playerEmails[bettingState.currentUserIdx] }
            .map { it to CpuPlayer(it) }
        )}

    println("current better: ${bettingState.currentBetterEmail}")


    var tmpFirstName by remember { mutableStateOf(mapOf<String, String>().withDefault { "" }) }
    var tmpLastName by remember { mutableStateOf(mapOf<String, String>().withDefault { "" }) }
    fun setToThinking(playerEmail: String) {
        val player = players.first { it.email == playerEmail }

        tmpFirstName = tmpFirstName.plus(playerEmail to player.firstName)
        tmpLastName = tmpLastName.plus(playerEmail to player.lastName)

        players = players.map {
            if (it.email == playerEmail) {
                it.copy(firstName = "I'm", lastName = "Thinking...")
            } else {
                it
            }
        }
    }

    fun setToNormalName(playerEmail: String) {
        players = players.map {
            if (it.email == playerEmail) {
                it.copy(firstName = tmpFirstName[playerEmail]!!, lastName = tmpLastName[playerEmail]!!)
            } else {
                it
            }
        }
    }

    LaunchedEffect(key1 = bettingState.currentBetterEmail) {
        val currentBetterEmail = bettingState.currentBetterEmail

        if (currentBetterEmail == currentUserEmail)
            return@LaunchedEffect

        val player = opponents.first { it.first == currentBetterEmail }.second
        setToThinking(currentBetterEmail)
        player.bet(bettingState)
            .thenAccept() {
                setToNormalName(currentBetterEmail)
                bettingState = it
            }
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayerEmail = bettingState.playerEmails[(currentUserIdx + 2) % 4]

        JassComposables.PlayerBox(playerData = players.first { it.email == topPlayerEmail }, playerSpot = 2)

        Spacer(modifier = Modifier.weight(1f))


        if (bettingState.currentBetterEmail == currentUserEmail) {

            BettingRow(
                bettingState = bettingState,
                players = players,
                onBetPlace = { bet -> bettingState = bettingState.nextPlayer(bet) },
                onPass = { bettingState = bettingState.nextPlayer() },
                onStartGame = {
                    val gameState = bettingState.startGame()
                    GameStateHolder.gameState = gameState
                    GameStateHolder.players = players

                    val intent = Intent(context, JassRoundActivity::class.java)
                    context.startActivity(intent)
                }
            )
        } else {
            MiddleRowInfo(bettingState = bettingState, players = players, currentPlayerIdx = currentUserIdx)
        }

        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(playerEmail = currentUserEmail, player = players.first { it.email == currentUserEmail })
    }
}

/**
 * Betting row composable (contains the betting elements or the players in the middle row).
 */
@Composable
private fun MiddleRowInfo(bettingState: BettingState, players: List<PlayerData>, currentPlayerIdx: Int) {
    Row {

        val leftPlayerEmail = bettingState.playerEmails[(currentPlayerIdx + 3) % 4]
        JassComposables.PlayerBox(
            playerData = players.first { it.email == leftPlayerEmail },
            playerSpot = 3,
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25f)
                .weight(1f)
        )

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
                val lastBetter = players.first { it.email == lastBet.playerEmail }
                Text(
                    text = "${lastBet.bet} ${lastBet.suit} by\n" +
                        "${lastBetter.firstName} ${lastBetter.lastName}",
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayerEmail = bettingState.playerEmails[(currentPlayerIdx + 1) % 4]

        JassComposables.PlayerBox(
            playerData = players.first { it.email == rightPlayerEmail },
            playerSpot = 1,
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f))
    }
}

/**
 * Betting row composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BettingRow(
    bettingState: BettingState,
    players: List<PlayerData>,
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
    ) {

        Spacer(modifier = Modifier.weight(1f))

        if (bettingState.bets.isEmpty()) {
            Text(
                text = "No bets yet",
                textAlign = TextAlign.Center
            )
        } else {
            val lastBet = bettingState.bets.last()
            val lastBetter = players.first { it.email == lastBet.playerEmail }

            Text(
                text = "${lastBet.bet} ${lastBet.suit} by\n" +
                        "${lastBetter.firstName} ${lastBetter.lastName}",
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
                    onBetPlace(Bet(bettingState.currentBetterEmail, selectedTrump!!, selectedBet))

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
                // If the last bet was placed by the current player, the player can start the game
                if (bettingState.bets.lastOrNull()?.playerEmail == bettingState.currentBetterEmail) {
                    onStartGame()

                    selectedTrump = null
                    selectedBet = BetHeight.NONE
                } else { // Otherwise the player can choose to pass
                    onPass()

                    selectedTrump = null
                    selectedBet = BetHeight.NONE
                }
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(5.dp, 5.dp, 20.dp, 5.dp),
        ) {
            if (bettingState.bets.lastOrNull()?.playerEmail == bettingState.currentBetterEmail)
                Text(text = "Start Game")
            else
                Text(text = "Pass")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

