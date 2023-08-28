package com.github.lucaengel.jass_entials.data.cards


/**
 * The types of cards.
 *
 * @property string The name of the card type.
 */
enum class CardType(val string: String) {
    FRENCH("French"), GERMAN("German");

    override fun toString(): String {
        return string
    }
}