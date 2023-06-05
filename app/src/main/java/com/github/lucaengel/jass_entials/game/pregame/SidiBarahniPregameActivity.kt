package com.github.lucaengel.jass_entials.game.pregame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.game.JassRoundActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class SidiBarahniPregameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    BettingRound()
                }
            }
        }
    }
}


@Preview
@Composable
fun BettingRound() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Player 1",
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 10.dp)
                )
                .padding(5.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Row {
            Text(
                text = "Player 2",
                modifier = Modifier.align(Alignment.CenterVertically)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 0.dp)
                    )
                    .padding(5.dp),
                textAlign = TextAlign.End,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Player 4",
                modifier = Modifier.align(Alignment.CenterVertically)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp, 10.dp, 0.dp, 10.dp)
                    )
                    .padding(5.dp),
                textAlign = TextAlign.Start,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Player 3",
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 0.dp)
                )
                .padding(5.dp),
        )
    }
}
