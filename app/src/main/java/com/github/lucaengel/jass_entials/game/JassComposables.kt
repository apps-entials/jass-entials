package com.github.lucaengel.jass_entials.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Player
import kotlin.math.absoluteValue

class JassComposables {

    companion object {
        @Composable
        fun CurrentPlayerBox(player: Player, isPlayingRound: Boolean = false) {

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp

//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .width(screenWidth),
////                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.Bottom
//            ) {

                val cardWidth = screenWidth.value / 10 * 1.5f
                val cardHeight = cardWidth * 1.5f

                val nbCards = player.cards.size
                val displacements = (0 until nbCards)
                    .map { (it - (nbCards / 2)).absoluteValue * (cardWidth * 1.2) }// - if (nbCards % 2 == 0) cardWidth / 2 else 0f }

                println("displacements: $displacements")
                BoxWithConstraints(
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    player.cards.mapIndexed { idx, card ->
                        Row {
                            if (idx > nbCards / 2) Spacer(modifier = Modifier.width(displacements[idx].dp))

                            Column {
                                Spacer(modifier = Modifier.height(((idx - nbCards / 2).absoluteValue * (cardHeight / 9f)).dp))

                                Image(
                                    painter = painterResource(id = Card.getCardImage(card)),
                                    contentDescription = card.toString(),
                                    modifier = Modifier
                                        .requiredHeight((cardHeight).dp)
                                        .requiredWidth(cardWidth.dp)
                                        .graphicsLayer {
                                            rotationZ = (idx - (nbCards / 2)) * 5f
                                        },
                                )
                            }
                            if (idx <= nbCards / 2) Spacer(modifier = Modifier.width(displacements[idx].dp))

                        }
                    }
                }

//                val middle = (player.cards.size / 2f) - 0.5f
////        player.cards.zip(adjustments).mapIndexed { idx, (card, xTranslate) ->
//                player.cards.mapIndexed { idx, card ->
//                    // index in the list of cards
//                    var cardTranslationX = 0f
//                    var cardTranslationY = 0f
//
//                    Box(
//                        modifier = Modifier
//                            .graphicsLayer {
//                                rotationZ = (idx - middle) * 5f
//                                translationY = (idx - middle)*(idx - middle)*0.05f*cardHeight
////                        translationX = cardTranslationX
//                                translationX = /*xTranslate*/(idx - middle) * cardWidth * (-0.5f)
//                            }
//                            .requiredWidth(cardWidth.dp)
//                            .requiredHeight(cardHeight.dp),
////                    .background(
////                        MaterialTheme.colorScheme.primaryContainer,
////                        shape = RoundedCornerShape(10.dp)
////                    ),
//                        contentAlignment = Alignment.BottomCenter
//
//                    ) {
//                        Image(
//                            painter = painterResource(id = Card.getCardImage(card)),
//                            contentDescription = card.toString(),
//                            modifier = Modifier
//                                .fillMaxHeight()
//                                .align(Alignment.BottomCenter)
//                                .fillMaxSize()
//                        )
//                    }
//                }
//            }
//    PlayerBox(player = player, bettingState = bettingState, playerSpot = playerSpot)
        }

        /**
         * Player box for displaying information
         *
         * @param player The player to display
         * @param playerSpot The spot of the player in the game (0 = bottom, 1 = right, 2 = top, 3 = left)
         */
        @Composable
        fun PlayerBox(player: Player, playerSpot: Int, modifier: Modifier = Modifier) {
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
                        text = if (isCurrentUser) "You" else "${player.firstName} ${player.lastName}",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}