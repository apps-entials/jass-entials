package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.Rank
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.jass.Trump
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import java.util.concurrent.TimeUnit


class MonteCarloCpuTest {

    private val SEED: Int = 0
    private val ITERATIONS = 10000

    /**
     * Timeout for all tests in this class.
     */
    @JvmField
    @Rule
    val globalTimeout = Timeout(15, TimeUnit.SECONDS)

    private data class CardSet(val cards: Set<Card>) {
        companion object {
            val EMPTY = CardSet(emptySet())
            val ALL_CARDS = CardSet(Deck.STANDARD_DECK.cards.toSet())
        }

        fun add(card: Card): CardSet {
            return CardSet(cards + card)
        }

        fun remove(card: Card): CardSet {
            return CardSet(cards - card)
        }

        fun cards(): List<Card> {
            return cards.toList()
        }
    }

    @Test
    fun constructorFailsWithTooFewIterations() {
        for (i in -10..8) {
            assertThrows(IllegalArgumentException::class.java) {
                CpuPlayer(
                    PlayerId.PLAYER_1,
                    0,
                    i
                )
            }
        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly1() {
        // Opponent team will win this trick, we have to minimize loss
        val p = CpuPlayer(playerId = PlayerId.PLAYER_2, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.SPADES, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.SPADES, Rank.JACK))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.EIGHT))
            .add(Card(Suit.SPADES, Rank.NINE))
            .add(Card(Suit.SPADES, Rank.TEN))
            .add(Card(Suit.HEARTS, Rank.SIX))
            .add(Card(Suit.HEARTS, Rank.SEVEN))
            .add(Card(Suit.HEARTS, Rank.EIGHT))
            .add(Card(Suit.HEARTS, Rank.NINE))
            .add(Card(Suit.HEARTS, Rank.TEN))
            .add(Card(Suit.HEARTS, Rank.JACK))

//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.SPADES,
                Rank.EIGHT
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly2() {
        // Our team will win this trick, play the 10 to maximize points
        val p = CpuPlayer(playerId = PlayerId.PLAYER_4, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.SPADES, Rank.JACK))
            .withCardPlayed(Card(Suit.SPADES, Rank.QUEEN))
            .withCardPlayed(Card(Suit.SPADES, Rank.SIX))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.EIGHT))
            .add(Card(Suit.SPADES, Rank.NINE))
            .add(Card(Suit.SPADES, Rank.TEN))
            .add(Card(Suit.CLUBS, Rank.SIX))
            .add(Card(Suit.HEARTS, Rank.SEVEN))
            .add(Card(Suit.HEARTS, Rank.EIGHT))
            .add(Card(Suit.HEARTS, Rank.NINE))
            .add(Card(Suit.HEARTS, Rank.TEN))
            .add(Card(Suit.HEARTS, Rank.JACK))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.SPADES,
                Rank.TEN
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly3() {
        // Lots of points in this trick, over-cut to get them
        val p = CpuPlayer(playerId = PlayerId.PLAYER_4, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.SPADES, Rank.TEN))
            .withCardPlayed(Card(Suit.HEARTS, Rank.TEN))
            .withCardPlayed(Card(Suit.CLUBS, Rank.NINE))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.EIGHT))
            .add(Card(Suit.SPADES, Rank.NINE))
            .add(Card(Suit.SPADES, Rank.JACK))
            .add(Card(Suit.CLUBS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.SEVEN))
            .add(Card(Suit.HEARTS, Rank.EIGHT))
            .add(Card(Suit.HEARTS, Rank.NINE))
            .add(Card(Suit.HEARTS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.QUEEN))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.CLUBS,
                Rank.JACK
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly4() {
        // Lots of points in this trick, cut to get them, but don't waste the Jack
        val p = CpuPlayer(playerId = PlayerId.PLAYER_4, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.SPADES, Rank.TEN))
            .withCardPlayed(Card(Suit.HEARTS, Rank.TEN))
            .withCardPlayed(Card(Suit.SPADES, Rank.ACE))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.EIGHT))
            .add(Card(Suit.SPADES, Rank.NINE))
            .add(Card(Suit.SPADES, Rank.JACK))
            .add(Card(Suit.CLUBS, Rank.SEVEN))
            .add(Card(Suit.CLUBS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.EIGHT))
            .add(Card(Suit.HEARTS, Rank.NINE))
            .add(Card(Suit.HEARTS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.QUEEN))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.CLUBS,
                Rank.SEVEN
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly5() {
        // Trick winner unclear, follow but don't risk the 10
        val p = CpuPlayer(playerId = PlayerId.PLAYER_3, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.DIAMONDS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.SPADES, Rank.EIGHT))
            .withCardPlayed(Card(Suit.SPADES, Rank.SEVEN))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.NINE))
            .add(Card(Suit.SPADES, Rank.TEN))
            .add(Card(Suit.CLUBS, Rank.SEVEN))
            .add(Card(Suit.CLUBS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.SIX))
            .add(Card(Suit.HEARTS, Rank.EIGHT))
            .add(Card(Suit.HEARTS, Rank.NINE))
            .add(Card(Suit.HEARTS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.QUEEN))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.SPADES,
                Rank.NINE
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly6() {
        // Very strong hand in trump, enter with the Jack
        val p = CpuPlayer(playerId = PlayerId.PLAYER_1, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.SPADES, PlayerId.PLAYER_1)
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.SEVEN))
            .add(Card(Suit.SPADES, Rank.EIGHT))
            .add(Card(Suit.SPADES, Rank.TEN))
            .add(Card(Suit.SPADES, Rank.JACK))
            .add(Card(Suit.SPADES, Rank.KING))
            .add(Card(Suit.SPADES, Rank.ACE))
            .add(Card(Suit.HEARTS, Rank.NINE))
            .add(Card(Suit.HEARTS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.QUEEN))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.SPADES,
                Rank.JACK
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly7() {
        // We can only play one card, play it
        val p = CpuPlayer(playerId = PlayerId.PLAYER_2, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.JACK))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.TEN))
            .add(Card(Suit.SPADES, Rank.JACK))
            .add(Card(Suit.SPADES, Rank.QUEEN))
            .add(Card(Suit.SPADES, Rank.KING))
            .add(Card(Suit.SPADES, Rank.ACE))
            .add(Card(Suit.CLUBS, Rank.NINE))
            .add(Card(Suit.HEARTS, Rank.TEN))
            .add(Card(Suit.HEARTS, Rank.JACK))
            .add(Card(Suit.HEARTS, Rank.QUEEN))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.CLUBS,
                Rank.NINE
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly8() {
        // We don't have to follow, save the Jack of trump for later (0 points in trick)
        val p = CpuPlayer(playerId = PlayerId.PLAYER_4, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.SIX))
            .withCardPlayed(Card(Suit.CLUBS, Rank.SEVEN))
            .withCardPlayed(Card(Suit.CLUBS, Rank.EIGHT))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.CLUBS, Rank.JACK))
            .add(Card(Suit.DIAMONDS, Rank.TEN))
            .add(Card(Suit.DIAMONDS, Rank.ACE))
            .add(Card(Suit.SPADES, Rank.SIX))
            .add(Card(Suit.SPADES, Rank.TEN))
            .add(Card(Suit.SPADES, Rank.ACE))
            .add(Card(Suit.HEARTS, Rank.TEN))
            .add(Card(Suit.HEARTS, Rank.ACE))
            .add(Card(Suit.HEARTS, Rank.KING))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.SPADES,
                Rank.SIX
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly9() {
        // Two tricks left, no trump left, we have an ace, we must enter with it
        val toPlay: CardSet = CardSet.ALL_CARDS
            .remove(Card(Suit.HEARTS, Rank.SIX))
            .remove(Card(Suit.HEARTS, Rank.SEVEN))
            .remove(Card(Suit.HEARTS, Rank.ACE))
            .remove(Card(Suit.CLUBS, Rank.SIX))
            .remove(Card(Suit.CLUBS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.ACE))
            .remove(Card(Suit.DIAMONDS, Rank.SIX))
            .remove(Card(Suit.DIAMONDS, Rank.ACE))
        val state: RoundState = stateAfterPlayingAllCardsIn(toPlay, Trump.SPADES, PlayerId.PLAYER_1)
        assert(state.trick().cards.isEmpty())
        val p = CpuPlayer(state.nextPlayer(), seed = SEED, nbSimulations = ITERATIONS)
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.CLUBS, Rank.SIX))
            .add(Card(Suit.CLUBS, Rank.ACE))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(Suit.CLUBS, Rank.ACE),
            c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly10() {
        // Two tricks left, two trumps left, we have the higher one, we must enter with it
        val toPlay: CardSet = CardSet.ALL_CARDS
            .remove(Card(Suit.HEARTS, Rank.SIX))
            .remove(Card(Suit.HEARTS, Rank.SEVEN))
            .remove(Card(Suit.HEARTS, Rank.ACE))
            .remove(Card(Suit.CLUBS, Rank.SIX))
            .remove(Card(Suit.CLUBS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.ACE))
            .remove(Card(Suit.DIAMONDS, Rank.SIX))
            .remove(Card(Suit.DIAMONDS, Rank.ACE))
        val state: RoundState = stateAfterPlayingAllCardsIn(toPlay, Trump.DIAMONDS, PlayerId.PLAYER_1)
        assert(state.trick().cards.isEmpty())
        val p = CpuPlayer(state.nextPlayer(), seed = SEED, nbSimulations = ITERATIONS)
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.DIAMONDS, Rank.ACE))
            .add(Card(Suit.CLUBS, Rank.ACE))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.DIAMONDS,
                Rank.ACE
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly11() {
        // Two tricks left, we are loosing the trick, we must cut to win the last tricks
        val toPlay: CardSet = CardSet.ALL_CARDS
            .remove(Card(Suit.HEARTS, Rank.SIX))
            .remove(Card(Suit.HEARTS, Rank.SEVEN))
            .remove(Card(Suit.HEARTS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.SIX))
            .remove(Card(Suit.CLUBS, Rank.SEVEN))
            .remove(Card(Suit.CLUBS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.ACE))
            .remove(Card(Suit.DIAMONDS, Rank.ACE))
        val state: RoundState = stateAfterPlayingAllCardsIn(toPlay, Trump.DIAMONDS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.SEVEN))
            .withCardPlayed(Card(Suit.CLUBS, Rank.SIX))
            .withCardPlayed(Card(Suit.HEARTS, Rank.SEVEN))
        assert(state.trick().cards.size == 3)
        val p = CpuPlayer(state.nextPlayer(), seed = SEED, nbSimulations = ITERATIONS)
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.DIAMONDS, Rank.ACE))
            .add(Card(Suit.HEARTS, Rank.KING))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.DIAMONDS,
                Rank.ACE
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly12() {
        // Same as above, but we're second to play
        val toPlay: CardSet = CardSet.ALL_CARDS
            .remove(Card(Suit.HEARTS, Rank.SIX))
            .remove(Card(Suit.HEARTS, Rank.SEVEN))
            .remove(Card(Suit.HEARTS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.SIX))
            .remove(Card(Suit.CLUBS, Rank.SEVEN))
            .remove(Card(Suit.CLUBS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.ACE))
            .remove(Card(Suit.DIAMONDS, Rank.ACE))
        val state: RoundState = stateAfterPlayingAllCardsIn(toPlay, Trump.DIAMONDS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.SEVEN))
        assert(state.trick().cards.size == 1)
        val p = CpuPlayer(state.nextPlayer(), seed = SEED, nbSimulations = ITERATIONS)
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.DIAMONDS, Rank.ACE))
            .add(Card(Suit.HEARTS, Rank.KING))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.DIAMONDS,
                Rank.ACE
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly13() {
        // We have the last trump and a 10, our partner has the lead, we must play the 10
        val toPlay: CardSet = CardSet.ALL_CARDS
            .remove(Card(Suit.HEARTS, Rank.SIX))
            .remove(Card(Suit.HEARTS, Rank.SEVEN))
            .remove(Card(Suit.HEARTS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.TEN))
            .remove(Card(Suit.CLUBS, Rank.JACK))
            .remove(Card(Suit.CLUBS, Rank.KING))
            .remove(Card(Suit.CLUBS, Rank.ACE))
            .remove(Card(Suit.DIAMONDS, Rank.SIX))
        val state: RoundState = stateAfterPlayingAllCardsIn(toPlay, Trump.DIAMONDS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.KING))
            .withCardPlayed(Card(Suit.CLUBS, Rank.ACE))
            .withCardPlayed(Card(Suit.HEARTS, Rank.SIX))
        assert(state.trick().cards.size == 3)
        val p = CpuPlayer(state.nextPlayer(), seed = SEED, nbSimulations = ITERATIONS)
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.CLUBS, Rank.TEN))
            .add(Card(Suit.DIAMONDS, Rank.SIX))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(Suit.CLUBS, Rank.TEN),
            c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly14() {
        // Two zero-points tricks remain, we must accept loosing the first to get the 5 final points
        val toPlay: CardSet = CardSet.ALL_CARDS
            .remove(Card(Suit.SPADES, Rank.SIX))
            .remove(Card(Suit.HEARTS, Rank.SIX))
            .remove(Card(Suit.HEARTS, Rank.SEVEN))
            .remove(Card(Suit.HEARTS, Rank.EIGHT))
            .remove(Card(Suit.HEARTS, Rank.NINE))
            .remove(Card(Suit.CLUBS, Rank.SIX))
            .remove(Card(Suit.CLUBS, Rank.SEVEN))
            .remove(Card(Suit.CLUBS, Rank.EIGHT))
        val state: RoundState = stateAfterPlayingAllCardsIn(toPlay, Trump.SPADES, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.CLUBS, Rank.SIX))
            .withCardPlayed(Card(Suit.CLUBS, Rank.SEVEN))
            .withCardPlayed(Card(Suit.CLUBS, Rank.EIGHT))
        assert(state.trick().cards.size == 3)
        val p = CpuPlayer(state.nextPlayer(), seed = SEED, nbSimulations = ITERATIONS)
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.SPADES, Rank.SIX))
            .add(Card(Suit.HEARTS, Rank.SIX))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.HEARTS,
                Rank.SIX
            ), c
        )
