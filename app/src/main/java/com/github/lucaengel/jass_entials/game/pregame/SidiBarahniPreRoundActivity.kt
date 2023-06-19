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
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.JassTypes
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.game.JassComposables
import com.github.lucaengel.jass_entials.game.JassRoundActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme
import java.lang.Thread.sleep

/**
 * Sidi Barahni pre-round activity (i.e., betting round).
 */
class SidiBarahniPreRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("SidiBarahniPreRoundActivity.onCreate()")

        setContent {
            MyPreview()
        }
    }
}

// Landscape mode:
@Preview(device = AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
//@Preview
@Composable
fun MyPreview() {

    val player1 = Player("email_1", 0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123")
    val player2 = Player("email_2", 0, "first_2", "second_2", Deck.STANDARD_DECK.cards.subList(9, 18), 0, "123")
    val player3 = Player("email_3", 0, "first_3", "second_3", Deck.STANDARD_DECK.cards.subList(18, 27), 0, "123")
    val player4 = Player("email_4", 0, "first_4", "second_4", Deck.STANDARD_DECK.cards.subList(27, 36), 0, "123")


    val currentPlayer = player1

    JassentialsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BettingRound(
                currentPlayer,
                BettingState(
                    listOf(player1, player2, player3, player4),
                    player1,
                    JassTypes.SIDI_BARAHNI,
                    listOf(
                        Bet(player2, Trump.CLUBS, 40)
                    ),
                    GameState()
                )
            )
        }
    }
}

///**
// * Provides a [BettingState] for previewing.
// */
//class BettingRoundProvider : PreviewParameterProvider<BettingState> {
//    override val values: Sequence<BettingState> = sequenceOf(
//        BettingState(
//            listOf(Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123")),
//            Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123"),
//            JassTypes.SIDI_BARAHNI,
//            listOf(
//                Bet(Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123"), Suit.CLUBS, 40)
//            ),
//            GameState()
//        )
//    )
//}

@Composable
fun BettingRound(
    currentPlayer: Player,
    bState: BettingState,
) {
    val context = LocalContext.current
    var currentPlayerIdx by remember { mutableStateOf(0) }


    // REMOVE THIS ONCE LAYOUT IS FINE:
//    val player1 = Player("email_1", 0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123")
//    val player2 = Player("email_2", 0, "first_2", "second_2", Deck.STANDARD_DECK.cards.subList(9, 18), 0, "123")
//    val player3 = Player("email_3", 0, "first_3", "second_3", Deck.STANDARD_DECK.cards.subList(18, 27), 0, "123")
//    val player4 = Player("email_4", 0, "first_4", "second_4", Deck.STANDARD_DECK.cards.subList(27, 36), 0, "123")
//
//    var state = BettingState(
//            listOf(player1, player2, player3, player4),
//            player1,
//            JassTypes.SIDI_BARAHNI,
//            listOf(
//                Bet(player1, Trump.CLUBS, 40)
//            ),
//            GameState()
//        )
//    var bettingState by remember { mutableStateOf(state) }
    // -----------------------------

    var bettingState by remember { mutableStateOf(BettingState()) }

    LaunchedEffect(key1 = true) {
        bettingState = bState
        currentPlayerIdx = bettingState.players.indexOfFirst { it.email == currentPlayer.email }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayer = bettingState.players[(currentPlayerIdx + 2) % 4]

        JassComposables.PlayerBox(player = topPlayer, playerSpot = 2)

        Spacer(modifier = Modifier.weight(1f))

        if (bettingState.currentBetter == currentPlayer) {
            val simulatePlayers: () -> Unit = {
                sleep(500)
                bettingState = bettingState.nextPlayer()
                bettingState = bettingState.nextPlayer()
                bettingState = bettingState.nextPlayer()
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
                    val gameState = bettingState.startGame()

                    val intent = Intent(context, JassRoundActivity::class.java)

                    GameStateHolder.gameState = gameState
                    //TODO: adapt to new betting state for the next potential round???
                    GameStateHolder.bettingState = BettingState()

                    context.startActivity(intent)
                }
            )
        } else {

            MiddleRowInfo(bettingState = bettingState, currentPlayerIdx = currentPlayerIdx)

        }

        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(player = currentPlayer)
    }
}

@Composable
private fun MiddleRowInfo(bettingState: BettingState, currentPlayerIdx: Int) {
    Row {

        val leftPlayer = bettingState.players[(currentPlayerIdx + 3) % 4]
//        Box(
//            modifier = Modifier
//                .fillMaxHeight(0.5F)
//                .fillMaxWidth(0.25F)
//                .weight(1f),
//            contentAlignment = Alignment.Center
//        ) {
        JassComposables.PlayerBox(player = leftPlayer, playerSpot = 3, Modifier.fillMaxHeight(0.5F).fillMaxWidth(0.25f).weight(1f))
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
                        "${lastBet.player.firstName} ${lastBet.player.lastName}",
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayer = bettingState.players[(currentPlayerIdx + 1) % 4]

        JassComposables.PlayerBox(player = rightPlayer, playerSpot = 1, Modifier
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

    // all possible bets (40, ..., 150) --> still need to add match as possibility
    val possibleBets = (1..12)
        .map { 10*it + 30 }
        .filter { it > (bettingState.bets.lastOrNull()?.bet ?: 0) }

    var isBetDropdownExpanded by remember { mutableStateOf(false) }
    var isTrumpDropdownExpanded by remember { mutableStateOf(false) }
    var selectedBet by remember { mutableStateOf(0) }
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
                        "${lastBet.player.firstName} ${lastBet.player.lastName}",
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
                value = if (selectedBet < 40 || selectedTrump == null) "" else "$selectedBet ${selectedTrump!!}",
                onValueChange = { selectedBet = it.toInt() },
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
                if (selectedBet >= 40 && selectedTrump != null) {
                    onBetPlace(Bet(bettingState.currentBetter, selectedTrump!!, selectedBet))

                    selectedTrump = null
                    selectedBet = 0
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
                if (bettingState.bets.lastOrNull()?.player == bettingState.currentBetter) {
                    onStartGame()

                    selectedTrump = null
                    selectedBet = 0
                }
                else {
                    onPass()

                    selectedTrump = null
                    selectedBet = 0
                }
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(5.dp, 5.dp, 20.dp, 5.dp),
        ) {
            if (bettingState.bets.lastOrNull()?.player == bettingState.currentBetter)
                Text(text = "Start Game")
            else
                Text(text = "Pass")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

