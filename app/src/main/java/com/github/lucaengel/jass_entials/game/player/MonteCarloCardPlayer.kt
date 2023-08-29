package com.github.lucaengel.jass_entials.game.player

import com.github.lucaengel.jass_entials.data.cards.Card
import com.github.lucaengel.jass_entials.data.cards.PlayerData
import com.github.lucaengel.jass_entials.data.game_state.GameStateHolder
import com.github.lucaengel.jass_entials.data.game_state.PlayerId
import com.github.lucaengel.jass_entials.data.game_state.RoundState
import com.github.lucaengel.jass_entials.data.game_state.Score
import java.util.SplittableRandom
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random


/**
 * Plays a card from the player's hand for the given game state based on the Monte Carlo Tree Search algorithm.
 *
 * @param playerId The id of the player for which the card is to be determined.
 * @param seed The seed for the random number generator.
 * @param nbSimulations The number of simulations to be performed.
 */
class MonteCarloCardPlayer(
    val playerId: PlayerId,
    seed: Long = Random.nextLong(),
    private val nbSimulations: Int = 9
) {

    private val rng = SplittableRandom(seed)

    companion object {
        private const val EXPLORATION_CONSTANT = 40
    }

    init {
        if (nbSimulations < 9) {
            throw IllegalArgumentException("Number of simulations must be at least 9")
        }
    }

    // TODO: make sure that when only teammates have trump, no trump card is played as the first card
    //  (except if the player has only trump cards)
    //  i.e., fill out the trump cards for the team mate based on the already played cards and the hand cards

    /**
     * Class representing a CPU player using the Monte Carlo Tree Search algorithm
     *
     * @param roundState The current round state.
     * @param handCards The hand cards of the player for which the card is to be determined.
     * @return The card to be played.
     */
    fun monteCarloCardToPlay(roundState: RoundState, handCards: List<Card>): Card {
        val playableCards = determinePlayableCards(roundState, roundState.nextPlayer(), handCards)

        println("playable cards: $playableCards")

        val root = Node(
            roundState = if (roundState.trick().isFull()) roundState.withTrickCollected() else roundState,
            childNodes = ArrayList(List(playableCards.size) { null }),
            nonExistentChildNodes = playableCards,
            totalPoints = 0,
            nbTurnsSimulated = 0,
        )

        while (root.nbTurnsSimulated < nbSimulations) {
            val path = addNodeAndGetPath(root, handCards)
            val score = simulateRandomRound(path.last().roundState, handCards)

            root.nbTurnsSimulated++
            for (node in path) {
                node.nbTurnsSimulated++

                // update the total points of the node with the points that the team scored
                val nodeTrick = node.roundState.trick()

                node.totalPoints += score.roundPoints(nodeTrick.lastPlayer().teamId())
            }
        }

        val trick = root.childNodes[root.getBestChildIndex(0)]!!.roundState.trick()

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

        val cardToPlay = currNode.nonExistentChildNodes[0]
        var currState =
            (if (currNode.roundState.trick().isFull()) {
                currNode.roundState.withTrickCollected(isSimulating = true)
            } else {
                currNode.roundState
            })

        // add card of the current player
        currState = currState.withCardPlayed(cardToPlay, isSimulating = true)

        // playable cards of the next player
        val newPlayableCards = determinePlayableCards(currState, currState.nextPlayer(), handCards)


        val newLeaf = Node(
            roundState = currState,
            childNodes = ArrayList(List(newPlayableCards.size) { null }),
            nonExistentChildNodes = newPlayableCards.toMutableList(),
            totalPoints = 0,
            nbTurnsSimulated = 0,
        )

        val index = currNode.childNodes.size - currNode.nonExistentChildNodes.size

        currNode.nonExistentChildNodes -= cardToPlay
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
                initialState.withTrickCollected(isSimulating = true)
            } else {
                initialState
            }

        while (!currState.isRoundOver()) {
            val playableCards = determinePlayableCards(currState, currState.nextPlayer(), handCards)

            if (playableCards.isEmpty()) break // todo: check why isRoundOver() does not work

            val num = rng.nextInt(playableCards.size)
            val cardToPlay = playableCards[num]

            currState = currState.withCardPlayed(cardToPlay, isSimulating = true)

            if (currState.trick().isFull()) {
                currState = currState.withTrickCollected(isSimulating = true)
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
                currentState.withTrickCollected(isSimulating = true)
            } else {
                currentState
            }

        if (state.isRoundOver()) return listOf()

        // filter out cards that the player does not have
        val currentPlayerPotentialCards = state.unplayedCards().filter { !currentState.suitsNotInHand().getOrDefault(playerId, listOf()).contains(it.suit) }

        // filter out cards that other players have guaranteed
        val guaranteedCards = GameStateHolder.guaranteedCards.filter { it.key != playerId }.values.flatten()
        val playableCards = currentPlayerPotentialCards.minus(guaranteedCards.toSet())

        val cards =
            if (playerId == this.playerId) {
                handCards.intersect(playableCards.toSet())
                    .ifEmpty { handCards.intersect(currentPlayerPotentialCards.toSet()) }
                    .ifEmpty { handCards.intersect(state.unplayedCards().toSet()) }
                    .toList()
            } else {
                playableCards.minus(handCards.toSet())
                    .ifEmpty { currentPlayerPotentialCards.minus(handCards.toSet()).toList() }
                    .ifEmpty { currentState.unplayedCards().minus(handCards.toSet()).toList() }
            }

//        println("determine playable cards for player $playerId: \n$cards")

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
        var totalPoints: Int,
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

                if (currV > highestV) {
                    highestV = currV
                    bestChildIdx = idx
                }
            }

            return bestChildIdx
        }
    }
}