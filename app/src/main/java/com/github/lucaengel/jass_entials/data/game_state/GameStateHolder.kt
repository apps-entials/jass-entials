package com.github.lucaengel.jass_entials.data.game_state

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.CardType
import com.github.lucaengel.jass_entials.data.cards.Deck
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.cards.Suit
import com.github.lucaengel.jass_entials.data.jass.JassType
import com.github.lucaengel.jass_entials.data.jass.Trump

/**
 * Holds the current game and betting state.
 */
class GameStateHolder {

    companion object {
        private val shuffledDeck = Deck.STANDARD_DECK.shuffled()
        private val playerData1 = PlayerData(
            PlayerId.PLAYER_1,
            "first_1",
            "second_1",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(4, 12)),
            0,
            "123"
        )
        private val playerData2 = PlayerData(
            PlayerId.PLAYER_2,
            "first_2",
            "second_2",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(12, 20)),
            0,
            "123"
        )
        private val playerData3 = PlayerData(
            PlayerId.PLAYER_3,
            "first_3",
            "second_3",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(20, 28)),
            0,
            "123"
        )
        private val playerData4 = PlayerData(
            PlayerId.PLAYER_4,
            "first_4",
            "second_4",
            Deck.sortPlayerCards(shuffledDeck.cards.subList(28, 36)),
            0,
            "123"
        )

        /**
         * Whether the cpu should be run asynchronousl or not (for testing purposes this will be set to false).
         */
        var runCpuAsynchronously = true

        /**
         * The current card type.
         */
        var cardType = CardType.FRENCH

        /**
         * The current player datas.
         */
        var players = listOf(playerData1, playerData2, playerData3, playerData4)

        /**
         * The current game state.
         */
        var gameState: GameState = GameState(
            currentUserId = playerData1.id,
            playerEmails = listOf(),
            currentPlayerId = playerData1.id,
            startingPlayerId = playerData1.id,
            currentRound = 1,
            jassType = JassType.SIDI_BARRANI,
            roundState = RoundState.initial(trump = Trump.CLUBS, startingPlayerId = playerData2.id)
                .withCardPlayed(Deck.STANDARD_DECK.cards[0])
                .withCardPlayed(Deck.STANDARD_DECK.cards[1])
                .withCardPlayed(Deck.STANDARD_DECK.cards[2])
                .withCardPlayed(Deck.STANDARD_DECK.cards[3]),
            winningBet = Bet(playerData2.id, Trump.CLUBS, BetHeight.FORTY),
            playerCards = Deck.STANDARD_DECK.dealCards(),
        )

        /**
         * The current point limits for the different jass types.
         * The default value is 1000.
         */
        var pointLimits = mapOf<JassType, Int>().withDefault { 1000 }

        /**
         * The current betting state.
         */
        var bettingState: BettingState =
            BettingState(
                currentUserId = playerData1.id,
                playerEmails = listOf(),
                currentBetterId = playerData1.id,
                startingBetterId = playerData4.id,
                jassType = JassType.SIDI_BARRANI,
                bets = listOf(Bet(playerData4.id, Trump.CLUBS, BetHeight.FORTY)),
                betActions = listOf(Bet.BetAction.BET),
                gameState = GameState(),
                score = Score.INITIAL,
            )

        /**
         * The trumps that have already been bet by each team. Used mainly for Coiffeur
         */
        var prevTrumpsByTeam = mapOf<TeamId, Set<Trump>>()

        /**
         * The cards that are guaranteed to be in the hands of the given players.
         */
        var guaranteedCards = mapOf<PlayerId, Set<Card>>()

        /**
         * The number of cards for a given suit that are guaranteed to be in the hands of a given player.
         */
        var cardsPerSuit = mapOf<PlayerId, Map<Suit, Int>>()

        /**
         * The number of aces that are guaranteed to be in the hands of a given player. (6's when unger ufe)
         */
        var acesPerPlayer = mapOf<PlayerId, Int>()

//        /**
//         * The current jass type.
//         */
//        var jassType: JassType = JassType.SIDI_BARRANI

        // TODO: call this when a new game starts
        /*fun startNewGameBettingState(playerDatas: List<PlayerData>, currentPlayerData: PlayerData) {
            if (!playerDatas.contains(currentPlayerData)) throw IllegalArgumentException("Current player must be in playerDatas")

            bettingState = BettingState(
                0,
                listOf(playerData1, playerData2, playerData3, playerData4),
                playerData1,
                JassType.SIDI_BARRANI,
                listOf(),
                GameState()
            )
        }*/

        /**
         * Updates the betting state to go to the next betting state round.
         *
         * @param startingBetter The player that starts the next betting round.
         */
        fun goToNextBettingStateRound(startingBetter: PlayerId) {
            prevTrumpsByTeam = mapOf()
            bettingState = bettingState.nextBettingRound(
                startingBetter = startingBetter,
//                jassType = jassType,
                score = gameState.roundState.score().nextRound(),
            )
        }
    }
}