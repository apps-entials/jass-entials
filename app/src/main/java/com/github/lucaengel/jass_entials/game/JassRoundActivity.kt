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
    val currentPlayerIdx by remember { mutableStateOf(gameState.currentPlayerIdx) }
    val currentPlayerEmail by remember { mutableStateOf(gameState.playerEmails[currentPlayerIdx]) }
    val gameStateHolderPlayerIdx = GameStateHolder.players.indexOfFirst { it.email == currentPlayerEmail }
    val opponents by remember {
        mutableStateOf(gameState.playerEmails
            .filter { it != gameState.playerEmails[gameState.currentPlayerIdx] }
            .map { it to CpuPlayer(it) }
        )}

    val nextTrickFun: () -> Unit = {
        gameState = gameState.nextTrick()

        if (gameState.isLastTrick()) {
            GameStateHolder.gameState = gameState

            val postGameActivity = Intent(context, SidiBarahniPostRoundActivity::class.java)
            context.startActivity(postGameActivity)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayerEmail = gameState.playerEmails[(currentPlayerIdx + 2) % 4]
        Row {
            JassComposables.PlayerBox(playerData = GameStateHolder.players.first { it.email == topPlayerEmail }, playerSpot = 2)

            Text(text = "Trump: ${gameState.currentTrump}")
        }

        Spacer(modifier = Modifier.weight(1f))
        JassMiddleRowInfo(
            gameState = gameState,
            currentPlayerIdx = currentPlayerIdx,
            onClick = {
                if (gameState.currentTrick.isFull()) {
                    nextTrickFun()
                }
            })
        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(
            playerEmail = currentPlayerEmail,
        ) { card ->
            if (gameState.currentTrick.isFull()) {
                nextTrickFun()
                return@CurrentPlayerBox
            }

            if (gameState.currentTrick.trickCards.size != currentPlayerIdx) {
                Toast.makeText(
                    context,
                    "It is not your turn",
                    Toast.LENGTH_SHORT
                ).show()
                return@CurrentPlayerBox
            }

            if (!GameStateHolder.players[gameStateHolderPlayerIdx]
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

            gameState = gameState.playCard(currentPlayerEmail, card)
            GameStateHolder.gameState = gameState
            GameStateHolder.players = GameStateHolder.players.mapIndexed { idx, player ->
                if (idx == gameStateHolderPlayerIdx) {
                    player.copy(cards = player.cards.minus(card))
                } else {
                    player
                }
            }

            // Have opponents play
            opponents[0].second
                .playCard(gameState)
                .thenCompose {
                    gameState = gameState.playCard(opponents[0].first, it)

                    opponents[1].second
                        .playCard(gameState)
                }.thenCompose {
                    gameState = gameState.playCard(opponents[1].first, it)

                    opponents[2].second
                        .playCard(gameState)
                }.thenAccept {
                    gameState = gameState.playCard(opponents[1].first, it)
                }
        }
    }
}

/**
 * Composable for the current trick and the players in the middle (left and right opponents).
 */
@Composable
private fun JassMiddleRowInfo(gameState: GameState, currentPlayerIdx: Int, onClick: () -> Unit) {
    Row() {
        val leftPlayer = gameState.playerEmails[(currentPlayerIdx + 3) % 4]
        JassComposables.PlayerBox(
            playerData = GameStateHolder.players.first { it.email == leftPlayer },
            playerSpot = 3,
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f)
        )

        Spacer(modifier = Modifier.weight(0.1f))
        CurrentTrick(gameState = gameState, currentPlayerIdx = currentPlayerIdx, onClick = onClick)
        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayer = gameState.playerEmails[(currentPlayerIdx + 1) % 4]
        JassComposables.PlayerBox(
            playerData = GameStateHolder.players.first { it.email == rightPlayer },
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
fun CurrentTrick(gameState: GameState, currentPlayerIdx: Int, onClick: () -> Unit) {

    val currentTrick = gameState.currentTrick
    val startingPlayerIdx by remember { mutableStateOf(gameState.playerEmails.indexOfFirst { it == gameState.startingPlayerEmail }) }

    // 0 is bottom, 1 is right, 2 is top, 3 is left
    val idxToCards = (0..3)
        .map { idx -> Triple(idx, (currentPlayerIdx + idx) % 4, Math.floorMod(idx - startingPlayerIdx, 4)) }
        .associate { (i, playerIdx, zIndex) -> i to Pair(currentTrick.trickCards.map { it.card }.getOrNull(playerIdx), zIndex) }

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

