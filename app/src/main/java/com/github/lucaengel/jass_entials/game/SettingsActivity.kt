package com.github.lucaengel.jass_entials.game

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.cards.CardType
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JassentialsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SettingsScreen { finish() }
                }
            }
        }
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(finishActivity: () -> Unit = {}) {
    var selectedCardType by remember { mutableStateOf(GameStateHolder.cardType) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            finishActivity()
                        },
                        modifier = Modifier.testTag(SelectGameActivity.TestTags.Buttons.BACK)
                    ) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
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
            Column {

                CardTypeSelector(selectedCardType) { cardType ->
                    selectedCardType = cardType
                    GameStateHolder.cardType = cardType
                }

                listOf(JassType.SCHIEBER, JassType.SIDI_BARRANI)
                    .map { jassType ->
                        Divider()

                        PointLimitSetter(
                            jassType = jassType,
                            onPointLimitSelected = { pointLimit ->
                                GameStateHolder.pointLimits += (jassType to pointLimit)
                            }
                        )
                    }
            }
        }
    }
}

@Composable
fun CardTypeSelector(
    selectedCardType: CardType,
    onCardTypeSelected: (CardType) -> Unit
) {
    Row (
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Select Playing Card Type:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(16.dp))
        CardType.values().forEach { cardType ->
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    selected = cardType == selectedCardType,
                    onClick = { onCardTypeSelected(cardType) }
                )
                Text(
                    text = cardType.string,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { onCardTypeSelected(cardType) }
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun PointLimitSetter(
    jassType: JassType,
    onPointLimitSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Set a point limit for $jassType",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(16.dp))

        val pointLimits = listOf(500, 1000, 1500, 2000, 2500, 3000, 3500, 4000)

        var isTrumpDropdownExpanded by remember { mutableStateOf(false) }

        DropdownMenu(
            expanded = isTrumpDropdownExpanded,
            onDismissRequest = { isTrumpDropdownExpanded = false },
            modifier = Modifier.testTag("betDropdown")
        ) {
            pointLimits.forEach { limit ->
                Text(
                    text = limit.toString(),
                    modifier = Modifier
                        .clickable {
                            onPointLimitSelected(limit)
                        }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

    }
}
