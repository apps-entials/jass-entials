package com.github.lucaengel.jass_entials.data.cards

import java.io.Serializable

/**
 * The ranks of a card.
 *
 * @property rank The name of the rank.
 * @property normalHeight The height of the card in a normal game.
 * @property trumpHeight The height of the card in a trump game.
 */
enum class Rank(private val rank: String, private val normalHeight: Int, val trumpHeight: Int) :
    Serializable {
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