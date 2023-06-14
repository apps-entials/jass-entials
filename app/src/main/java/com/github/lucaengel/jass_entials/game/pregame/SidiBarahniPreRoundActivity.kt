package com.github.lucaengel.jass_entials.game.pregame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.AUTOMOTIVE_1024p
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Player
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.jass.JassTypes
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

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
                        Bet(player1, Suit.CLUBS, 40)
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
    bettingState: BettingState,
) {
    val context = LocalContext.current
    val currentPlayerIdx = bettingState.players.indexOfFirst { it.email == currentPlayer.email }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val topPlayer = bettingState.players[(currentPlayerIdx + 2) % 4]
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PlayerBox(player = topPlayer, bettingState = bettingState, playerSpot = 2)
        }

        Spacer(modifier = Modifier.weight(1f))

        MiddleRowInfo(bettingState, currentPlayerIdx)

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CurrentPlayerBox(player = currentPlayer, bettingState = bettingState, playerSpot = 0)
        }
    }
}

@Composable
private fun MiddleRowInfo(bettingState: BettingState, currentPlayerIdx: Int) {
    Row {

        val leftPlayer = bettingState.players[(currentPlayerIdx + 3) % 4]
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            PlayerBox(player = leftPlayer, bettingState = bettingState, playerSpot = 3)
        }

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
                    text = "${lastBet.bet} ${lastBet.suit.symbol} by\n" +
                        "${lastBet.player.firstName} ${lastBet.player.lastName}",
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        val rightPlayer = bettingState.players[(currentPlayerIdx + 1) % 4]

        Box(
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .fillMaxWidth(0.25F)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            PlayerBox (player = rightPlayer, bettingState = bettingState, playerSpot = 1)
        }
    }
}

/**
 * Player box for displaying information
 *
 * @param player The player to display
 * @param bettingState The current betting state
 * @param playerSpot The spot of the player in the game (0 = bottom, 1 = right, 2 = top, 3 = left)
 */
@Composable
fun PlayerBox(player: Player, bettingState: BettingState, playerSpot: Int) {
    val isCurrentUser = playerSpot == 0
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(5.dp)
    ) {
        Text(
            text = if (isCurrentUser) "You" else "${player.firstName} ${player.lastName}",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun CurrentPlayerBox(player: Player, bettingState: BettingState, playerSpot: Int) {
    PlayerBox(player = player, bettingState = bettingState, playerSpot = playerSpot)

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Row {
        for (card in player.cards) {
            Image(
                painter = painterResource(id = Card.getCardImage(card)),
                contentDescription = card.toString(),
                modifier = Modifier
                    .width(screenWidth / 10)
                    .padding(0.dp)

            )
        }
    }
}

