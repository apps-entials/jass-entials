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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.AUTOMOTIVE_1024p
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BetHeight
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.JassComposables
import com.github.lucaengel.jass_entials.game.JassRoundActivity
import com.github.lucaengel.jass_entials.game.player.CpuPlayer
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

/**
 * Sidi Barahni pre-round activity (i.e., betting round).
 */
class PreRoundBettingActivity : ComponentActivity() {

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

    val currentUserId by remember { mutableStateOf(bettingState.currentUserId) }

    val opponents by remember {
        mutableStateOf(
            PlayerId.values()
            .filter { it != currentUserId }
            .map { it to CpuPlayer(it) }
        )}

    var tmpFirstName by remember { mutableStateOf(mapOf<PlayerId, String>().withDefault { "" }) }
    var tmpLastName by remember { mutableStateOf(mapOf<PlayerId, String>().withDefault { "" }) }
    fun setToThinking(playerId: PlayerId) {
        val player = players.first { it.id == playerId }

        tmpFirstName = tmpFirstName.plus(playerId to player.firstName)
        tmpLastName = tmpLastName.plus(playerId to player.lastName)

        players = players.map {
            if (it.id == playerId) {
                it.copy(firstName = "I'm", lastName = "Thinking...")
            } else {
                it
            }
        }
    }

    fun setToNormalName(playerId: PlayerId) {
        players = players.map {
            if (it.id == playerId) {
                it.copy(firstName = tmpFirstName[playerId]!!, lastName = tmpLastName[playerId]!!)
            } else {
                it
            }
        }
    }

