package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.R
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.io.Serializable

/**
 * The suits of a card.
 *
 * @property toStringFrench The french name of the suit.
 * @property toStringGerman The german name of the suit.
 * @property frenchSymbol The french symbol of the suit.
 * @property germanSymbol The german symbol of the suit.
 */
enum class Suit(private val toStringFrench: String, private val toStringGerman: String, private val frenchSymbol: Char, private val germanSymbol: String) : Serializable  {
    CLUBS("Clubs", "Eichel", '\u2663', "\uD83C\uDF30"),
    SPADES("Spades", "Schilte", '\u2660', "🛡"),
    HEARTS("Hearts", "Rosen", '\u2661', "\uD83C\uDFF5"),
    DIAMONDS("Diamonds", "Schellen", '\u2662', "\uD83D\uDD14");

    fun symbol(): String {
        return if (GameStateHolder.cardType == CardType.FRENCH) frenchSymbol.toString()
        else germanSymbol
    }

    override fun toString(): String {
        return if (GameStateHolder.cardType == CardType.FRENCH) toStringFrench
        else toStringGerman
    }
}

/**
 * The ranks of a card.
 *
 * @property rank The name of the rank.
 * @property normalHeight The height of the card in a normal game.
 * @property trumpHeight The height of the card in a trump game.
 */
enum class Rank(private val rank: String, private val normalHeight: Int, val trumpHeight: Int) : Serializable  {
    SIX("6", 6, 6),
    SEVEN("7", 7, 7),
    EIGHT("8", 8, 8),
    NINE("9", 9, 15),
    TEN("10", 10, 10),
    JACK("J", 11, 16),
    QUEEN("Q", 12, 12),
    KING("K", 13, 13),
    ACE("A", 14, 14);

    override fun toString(): String {
        return rank
    }
}

enum class CardType(val string: String) {
    FRENCH("French"), GERMAN("German");

    override fun toString(): String {
        return string
    }
}

/**
 * A card in a game of Jass.
 *
 * @property rank The rank of the card.
 * @property suit The suit of the card.
 */
