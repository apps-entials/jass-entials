package com.github.lucaengel.jass_entials.game.pregame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
import com.github.lucaengel.jass_entials.game.player.DelayedCpuPlayer
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
            .map { it to DelayedCpuPlayer(playerId = it) }
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

    fun startGameFun(currBettingState: BettingState) {
        val gameState = currBettingState.startGame()
        GameStateHolder.gameState = gameState
        GameStateHolder.players = players

        val intent = Intent(context, JassRoundActivity::class.java)
        context.startActivity(intent)
    }

    fun onDouble(doubledBet: Bet) {
        val doubledBettingState = bettingState.withBetDoubled(doubledBet)
        // if doubledBettingState is null, the bet was not the last bet
        // and, therefore, cannot be doubled
        if (doubledBettingState != null) {
            bettingState = doubledBettingState
            startGameFun(bettingState)
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
        val betFuture = player.bet(bettingState, GameStateHolder.players[currentBetterId.ordinal].cards)
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
                },
                onDouble = { onDouble(it) }
            )
        } else {
            MiddleRowInfo(
                bettingState = bettingState,
                players = players,
                currentPlayerId = currentUserId,
                onDouble = { onDouble(it) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(player = players.first { it.id == currentUserId })
    }
}

/**
 * Betting row composable (contains the betting elements or the players in the middle row).
 */
@Composable
private fun MiddleRowInfo(
    bettingState: BettingState,
    players: List<PlayerData>,
    currentPlayerId: PlayerId,
    onDouble: (Bet) -> Unit = {},
) {
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

                JassComposables.LastBetComposable(
                    lastBet = lastBet,
                    jassType = bettingState.jassType,
                    currentUserId = bettingState.currentUserId,
                    lastBetter = lastBetter,
                    onDouble = { onDouble(it) }
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
@Composable
fun BettingRow(
    bettingState: BettingState,
    players: List<PlayerData>,
    onBetPlace: (Bet) -> Unit = {},
    onPass: () -> Unit = {},
    onStartGame: () -> Unit = {},
    onDouble: (Bet) -> Unit = {},
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
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        } else {
            val lastBet = bettingState.bets.last()
            val lastBetter = players.first { it.id == lastBet.playerId }

            JassComposables.LastBetComposable(
                lastBet = lastBet,
                jassType = bettingState.jassType,
                currentUserId = bettingState.currentUserId,
                lastBetter = lastBetter,
                onDouble = { onDouble(it) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (selectedTrump != null) {
            Image(
                painter = painterResource(id = selectedTrump!!.asPicture()),
                contentDescription = selectedTrump!!.toString(),
                modifier = Modifier
                    .height(30.dp)
                    .align(Alignment.CenterVertically),
                alignment = Alignment.Center,
            )

            if (bettingState.jassType == JassType.COIFFEUR) {
                Text(
                    text = " x ${selectedTrump!!.ordinal + 1}",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(5.dp, 0.dp, 10.dp, 0.dp),
                )
            }
        }

        if (bettingState.availableTrumps().isNotEmpty()
            && (bettingState.jassType != JassType.SIDI_BARAHNI || bettingState.availableBets().isNotEmpty())) {
            if (bettingState.jassType == JassType.SIDI_BARAHNI || selectedTrump == null) {
                Text(
                    text = if (bettingState.jassType == JassType.SIDI_BARAHNI && selectedBet == BetHeight.NONE || selectedTrump == null) {
                        "Select a bet: "
                    } else {
                        selectedBet.toString()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(5.dp),
                )
            }

            Icon(icon, contentDescription = "Bet placing dropdown icon",
                modifier = Modifier
                    .clickable {
                        if (isBetDropdownExpanded || isTrumpDropdownExpanded) {
                            isBetDropdownExpanded = false
                            isTrumpDropdownExpanded = false
                        } else {
                            if (bettingState.jassType == JassType.SIDI_BARAHNI) isBetDropdownExpanded =
                                true
                            isTrumpDropdownExpanded = true
                        }
                    }
                    .align(Alignment.CenterVertically)
                    .padding(5.dp)
            )
        }

        if (bettingState.jassType == JassType.SIDI_BARAHNI) {
            DropdownMenu(
                expanded = isBetDropdownExpanded,
                onDismissRequest = { isBetDropdownExpanded = false },
                modifier = Modifier.testTag("betDropdown")
            ) {
                bettingState.availableBets().forEach { bet ->
                    Text(
                        text = bet.toString(),
                        modifier = Modifier
                            .clickable {
                                selectedBet = bet
                                isBetDropdownExpanded = false
                            }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
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

                Row (
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(
                            onClick = {
                                selectedTrump = trump
                                isTrumpDropdownExpanded = false
                            }
                        )
                ) {
                    Image(
                        painter = painterResource(id = trump.asPicture()),
                        contentDescription = trump.toString(),
                        modifier = Modifier
                            .height(50.dp)
                            .align(Alignment.CenterVertically)
                            .padding(
                                10.dp,
                                8.dp,
                                if (bettingState.jassType == JassType.COIFFEUR) 0.dp else 10.dp,
                                8.dp
                            ),
                        alignment = Alignment.Center,
                    )

                    if (bettingState.jassType == JassType.COIFFEUR) {
                        Text(
                            text = " x ${trump.ordinal + 1}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(5.dp, 0.dp, 10.dp, 0.dp),
                        )
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

        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

