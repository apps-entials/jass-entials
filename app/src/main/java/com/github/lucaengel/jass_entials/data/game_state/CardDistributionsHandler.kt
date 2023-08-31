package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.cards.Trick
import com.github.lucaengel.jass_entials.data.jass.Trump

class CardDistributionsHandler {
    companion object {
        private val NORMAL_BIDDING_THRESHOLD = BetHeight.HUNDRED
    }

    /**
     * The cards that are guaranteed to be in the hands of the given players.
     */
    private val guaranteedCards: MutableMap<PlayerId, Set<Card>> = mutableMapOf()

    /**
     * The number of cards for a given suit that are guaranteed to be in the hands of a given player.
     */
    private val cardsPerSuitPerPlayer: MutableMap<PlayerId, Map<Suit, Int>> = mutableMapOf()

    /**
     * Map from players to suits they do not have
     */
    private val suitsNotInHand: MutableMap<PlayerId, Set<Suit>> = mutableMapOf()

    /**
     * The number of aces that are guaranteed to be in the hands of a given player. (6's when unger ufe)
     */
    private val acesPerPlayer: MutableMap<PlayerId, Int> = mutableMapOf()

    /**
     * The number of aces that are guaranteed to be in the hands of a given player. (6's when unger ufe)
     */
    private val sixesPerPlayer: MutableMap<PlayerId, Int> = mutableMapOf()


    fun setSuitsNotInHand(suitsNotInHand: Map<PlayerId, Set<Suit>>): CardDistributionsHandler {
        this.suitsNotInHand.clear()
        this.suitsNotInHand.putAll(suitsNotInHand)

        return this
    }

    fun guaranteedCards(): Map<PlayerId, Set<Card>> {
        return guaranteedCards.toMap()
    }

    fun cardsPerSuitPerPlayer(): Map<PlayerId, Map<Suit, Int>> {
        return cardsPerSuitPerPlayer.toMap()
    }

    fun acesPerPlayer(): Map<PlayerId, Int> {
        return acesPerPlayer.toMap()
    }

    fun sixesPerPlayer(): Map<PlayerId, Int> {
        return sixesPerPlayer.toMap()
    }

    fun suitsNotInHand(): Map<PlayerId, Set<Suit>> {
        return suitsNotInHand.toMap()
    }

    /**
     * Responsible to update the card distributions known based on the newly played card.
     *
     * @param card The card that was played.
     */
    fun updateCardDistributions(
        card: Card,
        trick: Trick,
        unplayedCards: List<Card>,
        cardPlayer: PlayerId,
    ) {
        // update cards per suit for card player:
        updateCardsPerSuit(cardPlayer, card)

        // update aces per player to maybe find guaranteed cards
        updateAcesPerPlayer(unplayedCards, card, cardPlayer)

        // update sixes per player to maybe find guaranteed cards
        updateSixesPerPlayer(unplayedCards, card, cardPlayer)

        if (trick.isFull()) {
            updateSuitsNotInHand(trick, unplayedCards)
        }

        // TODO: based on suits not in hand adapt guaranteed cards!!!
        // adjust guaranteed cards if they are clear from number of cards per color
        updateGuaranteedCardsBasedOnCardsPerSuit(unplayedCards, card)
    }

