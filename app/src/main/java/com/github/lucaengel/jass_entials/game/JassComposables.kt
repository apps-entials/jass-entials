package com.github.lucaengel.jass_entials.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import kotlin.math.absoluteValue

/**
 * Reusable composables for the Jass game.
 */
class JassComposables {

    companion object {

        /**
         * Displays the current player's cards.
         *
         * @param onPlayCard The callback to be called when a card is played.
         */
        @Composable
        fun CurrentPlayerBox(player: PlayerData, onPlayCard: (Card) -> Unit = {}) {

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val cardWidth = screenWidth.value / 10 * 1.5f
            val cardHeight = cardWidth * 1.5f


            val nbCards = player.cards.size
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
                player.cards.mapIndexed { idx, card ->
                    val shouldPlaceCardRight = idx >= nbCards / 2
                    Row {
                        if (shouldPlaceCardRight) Spacer(modifier = Modifier.width(displacements[idx].first.dp))

                        val heightLevel = (
                                if (cardNbIsEven && idx >= nbCards / 2) (idx+1 - nbCards / 2)
                                else (idx - nbCards / 2)
                            ).absoluteValue
                        Column(modifier = Modifier.height(((heightLevel * (cardHeight / 9f)) + cardHeight).dp)) {
                            Spacer(modifier = Modifier.height((heightLevel * (cardHeight / 9f)).dp))

                            CardBox(
                                card = card,
                                onClick = { onPlayCard(card) },
                                cardWidth = cardWidth.dp,
                                zRotation = displacements[idx].second,
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
            val isCurrentUser = (playerSpot == 0)
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

        /**
         * Displays the 0-4 cards that are currently in the middle of the table.
         *
         * @param gameState The current game state.
         * @param players The list of playerData's.
         * @param currentUserId The id of the current user.
         * @param onClick The callback to be called when a card is clicked.
         **/
        @Composable
        fun CurrentTrick(gameState: GameState, players: List<PlayerData>, currentUserId: PlayerId, onClick: () -> Unit) {

            val currentTrick = gameState.roundState.trick()
            gameState.startingPlayerId.ordinal
            val startingPlayerId = gameState.startingPlayerId

            // playerId to (card, z-index)
            val playerToCard = mapOf(
                startingPlayerId to Pair(currentTrick.cards.getOrNull(0), 0),
                startingPlayerId.nextPlayer() to Pair(currentTrick.cards.getOrNull(1), 1),
                startingPlayerId.nextPlayer().nextPlayer() to Pair(currentTrick.cards.getOrNull(2), 2),
                startingPlayerId.nextPlayer().nextPlayer().nextPlayer() to Pair(currentTrick.cards.getOrNull(3), 3),
            )

            // 0 is bottom, 1 is right, 2 is top, 3 is left
//            val idxToCards = (0..3)
//                // Triple: (location of card (i.e. 0 is bottom, ...), index of player in [players], z-index)
//                .map { idx -> Triple(idx, (currentUserId.ordinal + idx) % 4, Math.floorMod(idx - startingPlayerIdx, 4)) }
//                .associate { (i, playerIdx, zIndex) -> i to Pair(currentTrick.cards.firstOrNull { it.playerId == players[playerIdx].id }?.card, zIndex) }

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val cardWidth = screenWidth.value / 10
            val cardHeight = cardWidth * 1.5f

            // Displays the cards in the middle of the table.
            BoxWithConstraints(
                contentAlignment = Alignment.TopCenter
            ) {

                // commented out parts in the spacer are for the old layout where all cards were upright
                // (also need to remove rotate modifier to go back)

                // Card at the top middle
                Column(modifier = Modifier.zIndex(playerToCard[currentUserId.teamMate()]?.second?.toFloat() ?: 0f)) {
                    CardBox(
                        card = playerToCard[currentUserId.teamMate()]?.first,
                        onClick = onClick,
                        cardWidth = cardWidth.dp
                    )
                }

                // Card at the bottom middle
                Column(modifier = Modifier.zIndex(playerToCard[currentUserId]?.second?.toFloat() ?: 0f)) {
                    Spacer(modifier = Modifier.height((cardHeight - 2 * cardWidth / 3).dp) /*(cardHeight * 2 / 3).dp*/)

                    CardBox(
                        card = playerToCard[currentUserId]?.first,
                        onClick = onClick,
                        cardWidth = cardWidth.dp
                    )
                }

                // Card in the middle on the left
                Column(modifier = Modifier.zIndex(playerToCard[currentUserId.teamMate().nextPlayer()]?.second?.toFloat() ?: 0f)) {
                    // if it should be on the same height as the end of the top card, use: cardWidth
                    Spacer(modifier = Modifier.height(((cardHeight - 2 * cardWidth / 3) / 2).dp/*(cardHeight / 3).dp*/))

                    Row {
                        CardBox(
                            card = playerToCard[currentUserId.teamMate().nextPlayer()]?.first,
                            onClick = onClick,
                            cardWidth = cardWidth.dp,
                            zRotation = 90f
                        )

                        Spacer(modifier = Modifier.width((cardHeight - 2 * cardWidth / 3).dp/*cardWidth * 1.25f).dp*/))
                    }
                }

                // Card in the middle on the right
                Column(modifier = Modifier.zIndex(playerToCard[currentUserId.nextPlayer()]?.second?.toFloat() ?: 0f)) {
                    Spacer(modifier = Modifier.height(((cardHeight - 2 * cardWidth / 3) / 2).dp/*(cardHeight / 3).dp*/))

                    Row {
                        Spacer(modifier = Modifier.width((cardHeight - 2 * cardWidth / 3/*cardWidth * 1.25f*/).dp))

                        CardBox(
                            card = playerToCard[currentUserId.nextPlayer()]?.first,
                            onClick = onClick,
                            cardWidth = cardWidth.dp,
                            zRotation = 90f
                        )
                    }
                }
            }
        }

        /**
         * Displays a jass card image for the given card.
         *
         * @param card The card to display.
         */
        @Composable
        fun CardBox(card: Card?, onClick: () -> Unit = {}, cardWidth: Dp, zRotation: Float = 0f) {
            val cardHeight = cardWidth * 1.5f
            val cornerShape = cardWidth / 12

            if (card != null) {
                Image(
                    painter = painterResource(id = Card.getCardImage(card)),
                    contentDescription = card.toString(),
                    modifier = Modifier
                        .requiredWidth(cardWidth)
                        .requiredHeight(cardHeight)
                        .graphicsLayer {
                            rotationZ = zRotation
                        }.border(
                            width = Dp.Hairline,
                            shape = RoundedCornerShape(cornerShape),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ).clip(RoundedCornerShape(cornerShape))
                        .clickable(onClick = onClick),
                )
            }
        }
    }
}