package com.github.lucaengel.jass_entials.data.cards

data class Player(
    val playerIdx: Int,
    val firstName: String,
    val lastName: String,
    val cards: List<Card>,
    val teamNb: Int,
    val token: String,
) {
    constructor() : this(
        playerIdx = 0,
        firstName = "",
        lastName = "",
        cards = listOf(),
        teamNb = 0,
        token = "",
    )
}