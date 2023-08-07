package com.github.lucaengel.jass_entials.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.zIndex
import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.GameState
import kotlin.math.absoluteValue

/**
 * Reusable composables for the Jass game.
 */
class JassComposables {

    companion object {

        /**
         * Displays the current player's cards.
         *
         * @param playerEmail The current player's data.
         * @param onPlayCard The callback to be called when a card is played.
         */
        @Composable
        fun CurrentPlayerBox(playerEmail: String, player: PlayerData, onPlayCard: (Card) -> Unit = {}) {

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
                                    .clickable { onPlayCard(card) },
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
         * @param currentUserIdx The index of the current user in [players].
         * @param onClick The callback to be called when a card is clicked.
         **/
        @Composable
        fun CurrentTrick(gameState: GameState, players: List<PlayerData>, currentUserIdx: Int, onClick: () -> Unit) {

            val currentTrick = gameState.currentTrick
            val startingPlayerIdx = gameState.playerEmails.indexOfFirst { it == gameState.startingPlayerEmail }

            // 0 is bottom, 1 is right, 2 is top, 3 is left
            val idxToCards = (0..3)
                // Triple: (location of card, i.e. 0 is bottom, ..., index of player in [players], z-index)
                .map { idx -> Triple(idx, (currentUserIdx + idx) % 4, Math.floorMod(idx - startingPlayerIdx, 4)) }
                .associate { (i, playerIdx, zIndex) -> i to Pair(currentTrick.trickCards.firstOrNull { it.email == players[playerIdx].email }?.card, zIndex) }

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
                Column(modifier = Modifier.zIndex(idxToCards[2]?.second?.toFloat() ?: 0f)) {
                    CardBox(card = idxToCards[2]?.first, onClick = onClick)
                }

                // Card at the bottom middle
                Column(modifier = Modifier.zIndex(idxToCards[0]?.second?.toFloat() ?: 0f)) {
                    Spacer(modifier = Modifier.height((cardHeight - cardWidth).dp) /*(cardHeight * 2 / 3).dp*/)

                    CardBox(card = idxToCards[0]?.first, onClick = onClick)
                }

                val rotateModifier = Modifier
                    .graphicsLayer {
                        rotationZ = 90f
                    }

                // Card in the middle on the left
                Column(modifier = Modifier.zIndex(idxToCards[3]?.second?.toFloat() ?: 0f)) {
                    Spacer(modifier = Modifier.height(((cardHeight - cardWidth) / 2).dp/*(cardHeight / 3).dp*/))

                    Row {
                        CardBox(card = idxToCards[3]?.first, onClick = onClick, modifier = rotateModifier)

                        Spacer(modifier = Modifier.width((cardHeight - cardWidth).dp/*cardWidth * 1.25f).dp*/))
                    }
                }

                // Card in the middle on the right
                Column(modifier = Modifier.zIndex(idxToCards[1]?.second?.toFloat() ?: 0f)) {
                    Spacer(modifier = Modifier.height(((cardHeight - cardWidth) / 2).dp/*(cardHeight / 3).dp*/))

                    Row {
                        Spacer(modifier = Modifier.width((cardHeight - cardWidth/*cardWidth * 1.25f*/).dp))

                        CardBox(card = idxToCards[1]?.first, onClick = onClick, modifier = rotateModifier)
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
        fun CardBox(card: Card?, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val cardWidth = screenWidth.value / 10 //* 1.5f
            val cardHeight = cardWidth * 1.5f

            Box(modifier = modifier
                .requiredWidth(cardWidth.dp)
                .requiredHeight(cardHeight.dp)
            ) {
                if (card != null) {
                    Image(
                        painter = painterResource(id = Card.getCardImage(card)),
                        contentDescription = card.toString(),
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxSize()
                            .clickable(onClick = onClick),
                    )
                }
            }
        }
    }
}