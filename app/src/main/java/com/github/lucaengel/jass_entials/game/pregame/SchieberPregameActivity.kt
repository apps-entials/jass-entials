package com.github.lucaengel.jass_entials.game.pregame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

/**
 * Activity for the Schieber pregame screen.
 */
class SchieberPregameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SchieberPreview()
                }
            }
        }
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun SchieberPreview() {

    JassentialsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BettingRound()
        }
    }
}
