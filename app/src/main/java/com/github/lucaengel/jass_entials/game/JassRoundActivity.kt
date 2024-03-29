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
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.game.JassComposables.Companion.CurrentTrick
import com.github.lucaengel.jass_entials.game.player.DelayedCpuPlayer
import com.github.lucaengel.jass_entials.game.postgame.CoiffeurPostRoundActivity
import com.github.lucaengel.jass_entials.game.postgame.SchieberPostRoundActivity
import com.github.lucaengel.jass_entials.game.postgame.SidiBarraniPostRoundActivity
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

    val currentUserId by remember { mutableStateOf(gameState.currentUserId) }

    val opponents by remember {
        mutableStateOf(PlayerId.values()
            .filter { id -> id != currentUserId }
            .map { id -> id to DelayedCpuPlayer(playerId = id) }
        )}

    val nextTrickFun: () -> Unit = {
        gameState = gameState.nextTrick()

        println("${players[0].id} cards: ${players[0].cards}")
        println("${players[1].id} cards: ${players[1].cards}")
        println("${players[2].id} cards: ${players[2].cards}")
        println("${players[3].id} cards: ${players[3].cards}")

        if (gameState.isLastTrick()) {

            // Store current game state
            GameStateHolder.gameState = gameState
            GameStateHolder.players = players
            GameStateHolder.prevRoundScores = GameStateHolder.prevRoundScores + Pair(gameState.winningBet, gameState.roundState.score())



            val postGameActivity = when (gameState.jassType) {
                JassType.SCHIEBER ->
                    SchieberPostRoundActivity::class.java
                JassType.COIFFEUR ->
                    CoiffeurPostRoundActivity::class.java
                JassType.SIDI_BARRANI -> {
                    // add bet points to score
                    val bet = gameState.winningBet
                    val madeEnoughPoints = gameState.roundState.score().roundPoints(bet.playerId.teamId()) >= bet.bet.asInt()
                    val betPoints = if (gameState.winningBet.doubledBy != null) bet.bet.asInt() * 2 else bet.bet.asInt()

                    gameState = gameState.copy(
                        roundState = gameState.roundState.copy(
                            score = gameState.roundState.score()
                                .withBonusAddedToGameScore(bet.playerId.teamId().let {
                                    if (madeEnoughPoints) it
                                    else it.otherTeam() }
                                    , betPoints
                                )
                        )
                    )
                    GameStateHolder.gameState = gameState

                    SidiBarraniPostRoundActivity::class.java
                }
            }

            context.startActivity(Intent(context, postGameActivity))
        }
    }

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

    fun updatePlayer(playerId: PlayerId, card: Card) {
        players = players.map { player ->
            if (player.id == playerId) {
                player.withCardPlayed(card)
            } else {
                player
            }
        }
    }

    // this launched effect is responsible for the cpu players' actions
    LaunchedEffect(key1 = gameState.currentPlayerId) {
        val currentPlayerId = gameState.currentPlayerId

        if (currentPlayerId == currentUserId || gameState.isLastTrick())
            return@LaunchedEffect

        val player = opponents.first { it.first == currentPlayerId }.second
        setToThinking(currentPlayerId)
        player.cardToPlay(gameState.roundState, players.first { it.id == currentPlayerId }.cards)
            .thenAccept {
                setToNormalName(currentPlayerId)

                gameState = gameState.playCard(currentPlayerId, it)
                updatePlayer(currentPlayerId, it)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayerId = currentUserId.nextPlayer().nextPlayer()
        Row (verticalAlignment = Alignment.CenterVertically) {
            JassComposables.PlayerBox(playerData = players.first { it.id == topPlayerId }, playerSpot = 2)

            JassComposables.LastBetComposable(
                lastBet = gameState.winningBet,
                jassType = gameState.jassType,
                currentUserId = gameState.currentUserId,
                lastBetter = players[gameState.winningBet.playerId.ordinal],
                onTheSameRow = true,
            )

        }

        Spacer(modifier = Modifier.weight(1f))

        JassMiddleRowInfo(
            gameState = gameState,
            players = players,
            currentUserId = currentUserId
        ) {
            if (gameState.roundState.trick().isFull()) {
                nextTrickFun()
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(
            player = players[currentUserId.ordinal],
        ) { card ->
            if (gameState.roundState.trick().isFull()) {
                nextTrickFun()
                return@CurrentPlayerBox
            }

            if ((players.indexOfFirst { it.id == gameState.startingPlayerId }
                        + gameState.roundState.trick().cards.size) % 4 != currentUserId.ordinal) {
                Toast.makeText(
                    context,
                    "It is not your turn",
                    Toast.LENGTH_SHORT
                ).show()
                return@CurrentPlayerBox
            }

            if (!players[currentUserId.ordinal]
                    .playableCards(gameState.roundState.trick(), gameState.roundState.trick().trump)
                    .contains(card)
            ) {
                Toast.makeText(
                    context,
                    "You can't play this card",
                    Toast.LENGTH_SHORT
                ).show()
                return@CurrentPlayerBox
            }

            gameState = gameState.playCard(currentUserId, card)

            updatePlayer(currentUserId, card)
            GameStateHolder.gameState = gameState
            GameStateHolder.players = players
        }
    }
}

/**
 * Composable for the current trick and the players in the middle (left and right opponents).
 */
@Composable
private fun JassMiddleRowInfo(gameState: GameState, players: List<PlayerData>, currentUserId: PlayerId, onClick: () -> Unit) {
    Row {
        val leftPlayer = currentUserId
            .nextPlayer()
            .nextPlayer()
            .nextPlayer()
        JassComposables.PlayerBox(
            playerData = players.first { it.id == leftPlayer },
            playerSpot = 3,
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f)
        )

        Spacer(modifier = Modifier.weight(0.1f))
        CurrentTrick(
            gameState = gameState,
            currentUserId = currentUserId,
            onClick = onClick
        )
        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayer = currentUserId.nextPlayer()
        JassComposables.PlayerBox(
            playerData = players.first { it.id == rightPlayer },
            playerSpot = 1, Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f)
        )
    }
}

