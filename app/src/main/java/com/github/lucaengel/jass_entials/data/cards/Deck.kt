package com.github.lucaengel.jass_entials.data.cards

/**
 * A deck of cards.
 *
 * @property cards the cards of the deck
 */
data class Deck(val cards: List<Card> = listOf()) {

    constructor() : this(listOf())

    /**
     * Shuffles the cards of the deck.
     *
     * @return the shuffled deck
     */
    fun shuffled(): Deck {
        return this.copy(cards = cards.shuffled())
    }

    /**
     * Deals the cards of the deck to the given players.
     *
     * @param playerData the players to deal the cards to
     * @return a map of the players and their cards
     */
    fun dealCards(playerData: List<PlayerData>): Map<PlayerData, List<Card>> {
        return playerData.zip(cards.chunked(9)).map { (playerData, cards) ->
            val sortedCards = sortPlayerCards(cards)
            playerData.copy(cards = sortedCards) to sortedCards
        }.toMap()
    }

    companion object {

        /**
         * A standard deck of cards.
         */
        val STANDARD_DECK = Deck(
            Suit.values().flatMap { suit ->
                Rank.values().map { rank ->
                    Card(rank, suit)
                }
            }
        )

        /**
         * Sorts the cards of a player in a way that the cards of the same suit are next to each other \
         * and the red and black suits are alternating.
         *
         * @param cards the cards of a player
         * @return the sorted cards
         */
        fun sortPlayerCards(cards: List<Card>): List<Card> {

            // create list of lists of cards, where each list contains cards of the same suit
            val sortedCardsBySuit = cards.sortedWith(compareBy({ it.suit }, { -it.rank.ordinal })).groupBy { it.suit }.values.toList()

            val (redSuits, blackSuits) = sortedCardsBySuit.partition { it[0].suit in setOf(Suit.HEARTS, Suit.DIAMONDS) }

            // extend shorter list with empty list to make sure both lists have the same size
            // (2 empty lists added for the case where the player has only 2 red suits or 2 black suits)
            val zipped =
                if (redSuits.size > blackSuits.size) redSuits.zip(blackSuits + listOf(listOf(), listOf()))
                else blackSuits.zip(redSuits + listOf(listOf(), listOf()))

            return zipped.map { it.first + it.second }.flatten()
        }
    }
}