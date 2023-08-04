package com.github.lucaengel.jass_entials.game.pregame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

/**
 * Activity for the Coiffeur pregame screen.
 */
class CoiffeurPregameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // todo: implement
                }
            }
        }
    }
}
