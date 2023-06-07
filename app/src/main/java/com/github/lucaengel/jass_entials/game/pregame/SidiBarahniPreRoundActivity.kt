package com.github.lucaengel.jass_entials.game.pregame

import android.os.Bundle
import android.text.Html
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
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

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    BettingRound(
                        BettingState(
                            listOf(Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123")),
                            Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123"),
                            JassTypes.SIDI_BARAHNI,
                            listOf(),
                            GameState()
                        )
                    )
                }
            }
        }
    }
}

/**
 * Provides a [BettingState] for previewing.
 */
class BettingRoundProvider : PreviewParameterProvider<BettingState> {
    override val values: Sequence<BettingState> = sequenceOf(
        BettingState(
            listOf(Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123")),
            Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123"),
            JassTypes.SIDI_BARAHNI,
            listOf(
                Bet(Player(0, "first_1", "second_1", Deck.STANDARD_DECK.cards.subList(0, 9), 0, "123"), Suit.CLUBS, 40)
            ),
            GameState()
        )
    )
}

@Preview
@Composable
fun BettingRound(
    @PreviewParameter(BettingRoundProvider::class) bettingState: BettingState,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Player 1",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 10.dp)
                )
                .padding(5.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        MiddleRowInfo(bettingState)

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Player 3",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 0.dp)
                )
                .padding(5.dp),
        )
    }
}

@Composable
private fun MiddleRowInfo(bettingState: BettingState) {
    Row {
        Text(
            text = "Player 2",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(
                    MaterialTheme.colorScheme.surface,
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

        Text(
            text = "Player 4",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp, 10.dp, 0.dp, 10.dp)
                )
                .padding(5.dp),
            textAlign = TextAlign.Start,
        )
    }
}