//        }
    }

    @Test
    fun CpuPlayerPlaysCorrectly15() {
        // Cannot undercut, must accept loss (only one playable card)
        val p = CpuPlayer(playerId = PlayerId.PLAYER_4, seed = SEED, nbSimulations = ITERATIONS)
        val state: RoundState = RoundState.initial(Trump.CLUBS, PlayerId.PLAYER_1)
            .withCardPlayed(Card(Suit.HEARTS, Rank.KING))
            .withCardPlayed(Card(Suit.HEARTS, Rank.ACE))
            .withCardPlayed(Card(Suit.CLUBS, Rank.NINE))
        val hand: CardSet = CardSet.EMPTY
            .add(Card(Suit.CLUBS, Rank.SIX))
            .add(Card(Suit.CLUBS, Rank.SEVEN))
            .add(Card(Suit.CLUBS, Rank.EIGHT))
            .add(Card(Suit.CLUBS, Rank.TEN))
            .add(Card(Suit.CLUBS, Rank.QUEEN))
            .add(Card(Suit.CLUBS, Rank.KING))
            .add(Card(Suit.CLUBS, Rank.ACE))
            .add(Card(Suit.SPADES, Rank.SIX))
            .add(Card(Suit.HEARTS, Rank.TEN))
//        assertTimeoutPreemptively(TIMEOUT) {
        val c: Card = p.cardToPlay(state, hand.cards()).join()
        assertEquals(
            Card(
                Suit.HEARTS,
                Rank.TEN
            ), c
        )
//        }
    }


    private fun stateAfterPlayingAllCardsIn(
        cards: CardSet,
        trump: Trump,
        firstPlayer: PlayerId
    ): RoundState {
        var s: RoundState = RoundState.initial(trump, firstPlayer)
        for (card in cards.cards()) {
            s = s.withCardPlayed(card)

            if (s.trick().isFull()) {
                s = s.withTrickCollected()
            }
        }
        return s
    }
}