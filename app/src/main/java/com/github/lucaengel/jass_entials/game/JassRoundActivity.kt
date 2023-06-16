package com.github.lucaengel.jass_entials.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class JassRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {

            val gameState = intent.getSerializableExtra("gameState") as GameState
            println("retrieved data form intent: $gameState")
        } catch (e: Exception) {
            println("error retrieving data from intent!!!!!!!!!!!!!!!!!")
            println("error retrieving data from intent: ${e.message}")
        }
//        intent.getSerializableExtra("test", Test::class.java)

        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TableLayout()
                }
            }
        }
    }
}

@Composable
fun TableLayout() {

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Text(text = "Jass Round Activity!")
    }
}