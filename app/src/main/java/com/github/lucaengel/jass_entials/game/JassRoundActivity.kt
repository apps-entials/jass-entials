package com.github.lucaengel.jass_entials.game

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.game.JassComposables.Companion.CurrentTrick
import com.github.lucaengel.jass_entials.game.player.CpuPlayer
import com.github.lucaengel.jass_entials.game.postgame.SidiBarahniPostRoundActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

/**
 * Activity for the Jass round screen.
 */
class JassRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    JassRoundPreview()
                }
            }
        }
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun JassRoundPreview() {
    JassentialsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            JassRound()
        }
    }
}

/**
 * Composable for the Jass round screen.
 */
@Composable
fun JassRound() {
    val context = LocalContext.current

    var gameState by remember { mutableStateOf(GameStateHolder.gameState) }
    var players by remember { mutableStateOf(GameStateHolder.players) }

    val currentUserIdx by remember { mutableStateOf(gameState.currentUserIdx) }
    val currentUserEmail by remember { mutableStateOf(gameState.playerEmails[currentUserIdx]) }

    val opponents by remember {
        mutableStateOf(gameState.playerEmails
            .filter { email -> email != gameState.playerEmails[currentUserIdx] }
            .map { email -> email to CpuPlayer(playerEmail = email) }
        )}

    val nextTrickFun: () -> Unit = {
        gameState = gameState.nextTrick()

        if (gameState.isLastTrick()) {
            // Store current game state
            GameStateHolder.gameState = gameState
            GameStateHolder.players = players

            val postGameActivity = Intent(context, SidiBarahniPostRoundActivity::class.java)
            context.startActivity(postGameActivity)
        }
    }

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

    fun updatePlayer(email: String, card: Card) {
        players = players.map { player ->
            if (player.email == email) {
                player.withCardPlayed(card)
            } else {
                player
            }
        }
    }

    // this launched effect is responsible for the cpu players' actions
    LaunchedEffect(key1 = gameState.currentPlayerEmail) {
        val currentPlayerEmail = gameState.currentPlayerEmail

        if (currentPlayerEmail == currentUserEmail || gameState.isLastTrick())
            return@LaunchedEffect

        val player = opponents.first { it.first == currentPlayerEmail }.second
        setToThinking(currentPlayerEmail)
        player.cardToPlay(gameState, players.first { it.email == currentPlayerEmail })
            .thenAccept {
                setToNormalName(currentPlayerEmail)

                gameState = gameState.playCard(currentPlayerEmail, it, currentUserIdx)
                updatePlayer(currentPlayerEmail, it)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayerEmail = gameState.playerEmails[(currentUserIdx + 2) % 4]
        Row {
            JassComposables.PlayerBox(playerData = players.first { it.email == topPlayerEmail }, playerSpot = 2)

            Text(text = "Trump: ${gameState.winningBet}")
        }

        Spacer(modifier = Modifier.weight(1f))
        JassMiddleRowInfo(
            gameState = gameState,
            players = players,
            currentUserIdx = currentUserIdx,
            onClick = {
                if (gameState.currentTrick.isFull()) {
                    nextTrickFun()
                }
            })
        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(
            playerEmail = currentUserEmail,
            player = players[currentUserIdx],
        ) { card ->
            if (gameState.currentTrick.isFull()) {
                nextTrickFun()
                return@CurrentPlayerBox
            }

            if ((players.indexOfFirst { it.email == gameState.startingPlayerEmail }
                        + gameState.currentTrick.trickCards.size) % 4 != currentUserIdx) {
                Toast.makeText(
                    context,
                    "It is not your turn",
                    Toast.LENGTH_SHORT
                ).show()
                return@CurrentPlayerBox
            }

            if (!players[currentUserIdx]
                    .playableCards(gameState.currentTrick, gameState.currentTrump)
                    .contains(card)
            ) {
                Toast.makeText(
                    context,
                    "You can't play this card",
                    Toast.LENGTH_SHORT
                ).show()
                return@CurrentPlayerBox
            }

            gameState = gameState.playCard(currentUserEmail, card, currentUserIdx)

            updatePlayer(currentUserEmail, card)
            GameStateHolder.gameState = gameState
            GameStateHolder.players = players
        }
    }
}

/**
 * Composable for the current trick and the players in the middle (left and right opponents).
 */
@Composable
private fun JassMiddleRowInfo(gameState: GameState, players: List<PlayerData>, currentUserIdx: Int, onClick: () -> Unit) {
    Row() {
        val leftPlayer = gameState.playerEmails[(currentUserIdx + 3) % 4]
        JassComposables.PlayerBox(
            playerData = players.first { it.email == leftPlayer },
            playerSpot = 3,
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f)
        )

        Spacer(modifier = Modifier.weight(0.1f))
        CurrentTrick(
            gameState = gameState,
            players = players,
            currentUserIdx = currentUserIdx,
            onClick = onClick)
        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayer = gameState.playerEmails[(currentUserIdx + 1) % 4]
        JassComposables.PlayerBox(
            playerData = players.first { it.email == rightPlayer },
            playerSpot = 1, Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f)
        )
    }
}

