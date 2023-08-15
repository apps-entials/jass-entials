package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.Bet
import com.github.lucaengel.jass_entials.data.game_state.BettingState
import com.github.lucaengel.jass_entials.data.game_state.GameState
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.game_state.Score
import com.github.lucaengel.jass_entials.data.jass.Trump
import java.util.SplittableRandom
import java.util.concurrent.CompletableFuture
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Class representing a CPU player
 *
 * @property playerId the player data
 */
class CpuPlayer(val playerId: PlayerId, val seed: Int = Random.nextInt(), private val nbSimulations: Int = 9, private val threadSleepTime: Long = 300) : Player {
    private val EXPLORATION_CONSTANT = 40
    private val rng = SplittableRandom(seed.toLong())

    init {
        if (nbSimulations < 9) {
            throw IllegalArgumentException("Number of simulations must be at least 9")
        }
    }


    override fun bet(bettingState: BettingState): CompletableFuture<BettingState> {

        val bettingFuture = CompletableFuture<BettingState>()
        CompletableFuture.runAsync {
            Thread.sleep(3*threadSleepTime)

            if ((bettingState.availableActions().contains(Bet.BetAction.PASS))
                && (Random.nextFloat() > 0.2 || bettingState.availableBets().isEmpty())) {

                bettingFuture.complete(bettingState.nextPlayer())
            // if player can start the game, do it with a 80% chance
            // if player has to start the game, do it (available actions only contains start game)
            } else if (bettingState.availableActions().contains(Bet.BetAction.START_GAME)
                && (Random.nextFloat() > 0.2 || bettingState.availableActions().size == 1)) {

                bettingFuture.complete(bettingState.nextPlayer())
            } else {
                bettingFuture.complete(
                    bettingState.nextPlayer(
                        Bet(
                            playerId = playerId,
                            bet = bettingState.availableBets().first(),
                            trump = bettingState.availableTrumps().random(),
                        )
                    )
                )
            }
        }
        return bettingFuture
    }

    override fun chooseTrump(gameState: GameState): CompletableFuture<Trump> {
        TODO("Not yet implemented")
    }

    override fun cardToPlay(roundState: RoundState, handCards: List<Card>): CompletableFuture<Card> {
//        val card = player.playableCards(gameState.roundState.trick(), gameState.roundState.trick().trump).random()
//
//        // TODO: maybe update player data here and return it as well
//
//        val cardFuture = CompletableFuture<Card>()
//        CompletableFuture.runAsync {
//            Thread.sleep(threadSleepTime)
//            cardFuture.complete(card)
//        }
//
//        return cardFuture

        val cardFuture = CompletableFuture<Card>()
//        CompletableFuture.runAsync {
            val card = monteCarloCardToPlay(
                roundState = roundState,
                handCards = handCards,
            )
            cardFuture.complete(card)
//        }
        return cardFuture
    }












    var i = 0


    /**
     * Class representing a CPU player using the Monte Carlo Tree Search algorithm
     *
     * @param roundState The current round state.
     * @param handCards The hand cards of the player for which the card is to be determined.
     * @return The card to be played.
     */
    private fun monteCarloCardToPlay(roundState: RoundState, handCards: List<Card>): Card {
        val playableCards = determinePlayableCards(roundState, roundState.nextPlayer(), handCards)

        println("total child nodes: " + playableCards.size)
        val root = Node(
            roundState = roundState,
            childNodes = ArrayList(List(playableCards.size) { null }),
            nonExistentChildNodes = playableCards,
            totalPoints = 0.0,
            nbTurnsSimulated = 0,
        )

        while (root.nbTurnsSimulated < nbSimulations) {
            val path = addNodeAndGetPath(root, handCards)
//            println("path: $path")
            val score = simulateRandomRound(path.last().roundState, handCards)

            root.nbTurnsSimulated++
            for (node in path) {
                node.nbTurnsSimulated++

                // update the total points of the node with the points that the team scored
                val nodeTrick = node.roundState.trick()

                node.totalPoints += score.roundPoints(nodeTrick.lastPlayer().team())
//                node.totalPoints = node.totalPoints / node.nbTurnsSimulated * (node.nbTurnsSimulated - 1) +
//                        score.gamePoints(nodeTrick.lastPlayer().team()) / node.nbTurnsSimulated
            }
        }

        for ((idx, child) in root.childNodes.withIndex()) {

            if (child != null) {
                val trick = child.roundState.trick()
                println("selected (idx, card): " + idx + ", " + trick.cards[trick.size() - 1])

            }
        }


        val trick = root.childNodes[root.getBestChildIndex(0)]!!.roundState.trick()

        println("Actual sel (idx, card): " + root.getBestChildIndex(0) + " "+ trick.cards[trick.size() - 1])
//        println("round state: " + root.childNodes[root.getBestChildIndex(0)]!!.roundState)

        // need last player who played a card
        return trick.cards[trick.size() - 1]
    }

