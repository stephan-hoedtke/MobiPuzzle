package com.stho.mobipuzzle.mcts

import android.util.Log
import java.lang.Exception

/**
 * https://en.wikipedia.org/wiki/Monte_Carlo_tree_search
 * https://int8.io/monte-carlo-tree-search-beginners-guide/
 * https://github.com/int8/gomcts
 */
class MCTS(
    private val fabric: IGameFabric,
    private val simulationPolicy: ISimulationPolicy,
    private val evaluationPolicy: IEvaluationPolicy,
    private val propagationPolicy: IPropagationPolicy,
    private val maxDepth: Int) {

    private lateinit var root: Node

    /**
     * With the new game state either create a complete new monte carlo tree, or, if possible reuse the current one:
     */
    fun setup(state: IGameState) {
        root = getNodeFor(state)
    }

    private fun getNodeFor(state: IGameState): Node {
        if (this::root.isInitialized) {
            if (root.state.isEqualTo(state)) {
                return root // keep the current tree
            }
            if (root.isExpanded) {
                root.children.forEach {
                    if (it.state.isEqualTo(state)) {
                        return it // use this child node as new root
                    }
                }
            }
        }
        return Node.createRootFor(state)
    }

    fun run() {
        prepare()
        val leaf = traverse()
        runSimulationFor(leaf)
    }

    private fun runSimulationFor(node: Node) {
        val result = simulate(node)
        backPropagation(node, result)
    }

    data class BestActionInfo(val action: IAction, val node: Node, val depth: Int, val reward: Double, val simulations: Int) {

        val isSolved: Boolean = node.state.isSolved
        val winDepth: Int =
            when {
                isSolved -> depth
                node.winDepth > 0 -> depth + node.winDepth
                else -> 0
            }

        fun isEqualTo(other: BestActionInfo): Boolean =
            action.isEqualTo(other.action)
                    && node == other.node
                    && depth == other.depth
                    && reward == other.reward
                    && simulations == other.simulations
                    && isSolved == other.isSolved
    }

    fun getBestActionInfo(): BestActionInfo? =
        root.getBestAction()?.let {
            var node = root
            var depth = 0
            var reward = node.averageReward

            while (node.isExpanded) {
                val newNode = node.getChildNodeWithBestReward()
                if (newNode.averageReward < node.averageReward) {
                    break;
                }
                reward = reward.coerceAtLeast(newNode.averageReward)
                node = newNode
                depth++
            }

            BestActionInfo(it, node, depth, reward, root.simulations)
        }

    private fun prepare() {
        if (root.isSolved || root.isExpanded) {
            return
        }
        expand(root)
    }

    /**
     * Start from root R and select successive child nodes until a leaf node L is reached.
     * The root is the current game state and a leaf is any node that has a potential child
     * from which no simulation (playout) has yet been initiated. The section below says more
     * about a way of biasing choice of child nodes that lets the game tree expand towards
     * the most promising moves, which is the essence of Monte Carlo tree search.
     */
    private fun traverse(): Node {
        var node = root
        try {
            while (node.isFullyExpanded)
                node = getBestChildNode(node)

            if (node.isNotExpanded)
                expand(node)

            if (node.isHavingUnvisitedChildren)
                node = node.chooseUnvisitedChildNode()
        }
        catch(ex: Exception) {
            Log.d("DEBUG", ex.toString())
            throw ex // Debug breakpoint
        }
        return node
    }

    private fun getBestChildNode(node: Node): Node {
        if (node.isDirty) {
            node.evaluateChildNodes(evaluationPolicy)
        }
        return node.getBestChildNode()
    }

    /**
     * Unless L ends the game decisively (e.g. win/loss/draw) for either player, create
     * one (or more) child nodes and choose node C from one of them. Child nodes are
     * any valid moves from the game position defined by L.
     */
    private fun expand(node: Node) {
        if (node.isAlive) {
            if (node.isNotExpanded) {
                node.state.getLegalActions().forEach {
                    val newState = node.state.apply(it)
                    node.expand(it, newState)
                }
            }
            if (node.isNotExpanded) {
                node.kill() // because there are no legal actions available
            }
        }
    }

    /**
     *  Complete one random playout from node C. This step is sometimes also called playout
     *  or rollout. A playout may be as simple as choosing uniform random moves until the game
     *  is decided (for example in chess, the game is won, lost, or drawn).
     */
    private fun simulate(node: Node): GameResult  {
        var state = node.state
        var depth = 0
        while (state.isAlive) {
            if (isDead(depth++)) {
                return GameResult.alive(state.evaluate(), depth)
            } else {
                val actions = state.getLegalActions().toList()
                if (actions.isEmpty()) {
                    return GameResult.dead(depth)
                }
                val action = simulationPolicy.chooseAction(state, actions)
                state = state.apply(action)
            }
        }
        /**
         * found a winning path
         */
        return GameResult.win(depth)
    }

    private fun isDead(depth: Int): Boolean =
        depth > maxDepth

    /**
     * Use the result of the playout to update information in the nodes on the path from C to R.
     */
    private fun backPropagation(node: Node, result: GameResult) {
        var value = result.value
        var parent: Node? = node
        while (parent != null) {
            propagationPolicy.propagate(parent, value)
            if (result.isWin) {
                if (parent.winDepth > result.depth || parent.winDepth == 0)
                    parent.winDepth = result.depth
            }
            parent = getParent(parent)
            value = GameResult.decrement(value) // reduce the reward by this delta for every depth
        }
    }

    private fun getParent(node: Node): Node? =
        if (isRoot(node)) null else node.parent

    private fun isRoot(node: Node): Boolean =
        node == root
}

