package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import java.io.Serializable

/**
 * The suits of a card.
 *
 * @property toStringFrench The french name of the suit.
 * @property toStringGerman The german name of the suit.
 * @property frenchSymbol The french symbol of the suit.
 * @property germanSymbol The german symbol of the suit.
 */
enum class Suit(private val toStringFrench: String, private val toStringGerman: String, private val frenchSymbol: Char, private val germanSymbol: String) :
    Serializable {
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