data class Card(
    val suit: Suit,
    val rank: Rank,
) : Serializable {
    constructor() : this(Suit.HEARTS, Rank.TEN)

    /**
     * Returns the points of this card considering the given trump.
     *
     * @param trump The trump of the game.
     */
    fun points(trump: Trump): Int {
        if ((trump == Trump.OBE_ABE || trump == Trump.UNGER_UFE) && rank == Rank.EIGHT) return 8

        if (trump == Trump.UNGER_UFE) {
            if (rank == Rank.SIX) return 11
            if (rank == Rank.ACE) return 0
        }

        if (Trump.isSuitTrumpSuit(suit, trump)) {
            if (rank == Rank.JACK) return 20
            if (rank == Rank.NINE) return 14
        }

        return when (rank) {
            Rank.TEN -> 10
            Rank.JACK -> 2
            Rank.QUEEN -> 3
            Rank.KING -> 4
            Rank.ACE -> 11
            else -> 0
        }
    }

    /**
     * Returns true if this card is higher than the given card.
     * Assumes that this card was played first (e.g., clubs then heart --> clubs wins)
     */
    fun isHigherThan(that: Card, trump: Trump): Boolean {
        // this is trump
        if (Trump.isSuitTrumpSuit(this.suit, trump)) {
            if (Trump.isSuitTrumpSuit(that.suit, trump)) {
                return this.rank.trumpHeight > that.rank.trumpHeight
            }
            return true
        }

        // only that is trump
        if (Trump.isSuitTrumpSuit(that.suit, trump)) {
            return false
        }

        // lei haute
        if (this.suit != that.suit) return true

        //same suit: check rank
        // unger ufe
        if (trump == Trump.UNGER_UFE) return this.rank < that.rank

        // no trump
        return this.rank > that.rank
    }

    override fun toString(): String {
        return "${suit.symbol()}$rank"
    }

    companion object {

        private val frenchCardImageMap = mapOf(
            Card(Suit.CLUBS, Rank.SIX) to R.drawable.clubs_6,
            Card(Suit.CLUBS, Rank.SEVEN) to R.drawable.clubs_7,
            Card(Suit.CLUBS, Rank.EIGHT) to R.drawable.clubs_8,
            Card(Suit.CLUBS, Rank.NINE) to R.drawable.clubs_9,
            Card(Suit.CLUBS, Rank.TEN) to R.drawable.clubs_10,
            Card(Suit.CLUBS, Rank.JACK) to R.drawable.clubs_jack,
            Card(Suit.CLUBS, Rank.QUEEN) to R.drawable.clubs_queen,
            Card(Suit.CLUBS, Rank.KING) to R.drawable.clubs_king,
            Card(Suit.CLUBS, Rank.ACE) to R.drawable.clubs_ace,

            Card(Suit.DIAMONDS, Rank.SIX) to R.drawable.diamonds_6,
            Card(Suit.DIAMONDS, Rank.SEVEN) to R.drawable.diamonds_7,
            Card(Suit.DIAMONDS, Rank.EIGHT) to R.drawable.diamonds_8,
            Card(Suit.DIAMONDS, Rank.NINE) to R.drawable.diamonds_9,
            Card(Suit.DIAMONDS, Rank.TEN) to R.drawable.diamonds_10,
            Card(Suit.DIAMONDS, Rank.JACK) to R.drawable.diamonds_jack,
            Card(Suit.DIAMONDS, Rank.QUEEN) to R.drawable.diamonds_queen,
            Card(Suit.DIAMONDS, Rank.KING) to R.drawable.diamonds_king,
            Card(Suit.DIAMONDS, Rank.ACE) to R.drawable.diamonds_ace,

            Card(Suit.HEARTS, Rank.SIX) to R.drawable.heart_6,
            Card(Suit.HEARTS, Rank.SEVEN) to R.drawable.heart_7,
            Card(Suit.HEARTS, Rank.EIGHT) to R.drawable.heart_8,
            Card(Suit.HEARTS, Rank.NINE) to R.drawable.heart_9,
            Card(Suit.HEARTS, Rank.TEN) to R.drawable.heart_10,
            Card(Suit.HEARTS, Rank.JACK) to R.drawable.heart_jack,
            Card(Suit.HEARTS, Rank.QUEEN) to R.drawable.heart_queen,
            Card(Suit.HEARTS, Rank.KING) to R.drawable.heart_king,
            Card(Suit.HEARTS, Rank.ACE) to R.drawable.heart_ace,

            Card(Suit.SPADES, Rank.SIX) to R.drawable.spades_6,
            Card(Suit.SPADES, Rank.SEVEN) to R.drawable.spades_7,
            Card(Suit.SPADES, Rank.EIGHT) to R.drawable.spades_8,
            Card(Suit.SPADES, Rank.NINE) to R.drawable.spades_9,
            Card(Suit.SPADES, Rank.TEN) to R.drawable.spades_10,
            Card(Suit.SPADES, Rank.JACK) to R.drawable.spades_jack,
            Card(Suit.SPADES, Rank.QUEEN) to R.drawable.spades_queen,
            Card(Suit.SPADES, Rank.KING) to R.drawable.spades_king,
            Card(Suit.SPADES, Rank.ACE) to R.drawable.spades_ace,
        )

        private val germanCardImageMap = mapOf(
            Card(Suit.CLUBS, Rank.SIX) to R.drawable.eichel_6,
            Card(Suit.CLUBS, Rank.SEVEN) to R.drawable.eichel_7,
            Card(Suit.CLUBS, Rank.EIGHT) to R.drawable.eichel_8,
            Card(Suit.CLUBS, Rank.NINE) to R.drawable.eichel_9,
            Card(Suit.CLUBS, Rank.TEN) to R.drawable.eichel_10,
            Card(Suit.CLUBS, Rank.JACK) to R.drawable.eichel_under,
            Card(Suit.CLUBS, Rank.QUEEN) to R.drawable.eichel_ober,
            Card(Suit.CLUBS, Rank.KING) to R.drawable.eichel_king,
            Card(Suit.CLUBS, Rank.ACE) to R.drawable.eichel_ace,

            Card(Suit.DIAMONDS, Rank.SIX) to R.drawable.schellen_6,
            Card(Suit.DIAMONDS, Rank.SEVEN) to R.drawable.schellen_7,
            Card(Suit.DIAMONDS, Rank.EIGHT) to R.drawable.schellen_8,
            Card(Suit.DIAMONDS, Rank.NINE) to R.drawable.schellen_9,
            Card(Suit.DIAMONDS, Rank.TEN) to R.drawable.schellen_10,
            Card(Suit.DIAMONDS, Rank.JACK) to R.drawable.schellen_under,
            Card(Suit.DIAMONDS, Rank.QUEEN) to R.drawable.schellen_ober,
            Card(Suit.DIAMONDS, Rank.KING) to R.drawable.schellen_king,
            Card(Suit.DIAMONDS, Rank.ACE) to R.drawable.schellen_ace,

            Card(Suit.HEARTS, Rank.SIX) to R.drawable.rosen_6,
            Card(Suit.HEARTS, Rank.SEVEN) to R.drawable.rosen_7,
            Card(Suit.HEARTS, Rank.EIGHT) to R.drawable.rosen_8,
            Card(Suit.HEARTS, Rank.NINE) to R.drawable.rosen_9,
            Card(Suit.HEARTS, Rank.TEN) to R.drawable.rosen_10,
            Card(Suit.HEARTS, Rank.JACK) to R.drawable.rosen_under,
            Card(Suit.HEARTS, Rank.QUEEN) to R.drawable.rosen_ober,
            Card(Suit.HEARTS, Rank.KING) to R.drawable.rosen_king,
            Card(Suit.HEARTS, Rank.ACE) to R.drawable.rosen_ace,

            Card(Suit.SPADES, Rank.SIX) to R.drawable.schilten_6,
            Card(Suit.SPADES, Rank.SEVEN) to R.drawable.schilten_7,
            Card(Suit.SPADES, Rank.EIGHT) to R.drawable.schilten_8,
            Card(Suit.SPADES, Rank.NINE) to R.drawable.schilten_9,
            Card(Suit.SPADES, Rank.TEN) to R.drawable.schilten_10,
            Card(Suit.SPADES, Rank.JACK) to R.drawable.schilten_under,
            Card(Suit.SPADES, Rank.QUEEN) to R.drawable.schilten_ober,
            Card(Suit.SPADES, Rank.KING) to R.drawable.schilten_king,
            Card(Suit.SPADES, Rank.ACE) to R.drawable.schilten_ace,
        )

        /**
         * Returns the image id of the given card.
         */
        fun getCardImage(card: Card): Int {
            return if (GameStateHolder.cardType == CardType.FRENCH)
                 frenchCardImageMap[card]!!
            else
                germanCardImageMap[card]!!
        }

        /**
         * Returns the cards of the given [suit].
         *
         * @param suit the suit
         * @return the cards of the given [suit]
         */
        fun cardsOfSuit(suit: Suit, cards: List<Card>): List<Card> {
            return cards.filter { it.suit == suit }
        }
    }
}