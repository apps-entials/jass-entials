package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump

class CardDistributionsHandler {
    companion object {

        /**
         * Responsible to update the card distributions known in GameStateHolder based on the newly played card.
         *
         * @param card The card that was played.
         */
        fun updateCardDistributions(
            card: Card,
            trick: Trick,
            unplayedCards: List<Card>,
            nextPlayer: () -> PlayerId
        ) {
            // update cards per suit per player:
            val cardPlayer = nextPlayer()
            if (GameStateHolder.cardsPerSuitPerPlayer != null
                && GameStateHolder.cardsPerSuitPerPlayer[cardPlayer] != null) {

                val playerMap = GameStateHolder.cardsPerSuitPerPlayer[cardPlayer]!!
                val cardsForSuit = playerMap[card.suit] ?: 0
                if (cardsForSuit > 0) {
                    GameStateHolder.cardsPerSuitPerPlayer += cardPlayer to (playerMap + (card.suit to (cardsForSuit - 1)))
                }
            }


            if (trick.trump == Trump.OBE_ABE
                && card.rank == Rank.ACE
                && GameStateHolder.acesPerPlayer.isNotEmpty()
            ) {
                val acesLeft = unplayedCards.filter { it.rank == Rank.ACE } - card
                if (acesLeft.isEmpty()) {
                    GameStateHolder.acesPerPlayer = mutableMapOf()
                    return
                }

                val acesPerPlayer = GameStateHolder.acesPerPlayer[nextPlayer()] ?: 0
                if (acesPerPlayer > 0) GameStateHolder.acesPerPlayer += nextPlayer() to (acesPerPlayer - 1)

                // adjust guaranteed cards if they are clear
                if (GameStateHolder.acesPerPlayer.containsValue(acesLeft.size) && acesLeft.isNotEmpty()) {
                    GameStateHolder.acesPerPlayer.toList().filter { it.second == acesLeft.size }.forEach {
                        GameStateHolder.guaranteedCards += it.first to
                                ((GameStateHolder.guaranteedCards[it.first] ?: setOf())
                                        + acesLeft)
                    }
                }
            } else if (trick.trump == Trump.UNGER_UFE
                && card.rank == Rank.SIX
                && GameStateHolder.sixesPerPlayer.isNotEmpty()
            ) {

                val sixesLeft = unplayedCards.filter { it.rank == Rank.SIX } - card
                if (sixesLeft.isEmpty()) {
                    GameStateHolder.sixesPerPlayer = mutableMapOf()
                    return
                }

                val sixesPerPlayer = GameStateHolder.sixesPerPlayer[nextPlayer()] ?: 0
                if (sixesPerPlayer > 0) GameStateHolder.sixesPerPlayer += nextPlayer() to (sixesPerPlayer - 1)

                // adjust guaranteed cards if they are clear
                if (GameStateHolder.sixesPerPlayer.containsValue(sixesLeft.size)
                    && sixesLeft.isNotEmpty()) {
                    GameStateHolder.sixesPerPlayer.toList().filter { it.second == sixesLeft.size }.forEach {
                        GameStateHolder.guaranteedCards += it.first to
                                ((GameStateHolder.guaranteedCards[it.first] ?: setOf())
                                        + sixesLeft)
                    }
                    // TODO: could also still update the cards per suit if the sixes / aces are not already included in the player's cards per suit
                }
            }

            // adjust guaranteed cards if they are clear from number of cards per color
//        println("cards per suit per player: ${GameStateHolder.cardsPerSuitPerPlayer}")

            if (GameStateHolder.cardsPerSuitPerPlayer == null) {
                GameStateHolder.cardsPerSuitPerPlayer = mutableMapOf()
            }

            if (GameStateHolder.cardsPerSuitPerPlayer.isNotEmpty()) {
                val suitCardsLeft = unplayedCards.filter { it.suit == card.suit } - card

                println("suit (${card.suit}) cards left: $suitCardsLeft")

                if (GameStateHolder.cardsPerSuitPerPlayer.values.any { (it[card.suit] ?: 0) > 0 }
                    && suitCardsLeft.isNotEmpty()) {

                    GameStateHolder.cardsPerSuitPerPlayer
                        .toList()
                        // TODO: maybe == instead of >= is needed, test this!!!
                        .filter { (_, suitOccurrences) -> (suitOccurrences[card.suit] ?: -1) >= suitCardsLeft.size }
//                            (suitOccurrences.firstOrNull { it.first == card.suit && it.second >= suitCardsLeft.size } != null) }
                        .forEach { (id, _) ->
                            GameStateHolder.guaranteedCards += id to
                                    (GameStateHolder.guaranteedCards[id] ?: setOf()) + suitCardsLeft.toSet()
                        }
                }
            }
        }
    }
}