    /**
     * Adds a new node to the appropriate location in the [root]'s tree if possible, and returns the path from the [root]
     * to the new node. The nodes along this path need their scores updated after evaluating the new node.
     *
     * @param root The new node to be added.
     * @param handCards The hand cards of the player for which the round is simulated.
     * @return The path from the root to the new node.
     */
    private fun addNodeAndGetPath(root: Node, handCards: List<Card>): List<Node> {

        var currNode = root
//        val path = mutableListOf(root)
        val path: MutableList<Node> = mutableListOf()



        // scan for node with non-existent child nodes and who does not have a child yet
        while (currNode.nonExistentChildNodes.isEmpty()
            && currNode.childNodes.isNotEmpty()) {


            val bestChildIdx = currNode.getBestChildIndex(EXPLORATION_CONSTANT)
            currNode = currNode.childNodes[bestChildIdx]!!
            path.add(currNode)
        }

        // if the node has no child nodes we cannot add a new node
        // as all cards have been simulated
        if (currNode.childNodes.isEmpty()) return path

        if (i++ < 5) println("after the if")
        val cardToPlay = currNode.nonExistentChildNodes[0]
        var currState =
            (if (currNode.roundState.trick().isFull()) {
                currNode.roundState.withTrickCollected()
            } else {
                currNode.roundState
            })

        // add card of the current player
        currState = currState.withCardPlayed(cardToPlay)

        if (i++ < 5) println("curr trick: ${currState.trick()}")

//        if (currState.trick().isFull()) {
//            currState = currState.withTrickCollected()
//        }
        val nextPlayer =
            if (currState.trick().isFull()) {
                currState.trick().winner()
            } else {
                currState.trick().nextPlayer()
            }

        // playable cards of the next player
        val newPlayableCards = determinePlayableCards(currState, currState.nextPlayer(), handCards)

        if (i++ < 5) println("new playable cards: $newPlayableCards")


        val newLeaf = Node(
            roundState = currState,
            childNodes = ArrayList(List(newPlayableCards.size) { null }),
            nonExistentChildNodes = newPlayableCards.toMutableList(),
            totalPoints = 0.0,
            nbTurnsSimulated = 0,
        )
        // todo: change it to replace!!!
        val index = currNode.childNodes.size - currNode.nonExistentChildNodes.size

        currNode.nonExistentChildNodes -= cardToPlay

        if (i++ < 6) println("index to insert: $index")

        currNode.childNodes.add(index, newLeaf)

        path.add(newLeaf)

        return path
    }

    /**
     * Returns the final score of a randomly played round starting from a given state.
     *
     * @param initialState The initial state from which the round will be simulated.
     * @param handCards The hand cards of the player for which the round is simulated.
     * @return The final score of the round.
     */
    private fun simulateRandomRound(initialState: RoundState, handCards: List<Card>): Score {

        var currState =
            if (initialState.trick().isFull()) {
                initialState.withTrickCollected()
            } else {
                initialState
            }

        while (!currState.isRoundOver()) {
            val playableCards = determinePlayableCards(currState, currState.nextPlayer(), handCards)
//            println("hand cards: $handCards")
//            println("playable cards: $playableCards")
            if (playableCards.isEmpty()) break // todo: check why isRoundOver() does not work

            val cardToPlay = playableCards[rng.nextInt(playableCards.size)]
            currState = currState.withCardPlayed(cardToPlay)

            if (currState.trick().isFull()) {
                currState = currState.withTrickCollected()
            }
        }

        return currState.score()
    }

    /**
     * Determines the playable cards for a given state, considering the cards not yet played and the player's hand.
     *
     * @param currentState The current state for which playable cards need to be determined.
     * @param playerId The player for which playable cards need to be determined.
     * @param handCards The hand cards the current CpuPlayer has.
     * @return List of playable cards.
     */
    private fun determinePlayableCards(currentState: RoundState, playerId: PlayerId, handCards: List<Card>): List<Card> {

        val state =
            if (currentState.trick().isFull()) {
                currentState.withTrickCollected()
            } else {
                currentState
            }

        if (state.isRoundOver()) return listOf()


        val cards =
            if (playerId == this.playerId) {
                handCards.intersect(state.unplayedCards().toSet()).toList()
            } else {
                state.unplayedCards().minus(handCards.toSet())
            }

        return PlayerData.playableCards(trick = currentState.trick(), trump = currentState.trick().trump, cards = cards)
    }




    /**
     * Class representing a node in the game tree
     *
     * @property roundState the round state of the node
     * @property childNodes the child nodes of the node
     * @property nonExistentChildNodes the non existent child nodes of the node
     * @property totalPoints the total points of the node for the team that played the last card in the current trick
     * @property nbTurnsSimulated the number of turns simulated for this node
     */
    private data class Node(
        val roundState: RoundState,
        var childNodes: ArrayList<Node?>,
        var nonExistentChildNodes: List<Card>,
        var totalPoints: Double,
        var nbTurnsSimulated: Int,
    ) {

        /**
         * Returns the index of the "best" child node of a given node, where "best" is determined by the highest
         * value V (i.e., most interesting to explore). The constant c can be passed to use at the end of the algorithm (with c = 0) to determine
         * the card to play.
         *
         * @param c The exploration constant.
         * @return The index of the best child node.
         */
        fun getBestChildIndex(c: Int): Int {
            var highestV = Double.MIN_VALUE
            var bestChildIdx = 0
            for ((idx, child) in childNodes.withIndex()) {
                if (child == null) continue

                if (child.nbTurnsSimulated == 0) {
                    return idx
                }

                val currV = child.totalPoints / child.nbTurnsSimulated.toDouble() +
                        c * sqrt(2 * ln(nbTurnsSimulated.toDouble()) / child.nbTurnsSimulated.toDouble())

                if (c == 0) {
                    println("child: ${child.totalPoints}")
                    println("current (v, idx): ($currV, $idx)")
                }
                if (currV > highestV) {
                    highestV = currV
                    bestChildIdx = idx
                }
            }

            return bestChildIdx
        }
    }
}
