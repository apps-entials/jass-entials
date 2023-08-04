package com.github.lucaengel.jass_entials.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import kotlin.math.absoluteValue

class JassComposables {

    companion object {

        /**
         * Displays the current player's cards.
         *
         * @param playerData The current player's data.
         * @param onPlayCard The callback to be called when a card is played.
         */
        @Composable
        fun CurrentPlayerBox(playerData: PlayerData, onPlayCard: (Card) -> Unit = {}) {

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val cardWidth = screenWidth.value / 10 * 1.5f
            val cardHeight = cardWidth * 1.5f

            val nbCards = playerData.cards.size
            val cardNbIsEven = nbCards % 2 == 0

            // offset, rotation
            val displacements = (if (!cardNbIsEven) (0 until nbCards) else (0 .. nbCards).filter { it != nbCards / 2 })
                .map {
                    // move cards closer to the middle (to compensate for the 'missing' middle card)
                    val evenCardsCorrection = if (cardNbIsEven) cardWidth / 2 * 1.1f else 0f
                    Pair(
                        (it - (nbCards / 2)).absoluteValue * (1.2 * cardWidth) - 1.2 * evenCardsCorrection,
                        (it - (nbCards / 2)) * 5f
                    )
                }

            BoxWithConstraints(
                contentAlignment = Alignment.BottomCenter,
            ) {
                playerData.cards.mapIndexed { idx, card ->
                    val shouldPlaceCardRight = idx >= nbCards / 2
                    Row {
                        if (shouldPlaceCardRight) Spacer(modifier = Modifier.width(displacements[idx].first.dp))

                        val heightLevel = (
                                if (cardNbIsEven && idx >= nbCards / 2) (idx+1 - nbCards / 2)
                                else (idx - nbCards / 2)
                            ).absoluteValue
                        Column(modifier = Modifier.height(((heightLevel * (cardHeight / 9f)) + cardHeight).dp)) {
                            Spacer(modifier = Modifier.height((heightLevel * (cardHeight / 9f)).dp))

                            Image(
                                painter = painterResource(id = Card.getCardImage(card)),
                                contentDescription = card.toString(),
                                modifier = Modifier
                                    .requiredHeight((cardHeight).dp)
                                    .requiredWidth(cardWidth.dp)
                                    .graphicsLayer {
                                        rotationZ =
                                            displacements[idx].second
                                    }
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable {
                                        println("card was clicked!!!")
                                        onPlayCard(card) },
                            )
                        }
                        if (!shouldPlaceCardRight) Spacer(modifier = Modifier.width(displacements[idx].first.dp))

                    }
                }
            }
        }

        /**
         * Player box for displaying information
         *
         * @param playerData The player to display
         * @param playerSpot The spot of the player in the game (0 = bottom, 1 = right, 2 = top, 3 = left)
         * @param modifier The modifier to apply to the box
         */
        @Composable
        fun PlayerBox(playerData: PlayerData, playerSpot: Int, modifier: Modifier = Modifier) {
            val isCurrentUser = playerSpot == 0
            Box(
                modifier = modifier.fillMaxWidth(0.25f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(5.dp)
                ) {
                    Text(
                        text = if (isCurrentUser) "You" else "${playerData.firstName} ${playerData.lastName}",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}