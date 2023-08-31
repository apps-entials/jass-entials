package com.github.lucaengel.jass_entials.data.cards

import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class SuitTest {

    @Test
    fun toStringReturnsTheCorrectNameDependingOnTheCardType() {
        GameStateHolder.cardType = CardType.FRENCH

        assertThat(Suit.CLUBS.toString(), `is`("Clubs"))
        assertThat(Suit.SPADES.toString(), `is`("Spades"))
        assertThat(Suit.HEARTS.toString(), `is`("Hearts"))
        assertThat(Suit.DIAMONDS.toString(), `is`("Diamonds"))


        GameStateHolder.cardType = CardType.GERMAN

        assertThat(Suit.CLUBS.toString(), `is`("Eichel"))
        assertThat(Suit.SPADES.toString(), `is`("Schilte"))
        assertThat(Suit.HEARTS.toString(), `is`("Rosen"))
        assertThat(Suit.DIAMONDS.toString(), `is`("Schellen"))
    }

    @Test
    fun symbolReturnsTheCorrectSymbolDependingOnTheCardType() {
        GameStateHolder.cardType = CardType.FRENCH

        assertThat(Suit.CLUBS.symbol(), `is`("\u2663"))
        assertThat(Suit.SPADES.symbol(), `is`("\u2660"))
        assertThat(Suit.HEARTS.symbol(), `is`("\u2661"))
        assertThat(Suit.DIAMONDS.symbol(), `is`("\u2662"))


        GameStateHolder.cardType = CardType.GERMAN

        assertThat(Suit.CLUBS.symbol(), `is`("\uD83C\uDF30"))
        assertThat(Suit.SPADES.symbol(), `is`("🛡"))
        assertThat(Suit.HEARTS.symbol(), `is`("\uD83C\uDFF5"))
        assertThat(Suit.DIAMONDS.symbol(), `is`("\uD83D\uDD14"))
    }
}