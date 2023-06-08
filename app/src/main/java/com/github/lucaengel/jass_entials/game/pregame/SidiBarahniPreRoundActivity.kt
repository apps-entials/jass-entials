package com.github.lucaengel.jass_entials.game.pregame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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


@Preview
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
        Text(
            text = "${topPlayer.firstName} ${topPlayer.lastName}",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 10.dp)
                )
                .padding(5.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        MiddleRowInfo(bettingState, currentPlayerIdx)

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "You",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 0.dp)
                )
                .padding(5.dp),
        )
    }
}

@Composable
private fun MiddleRowInfo(bettingState: BettingState, currentPlayerIdx: Int) {
    Row {

        val leftPlayer = bettingState.players[(currentPlayerIdx + 3) % 4]
        Text(
            text = "${leftPlayer.firstName} ${leftPlayer.lastName}",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 0.dp)
                )
                .padding(5.dp),
            textAlign = TextAlign.End,
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(5.dp),
        ) {

            if (bettingState.bets.isEmpty()) {
                Text(text = "No bets yet")
            } else {
                val lastBet = bettingState.bets.last()

                Text(text = "Current bet: ${lastBet.bet} ${lastBet.suit.symbol}\n" +
                        "by ${lastBet.player.firstName} ${lastBet.player.lastName}"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val rightPlayer = bettingState.players[(currentPlayerIdx + 1) % 4]
        Text(
            text = "${rightPlayer.firstName} ${rightPlayer.lastName}",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp, 10.dp, 0.dp, 10.dp)
                )
                .padding(5.dp),
            textAlign = TextAlign.Start,
        )
    }
}
