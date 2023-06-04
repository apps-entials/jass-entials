package com.github.lucaengel.jass_entials.cards

data class Player(
    val playerIdx: Int,
    val firstName: String,
    val lastName: String,
    val cards: List<Card>,
    val teamNb: Int,
    val token: String,
) {
}