    // this launched effect is responsible for the cpu players' actions
    LaunchedEffect(key1 = bettingState.currentBetterId) {
        val currentBetterId = bettingState.currentBetterId

        if (currentBetterId == currentUserId)
            return@LaunchedEffect

        val player = opponents.first { it.first == currentBetterId }.second
        setToThinking(currentBetterId)


        // TODO: consider refactoring since context switches cannot be tested well when in futures and LaunchedEffects
        val oldBettingState = bettingState
        val betFuture = player.bet(bettingState)
            .thenApply {
                setToNormalName(currentBetterId)
                bettingState = it

                // Checks for game start when not playing sidi barahni
                if (it.jassType != JassType.SIDI_BARAHNI
                    && it.betActions.last() != Bet.BetAction.PASS) {
                    val gameState = bettingState.startGame()
                    GameStateHolder.gameState = gameState
                    GameStateHolder.players = players

                    val intent = Intent(context, JassRoundActivity::class.java)
                    context.startActivity(intent)
                }

                it
            }

        // cpu starting the game for sidi barahni
        if (oldBettingState.jassType == JassType.SIDI_BARAHNI
            && oldBettingState.bets.lastOrNull()?.playerId == currentBetterId) {
            // TODO: for now, if cpu can start the game, the start game is signalled by
            //  a pass after the last bet (since you cannot pass if you have made the last bet).
            //  This is not ideal, but it works for now.

            betFuture.thenAccept {
                // last bet was by current player, passing now means that they want to start
                // the game with the trump they announced in the last round.
                if (it.betActions.last() == Bet.BetAction.PASS) {
                    val gameState = it.startGame()
                    GameStateHolder.gameState = gameState
                    GameStateHolder.players = players

                    val intent = Intent(context, JassRoundActivity::class.java)
                    context.startActivity(intent)
                } else {
                    bettingState = it
                }
            }
        } else {
            betFuture.thenAccept { bettingState = it }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayerId = currentUserId.teamMate()

        JassComposables.PlayerBox(playerData = players.first { it.id == topPlayerId }, playerSpot = 2)

        Spacer(modifier = Modifier.weight(1f))


        if (bettingState.currentBetterId == currentUserId) {

            fun startGameFun(currBettingState: BettingState) {
                val gameState = currBettingState.startGame()
                GameStateHolder.gameState = gameState
                GameStateHolder.players = players

                val intent = Intent(context, JassRoundActivity::class.java)
                context.startActivity(intent)
            }

            BettingRow(
                bettingState = bettingState,
                players = players,
                onBetPlace = { bet ->
                    val newBettingState = bettingState.nextPlayer(bet)

                    // Only in Sidi Barahni, the next players
                    // can place a higher bet than the current one.
                    if (bettingState.jassType != JassType.SIDI_BARAHNI) {
                        startGameFun(newBettingState)
                    } else {
                        bettingState = newBettingState
                    }
                             },
                onPass = { bettingState = bettingState.nextPlayer() },
                onStartGame = {
                    startGameFun(bettingState)
                }
            )
        } else {
            MiddleRowInfo(bettingState = bettingState, players = players, currentPlayerId = currentUserId)
        }

        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(player = players.first { it.id == currentUserId })
    }
}

/**
 * Betting row composable (contains the betting elements or the players in the middle row).
 */
@Composable
private fun MiddleRowInfo(bettingState: BettingState, players: List<PlayerData>, currentPlayerId: PlayerId) {
    Row {

        JassComposables.PlayerBox(
            playerData = players.first { it.id == currentPlayerId.teamMate().nextPlayer() },
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
                val lastBetter = players.first { it.id == lastBet.playerId }
                Text(
                    text = "${lastBet.bet} ${lastBet.trump} by\n" +
                        "${lastBetter.firstName} ${lastBetter.lastName}",
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayerId = currentPlayerId.nextPlayer()

        JassComposables.PlayerBox(
            playerData = players.first { it.id == rightPlayerId },
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
            val lastBetter = players.first { it.id == lastBet.playerId }

            Text(
                text = "${lastBet.bet} ${lastBet.trump} by\n" +
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
                value = if (
                    (selectedBet == BetHeight.NONE
                                    && bettingState.jassType == JassType.SIDI_BARAHNI)
                    || selectedTrump == null) ""
                else if (bettingState.jassType == JassType.SIDI_BARAHNI) "$selectedBet ${selectedTrump!!}"
                else "${selectedTrump!!}",
                onValueChange = { selectedBet = BetHeight.fromString(it) },
                readOnly = true,
                placeholder = { Text("Select bet") },
                trailingIcon = {
                    Icon(icon, contentDescription = "Bet placing dropdown icon",
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

                if (bettingState.jassType == JassType.SIDI_BARAHNI) {
                    DropdownMenu(
                        expanded = isBetDropdownExpanded,
                        onDismissRequest = { isBetDropdownExpanded = false },
                        modifier = Modifier.testTag("betDropdown")
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
                }

                DropdownMenu(
                    expanded = isTrumpDropdownExpanded,
                    onDismissRequest = { isTrumpDropdownExpanded = false },
                    modifier = Modifier.testTag("trumpDropdown")
                ) {
                    bettingState.availableTrumps().forEach { trump ->
                        DropdownMenuItem(
                            modifier = Modifier.testTag(trump.toString()),
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

        if (bettingState.jassType == JassType.SIDI_BARAHNI && bettingState.availableActions().contains(Bet.BetAction.BET)
            || bettingState.jassType != JassType.SIDI_BARAHNI) {
            Button(
                onClick = {

                    if ((selectedBet != BetHeight.NONE || bettingState.jassType != JassType.SIDI_BARAHNI) && selectedTrump != null) {

                        onBetPlace(
                            Bet(
                                bettingState.currentBetterId,
                                selectedTrump!!,
                                selectedBet
                            )
                        )

                        selectedTrump = null
                        selectedBet = BetHeight.NONE
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(5.dp)
            ) {
                val actions = bettingState.availableActions()
                if (actions.contains(Bet.BetAction.BET)) {
                    if (bettingState.jassType == JassType.SIDI_BARAHNI)
                        Text(text = "Place Bet")
                    else
                        Text(text = "Start Game")
                }
            }
        }

        if (bettingState.jassType == JassType.SIDI_BARAHNI || bettingState.availableActions().contains(Bet.BetAction.PASS)) {
            Button(
                onClick = {
                    // If the last bet was placed by the current player, the player can start the game
                    if (bettingState.bets.lastOrNull()?.playerId == bettingState.currentBetterId) {
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
                if (bettingState.bets.lastOrNull()?.playerId == bettingState.currentBetterId)
                    Text(text = "Start Game")
                else if (bettingState.availableActions().contains(Bet.BetAction.PASS))
                    Text(text = "Pass")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

