package com.github.lucaengel.jass_entials.data.jass

/**
 * Enum representing the different types of Jass games.
 */
enum class JassType(val jassName: String) {
    SCHIEBER("Schieber"),
    COIFFEUR("Coiffeur"),
    SIDI_BARRANI("Sidi Barrani");

    override fun toString(): String {
        return jassName
    }
}