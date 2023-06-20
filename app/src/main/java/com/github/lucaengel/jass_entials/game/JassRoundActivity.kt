package com.github.lucaengel.jass_entials.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.Trump
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class JassRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameState = GameStateHolder.gameState
//        intent.getSerializableExtra("test", Test::class.java)

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
//@Preview
@Composable
fun JassRoundPreview() {

    val player1 = Player("email_1", 0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123")
    val player2 = Player("email_2", 0, "first_2", "second_2", Deck.STANDARD_DECK.cards.subList(9, 18), 0, "123")
    val player3 = Player("email_3", 0, "first_3", "second_3", Deck.STANDARD_DECK.cards.subList(18, 27), 0, "123")
    val player4 = Player("email_4", 0, "first_4", "second_4", Deck.STANDARD_DECK.cards.subList(27, 36), 0, "123")


    val currentPlayer = player1
    val players = listOf(player1, player2, player3, player4)

    JassentialsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            JassRound(
                currentPlayer,
//                GameStateHolder.gameState
                GameState(
                    players,
                    player1,
                    player3,
                    1,
                    players.mapIndexed { index, player ->
                        player to Deck.STANDARD_DECK.cards[index]
                    }.toMap(),
                    1,
                    Trump.UNGER_UFE,
                    Deck.STANDARD_DECK.dealCards(players),
                )
            )
        }
    }
}


@Composable
fun JassRound(
    currentPlayer: Player,
    gState: GameState,
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

    var gameState by remember { mutableStateOf(GameStateHolder.gameState) }

    LaunchedEffect(key1 = true) {
        gameState = gState
        currentPlayerIdx = gameState.players.indexOfFirst { it.email == currentPlayer.email }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val topPlayer = gameState.players[(currentPlayerIdx + 2) % 4]

        JassComposables.PlayerBox(player = topPlayer, playerSpot = 2)

        Spacer(modifier = Modifier.weight(1f))


        JassMiddleRowInfo(gameState = gameState, currentPlayerIdx = currentPlayerIdx)


        Spacer(modifier = Modifier.weight(1f))

        JassComposables.CurrentPlayerBox(player = currentPlayer)
    }
}

@Composable
private fun JassMiddleRowInfo(gameState: GameState, currentPlayerIdx: Int) {
    Row() {

        val leftPlayer = gameState.players[(currentPlayerIdx + 3) % 4]

        JassComposables.PlayerBox(
            player = leftPlayer,
            playerSpot = 3,
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f))

        Spacer(modifier = Modifier.weight(0.1f))


        CurrentTrick(gameState = gameState, currentPlayerIdx = currentPlayerIdx)


        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayer = gameState.players[(currentPlayerIdx + 1) % 4]

        JassComposables.PlayerBox(player = rightPlayer, playerSpot = 1, Modifier
            .fillMaxHeight(0.5F)
            .fillMaxWidth(0.25F)
            .weight(1f))
    }
}

/**
 * Displays the 0-4 cards that are currently in the middle of the table.
 *
 * @param gameState The current game state.
 **/
@Composable
fun CurrentTrick(gameState: GameState, currentPlayerIdx: Int) {

    val currentTrick = gameState.currentTrick

    val startingPlayerIdx = gameState.players.indexOf(gameState.startingPlayer)

    // 0 is bottom, 1 is right, 2 is top, 3 is left
    val idxToCards = (0..3)
        .map { idx -> Triple(idx, gameState.players[(currentPlayerIdx + idx) % 4], Math.floorMod(currentPlayerIdx + idx - startingPlayerIdx, 4)) }
        .associate { (i, player, zIndex) -> i to Pair(currentTrick.getOrDefault(player, null), zIndex) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth.value / 10
    val cardHeight = cardWidth * 1.5f

    BoxWithConstraints(
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.zIndex(idxToCards[2]?.second?.toFloat() ?: 0f)) {
            CardBox(
                card = idxToCards[2]?.first,
                modifier = Modifier.zIndex(1f)
            )
        }
        Column(modifier = Modifier.zIndex(idxToCards[0]?.second?.toFloat() ?: 0f)) {
            Spacer(modifier = Modifier.height((cardHeight * 2 / 3).dp))

            CardBox(
                card = idxToCards[0]?.first,
                modifier = Modifier.zIndex(4f)
            )
        }

        Column(modifier = Modifier.zIndex(idxToCards[3]?.second?.toFloat() ?: 0f)) {
            Spacer(modifier = Modifier.height((cardHeight / 3).dp))

            Row {
                CardBox(
                    card = idxToCards[3]?.first,
                    modifier = Modifier.zIndex(2f)
                )
                Spacer(modifier = Modifier.width((cardWidth * 1.25f).dp))
            }
        }
        Column(modifier = Modifier.zIndex(idxToCards[1]?.second?.toFloat() ?: 0f)){
            Spacer(modifier = Modifier.height((cardHeight / 3).dp))

            Row() {
                Spacer(modifier = Modifier.width((cardWidth * 1.25f).dp))

                CardBox(card = idxToCards[1]?.first, modifier = Modifier.zIndex(3f))
            }
        }
    }
}

@Composable
fun CardBox(card: Card?, modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth.value / 10 //* 1.5f
    val cardHeight = cardWidth * 1.5f

    Box(modifier = modifier
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

