package com.github.lucaengel.jass_entials.game

import android.os.Bundle
import android.widget.TableLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class JassRoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        Text(text = "Hello World!")
    }
}