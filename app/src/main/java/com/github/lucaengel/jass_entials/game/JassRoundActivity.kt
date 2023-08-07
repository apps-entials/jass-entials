package com.github.lucaengel.jass_entials.game

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
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
            .map { email -> email to CpuPlayer(email) }
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

    LaunchedEffect(key1 = gameState.currentPlayerEmail) {
        val currentPlayerEmail = gameState.currentPlayerEmail

        if (currentPlayerEmail == currentUserEmail || gameState.isLastTrick())
            return@LaunchedEffect

        val player = opponents.first { it.first == currentPlayerEmail }.second
        setToThinking(currentPlayerEmail)
        player.playCard(gameState, players.first { it.email == currentPlayerEmail })
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

            Text(text = "Trump: ${gameState.currentTrump}")
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

/**
 * Displays the 0-4 cards that are currently in the middle of the table.
 *
 * @param gameState The current game state.
 **/
@Composable
fun CurrentTrick(gameState: GameState, players: List<PlayerData>, currentUserIdx: Int, onClick: () -> Unit) {

    val currentTrick = gameState.currentTrick
    val startingPlayerIdx = gameState.playerEmails.indexOfFirst { it == gameState.startingPlayerEmail }

    // 0 is bottom, 1 is right, 2 is top, 3 is left
    val idxToCards = (0..3)
            // Triple: (location of card, i.e. 0 is bottom, ..., index of player in [players], z-index)
        .map { idx -> Triple(idx, (currentUserIdx + idx) % 4, Math.floorMod(idx - startingPlayerIdx, 4)) }
        .associate { (i, playerIdx, zIndex) -> i to Pair(currentTrick.trickCards.firstOrNull { it.email == players[playerIdx].email }?.card, zIndex) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth.value / 10
    val cardHeight = cardWidth * 1.5f

    // Displays the cards in the middle of the table.
    BoxWithConstraints(
        modifier = Modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.TopCenter
    ) {
        // Card at the top middle
        Column(modifier = Modifier.zIndex(idxToCards[2]?.second?.toFloat() ?: 0f)) {
            CardBox(card = idxToCards[2]?.first)
        }

        // Card at the bottom middle
        Column(modifier = Modifier.zIndex(idxToCards[0]?.second?.toFloat() ?: 0f)) {
            Spacer(modifier = Modifier.height((cardHeight * 2 / 3).dp))

            CardBox(card = idxToCards[0]?.first)
        }

        // Card in the middle on the left
        Column(modifier = Modifier.zIndex(idxToCards[3]?.second?.toFloat() ?: 0f)) {
            Spacer(modifier = Modifier.height((cardHeight / 3).dp))

            Row {
                CardBox(card = idxToCards[3]?.first)

                Spacer(modifier = Modifier.width((cardWidth * 1.25f).dp))
            }
        }

        // Card in the middle on the right
        Column(modifier = Modifier.zIndex(idxToCards[1]?.second?.toFloat() ?: 0f)){
            Spacer(modifier = Modifier.height((cardHeight / 3).dp))

            Row() {
                Spacer(modifier = Modifier.width((cardWidth * 1.25f).dp))

                CardBox(card = idxToCards[1]?.first)
            }
        }
    }
}

/**
 * Displays a jass card image for the given card.
 */
@Composable
fun CardBox(card: Card?) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth.value / 10 //* 1.5f
    val cardHeight = cardWidth * 1.5f

    Box(modifier = Modifier
        .requiredWidth(cardWidth.dp)
        .requiredHeight(cardHeight.dp)
    ) {
        if (card != null) {
            Image(
                painter = painterResource(id = Card.getCardImage(card)),
                contentDescription = card.toString(),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxSize(),
            )
        }
    }
}