    /**
     * Analyzes the last bets in Sidi Barrani and extracts knowledge from them.
     *
     * @param nbBetsInLastPass the number of bets in the last pass (i.e., the number of bets since current player placed their last bet)
     * @param allBets all bets that have been placed so far
     */
    fun extractKnowledgeFromBets(nbBetsInLastPass: Int, allBets: List<Bet>) {
        if (allBets.isEmpty()) return

        val lastBets = allBets.takeLast(nbBetsInLastPass + 1)

        val betsWithLastBet =
            if (lastBets.size < nbBetsInLastPass + 1 || lastBets.size < 4) {
                (listOf(
                    Bet(
                        PlayerId.PLAYER_1,
                        Trump.UNGER_UFE,
                        BetHeight.NONE
                    )
                ) + lastBets).zipWithNext()
            } else {
                lastBets.zipWithNext()
            }


        betsWithLastBet.forEach { (lastBet, currBet) ->
            val jump = currBet.bet.ordinal - lastBet.bet.ordinal
            // since none is at 0, 40 at 1, etc.:
            val isEven = currBet.bet.ordinal % 2 == 1

            if (currBet.bet <= NORMAL_BIDDING_THRESHOLD) {
                // here, we can add the known cards to the evaluation
                when (currBet.trump) {
                    Trump.OBE_ABE -> {
                        acesPerPlayer += currBet.playerId to jump
                        if (jump == 4) {
                            addAllSuitsOfRankToGuaranteed(currBet.playerId, Rank.ACE)
                        }
                    }
                    Trump.UNGER_UFE -> {
                        sixesPerPlayer += currBet.playerId to jump
                        if (jump == 4) {
                            addAllSuitsOfRankToGuaranteed(currBet.playerId, Rank.SIX)
                        }
                    }
                    // any suit trump
                    else -> {
                        // TODO: adapt algo for if the partner had already bid this suit before --> nel z zwöit, ass z dritt, etc.

                        val currGuaranteedCards =
                            guaranteedCards[currBet.playerId] ?: setOf()

                        val firstSuchBet = allBets
                            .first {
                                it.trump == currBet.trump
                                        && it.playerId.teamId() == currBet.playerId.teamId()
                            }

                        val nbCurrTeamTrumpBets = allBets
                            .count { it.trump == currBet.trump
                                    && it.playerId.teamId() == currBet.playerId.teamId()
                            }
                        // since, now, we do not know exactly what is being announced!
                        if (nbCurrTeamTrumpBets > 2) return

                        val guaranteedCard: Card
                        val nbCardsForSuit: Int
                        if (isEven && firstSuchBet == currBet) {
                            guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.JACK)
                            nbCardsForSuit = 3
                        } else if (isEven && firstSuchBet.bet.ordinal % 2 == 1) {
                            guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.ACE)
                            nbCardsForSuit = 3
                        } else if (isEven) {
                            // team partner announced the nell
                            guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.JACK)
                            nbCardsForSuit = 1
                        } else if (firstSuchBet == currBet) {
                            guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.NINE)
                            nbCardsForSuit = 3
                        } else if (firstSuchBet.bet.ordinal % 2 == 1) {
                            // team partner announced the jack
                            guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.NINE)
                            nbCardsForSuit = 2
                        } else {
                            guaranteedCard = Card(currBet.trump.asSuit()!!, Rank.ACE)
                            nbCardsForSuit = 3
                        }

                        // add trump jack, nine, or ace to guaranteed cards
                        guaranteedCards += (currBet.playerId to
                                currGuaranteedCards + guaranteedCard)
                        // TODO: do you say ace with 2 when the partner announced the nell?


                        // number of cards for the given suit
                        val currCardsPerSuit = cardsPerSuitPerPlayer[currBet.playerId] ?: mapOf()

                        // an additional card for every 20 added points (2 added in the ordinal)
                        val cardsForCurrTrump = nbCardsForSuit + (jump - 1) / 2

