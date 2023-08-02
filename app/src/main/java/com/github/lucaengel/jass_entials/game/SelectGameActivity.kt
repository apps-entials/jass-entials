package com.github.lucaengel.jass_entials.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.game.SelectGameActivity.TestTags.Buttons.Companion.BACK
import com.github.lucaengel.jass_entials.game.pregame.CoiffeurPregameActivity
import com.github.lucaengel.jass_entials.game.pregame.SchieberPregameActivity
import com.github.lucaengel.jass_entials.game.pregame.SidiBarahniPreRoundActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class SelectGameActivity: ComponentActivity() {
    class TestTags {
        class Texts {
            companion object {
                private fun text(tag: String): String {
                    return "${tag}Text"
                }

                val ACTIVITY_TITLE = text("activity_title")
            }

        }

        class Buttons {
            companion object {
                private fun button(tag: String): String {
                    return "${tag}Button"
                }

                val BACK = button("back")
                val DONE = button("done")
            }
        }

        companion object {
            const val TITLE = "title"
            const val SCAFFOLD = "scaffold"
        }
    }

    private var currentEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getStringExtra("email")?.let {
            currentEmail = it
        }

        setContent {
            GroundTheme() { finish() }
        }
    }
}

/**
 * The main theme of the app.
 */
@Preview
@Composable
fun GroundTheme(finishActivity: () -> Unit = {}) {
    JassentialsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SelectGameView { finishActivity }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectGameView(finishActivity: () -> Unit = {}) {
    val context = LocalContext.current


    fun onGameTypeClicked(gameType: JassType) {
        when (gameType) {
            JassType.SCHIEBER ->
                Intent(context, SchieberPregameActivity::class.java).also {
                    context.startActivity(it)
                }

            JassType.COIFFEUR ->
                Intent(context, CoiffeurPregameActivity::class.java).also {
                    context.startActivity(it)
                }

            JassType.SIDI_BARAHNI ->
                Intent(context, SidiBarahniPreRoundActivity::class.java).also {
                    context.startActivity(it)
                }
        }
    }

    val gameTypes = JassType.values().toList()

    Scaffold(
        modifier = Modifier.testTag(SelectGameActivity.TestTags.SCAFFOLD),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select a game mode",
                        modifier = Modifier.testTag(SelectGameActivity.TestTags.Texts.ACTIVITY_TITLE)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            finishActivity()
                        },
                        modifier = Modifier.testTag(BACK)
                    ) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
//                actions = {
//                    IconButton(
//                        onClick = {
//
//                        },
//                        modifier = Modifier.testTag("DONE")
//                    ) {
//                        Icon(Icons.Filled.Done, "Done",)
//                    }
//                },
                colors = smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // TODO: uncomment this to enable dark mode for maxkeppeler sheets
            //CoachMeMaterial3Theme {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .verticalScroll(scrollState)
            ) {

                gameTypes.map {
                    TextRow(
                        testTag = it.jassName,
                        text = it.jassName,
                        onClick = {
                            onGameTypeClicked(it)
                        }
                    )

                    Divider()
                }
            }
        }
    }
}

@Composable
fun TextRow(testTag: String, text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .padding(20.dp, 10.dp, 20.dp, 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            modifier = Modifier
                .testTag(testTag)
                .padding(10.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}