                        cardsPerSuitPerPlayer += currBet.playerId to
                                currCardsPerSuit + Pair(currBet.trump.asSuit()!!, cardsForCurrTrump)
                    }
                }


                println("\n\n\n")
                println(" guaranteed c': $guaranteedCards")
                println(" cardsuit p pl: $cardsPerSuitPerPlayer")
                println(" aces per play: $acesPerPlayer")
                println("\n\n\n")


                // TODO: come up with technique for the aces (maybe just have a number of aces that are guaranteed without the suit?)

                // TODO: come up with technique for adding value to trumps when obe abe was bid
            }
        }
    }

    /**
     * Adds all cards of a given rank to the guaranteed cards of a given player.
     *
     * @param playerId The player to add the cards to.
     * @param rank The rank of the cards to add.
     */
    private fun addAllSuitsOfRankToGuaranteed(
        playerId: PlayerId,
        rank: Rank
    ) {
        guaranteedCards += playerId to
                (guaranteedCards[playerId] ?: setOf()) +
                setOf(
                    Card(Suit.HEARTS, rank),
                    Card(Suit.DIAMONDS, rank),
                    Card(Suit.CLUBS, rank),
                    Card(Suit.SPADES, rank),
                )
    }


    /**
     * Returns an updated map containing the suits not in hand for each player.
     *
     * @return The updated map.
     */
    private fun updateSuitsNotInHand(
        trick: Trick,
        unplayedCards: List<Card>
    ) {
        val suit = trick.cards.first().suit
        for ((idx, card) in trick.cards.drop(1).withIndex()) {
            // if cannot follow suit and did not play trump on a non-trump suit, add suit to suits not in hand
            if (card.suit != suit
                && card.suit != Trump.asSuit(trick.trump)
            ) {
                val playerId = trick.startingPlayerId.playerAtPosition(idx + 1)

                // if jack of trump is still in the game, we check if it is guaranteed to be in someone else's hand
                // only if that is true, we add the suit to the suits not in hand (o/w the player could still have it)
                if (suit == Trump.asSuit(trick.trump) && unplayedCards.contains(Card(suit, Rank.JACK))) {
                    guaranteedCards.toList().firstOrNull { it.second.contains(Card(suit, Rank.JACK)) }?.let {
                        if (it.first != playerId) {
                            suitsNotInHand += playerId to suitsNotInHand.getOrDefault(
                                playerId,
                                setOf()
                            ).plus(suit)
                        }
                    }

                    continue
                }

                suitsNotInHand += playerId to suitsNotInHand.getOrDefault(playerId, setOf()).plus(suit)
            }
        }
    }

    /**
     * Adjusts the guaranteed cards if they are clear from number of cards per color.
     *
     * @param unplayedCards The cards that are still unplayed.
     * @param card The card that was played.
     */
    private fun updateGuaranteedCardsBasedOnCardsPerSuit(
        unplayedCards: List<Card>,
        card: Card
    ) {
        if (cardsPerSuitPerPlayer.isNotEmpty()) {
            val suitCardsLeft = unplayedCards.filter { it.suit == card.suit } - card

            println("suit (${card.suit}) cards left: $suitCardsLeft")

            if (cardsPerSuitPerPlayer.values.any { (it[card.suit] ?: 0) > 0 }
                && suitCardsLeft.isNotEmpty()) {

                cardsPerSuitPerPlayer
                    .toList()
                    // TODO: maybe == instead of >= is needed, test this!!!
                    .filter { (_, suitOccurrences) ->
                        (suitOccurrences[card.suit] ?: -1) >= suitCardsLeft.size
                    }
                    .forEach { (id, _) ->
                        guaranteedCards += id to
                                (guaranteedCards[id] ?: setOf()) + suitCardsLeft.toSet()
                    }
            }
        }
    }

    /**
     * Updates the number of sixes that are guaranteed to be in the hands of a given player and,
     * if possible, places sixes into the guaranteed cards map.
     *
     * @param unplayedCards The cards that are still unplayed.
     * @param card The card that was played.
     * @param cardPlayer The player that played the card.
     */
    private fun updateSixesPerPlayer(
        unplayedCards: List<Card>,
        card: Card,
        cardPlayer: PlayerId
    ) {
        if (card.rank != Rank.SIX || sixesPerPlayer.isEmpty())
            return

        val sixesLeft = unplayedCards.filter { it.rank == Rank.SIX } - card
        if (sixesLeft.isEmpty()) {
            sixesPerPlayer.clear()
            return
        }

        val nbSixesPerPlayer = sixesPerPlayer[cardPlayer] ?: 0
        if (nbSixesPerPlayer > 0) sixesPerPlayer += cardPlayer to (nbSixesPerPlayer - 1)

        // adjust guaranteed cards if they are clear
        if (sixesPerPlayer.containsValue(sixesLeft.size)
            && sixesLeft.isNotEmpty()
        ) {
            sixesPerPlayer.toList().filter { it.second == sixesLeft.size }.forEach {
                guaranteedCards += it.first to
                        ((guaranteedCards[it.first] ?: setOf())
                                + sixesLeft)
            }
            // TODO: could also still update the cards per suit if the sixes / aces are not already included in the player's cards per suit
        }
    }

    /**
     * Updates the number of aces that are guaranteed to be in the hands of a given player and,
     * if possible, places aces into the guaranteed cards map.
     *
     * @param unplayedCards The cards that are still unplayed.
     * @param card The card that was played.
     * @param cardPlayer The player that played the card.
     */
    private fun updateAcesPerPlayer(
        unplayedCards: List<Card>,
        card: Card,
        cardPlayer: PlayerId
    ) {
        if (card.rank != Rank.ACE || acesPerPlayer.isEmpty())
            return

        val acesLeft = unplayedCards.filter { it.rank == Rank.ACE } - card
        if (acesLeft.isEmpty()) {
            acesPerPlayer.clear()
            return
        }

        val nbAcesPerPlayer = acesPerPlayer[cardPlayer] ?: 0
        if (nbAcesPerPlayer > 0) acesPerPlayer += cardPlayer to (nbAcesPerPlayer - 1)

        // adjust guaranteed cards if they are clear
        if (acesPerPlayer.containsValue(acesLeft.size) && acesLeft.isNotEmpty()) {
            acesPerPlayer.toList().filter { it.second == acesLeft.size }.forEach {
                guaranteedCards += it.first to
                        ((guaranteedCards[it.first] ?: setOf())
                                + acesLeft)
            }
        }
    }

    /**
     * Updates the number of cards for a given suit that are guaranteed to be in the hands of a given player.
     *
     * @param cardPlayer The player that played the card.
     * @param card The card that was played.
     */
    private fun updateCardsPerSuit(
        cardPlayer: PlayerId,
        card: Card
    ) {
        if (cardsPerSuitPerPlayer[cardPlayer] != null) {

            val playerMap = cardsPerSuitPerPlayer[cardPlayer]!!
            val cardsForSuit = playerMap[card.suit] ?: 0
            if (cardsForSuit > 0) {
                cardsPerSuitPerPlayer += cardPlayer to (playerMap + (card.suit to (cardsForSuit - 1)))
            }
        }
    }

    override fun toString(): String {
        return "guaranteedCards: $guaranteedCards\n" +
                "cardsPerSuitPerPlayer: $cardsPerSuitPerPlayer\n" +
                "acesPerPlayer: $acesPerPlayer\n" +
                "sixesPerPlayer: $sixesPerPlayer\n" +
                "suitsNotInHand: $suitsNotInHand\n"

    }
}