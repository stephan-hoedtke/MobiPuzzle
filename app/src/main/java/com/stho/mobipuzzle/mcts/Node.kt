package com.stho.mobipuzzle.mcts

import java.lang.Exception
import java.lang.StringBuilder
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.random.Random

open class Node private constructor(val parent: Node?, val depth: Int, val state: IGameState) {

    private val nodes: ArrayList<Node> = ArrayList()
    private val actions: ArrayList<IAction> = ArrayList()

    var simulationReward: Double = 0.0
    var simulations: Int = 0
    var isDirty: Boolean = true
    var isDead: Boolean = false
    var value: Double = 0.0

    val isSolved
        get() = state.isSolved

    val isAlive
        get() = state.isAlive && !isDead

    /**
     * Unvisited
     */
    val isLeaf: Boolean
        get() = (simulations == 0)

    val isNotExpanded: Boolean
        get() = nodes.isEmpty()

    val isExpanded: Boolean
        get() = (nodes.size > 0)

    val isFullyExpanded: Boolean
        get() = (nodes.size > 0 && nodes.none { it.simulations == 0 })

    val isHavingUnvisitedChildren: Boolean
        get() = (nodes.size > 0 && nodes.any { it.simulations == 0 })

    val children: Collection<Node>
        get() = nodes

    operator fun get(index: Int): Node =
        nodes[index]

    fun kill() {
        isDead = true
    }

    /**
     * Update the value of all child nodes based on the evaluation policy.
     */
    fun evaluateChildNodes(policy: IEvaluationPolicy) {
        nodes.forEach {
            it.value = policy.evaluate(it)
        }
        isDirty = false
    }

    fun chooseUnvisitedChildNode(): Node =
        chooseNodeRandomly(nodes.filter { it.simulations == 0 }.toList())

    private fun chooseNodeRandomly(nodes: List<Node>): Node =
        nodes[Random.nextInt(nodes.size)]

    /**
     * Get the node with the best value. The values of all sub-nodes have to be evaluated before.
     */
    fun getBestChildNode(): Node {
        var value = 0.0
        var node: Node? = null

        nodes.forEach {
            if (it.isSolved) {
                return it
            }
            if (value < it.value || node == null) {
                value = it.value
                node = it
            }
        }

        return node!!
    }

    /**
     * Get the node with the most simulations. Should only be run for a parent node with simulations > 0.
     */
    fun getChildNodeWithMostSimulations(): Node {
        var simulations = 0
        var node: Node? = null
        nodes.forEach {
            if (simulations < it.simulations || node == null) {
                simulations = it.simulations
                node = it
            }
        }
        return node ?: throw Exception("This node doesn't have any child nodes: $this")
    }

    /**
     * Get the action with the highest number of wins
     */
    fun getBestAction(): IAction? {
        var simulations = 0
        var action: IAction? = null

        for (index in nodes.indices) {
            val node = nodes[index]
            if (node.isSolved) {
                return actions[index]
            }
            if (simulations < node.simulations || action == null) {
                simulations = node.simulations
                action = actions[index]
            }
        }

        return action
    }

    fun expand(action: IAction, state: IGameState) {
        nodes.add(createChildNode(state))
        actions.add(action)
    }

    fun propagate(value: Double) {
        simulations++
        simulationReward += value
        touch()
    }

    private fun touch() {
        isDirty = true
    }

    private fun createChildNode(state: IGameState): Node =
        Node(this, depth = depth + 1, state)

    override fun toString(): String {
        val dec = DecimalFormat("#.###")
        val sb = StringBuilder()
        if (sb.isNotEmpty()) {
            sb.append("; ")
        }
        getParentAction()?.also {
            sb.append("$it -> ")
        }
        sb.append("$simulations: ${dec.format(averageReward)}")
        when {
            isSolved -> sb.append(" win")
            isDead -> sb.append(" dead")
            isAlive -> sb.append(" ...")
        }
        return sb.toString()
    }

    val history: String
        get() {
            val sb = StringBuilder()
            parent?.also {
                sb.append(it.history)
                sb.append(", ")
                sb.append(it.getParentAction())
            }
            return sb.toString()
        }

    private val averageReward
        get() = simulationReward / simulations

    private fun getParentAction(): IAction? =
        parent?.getParentAction(this)

    private fun getParentAction(childNode: Node): IAction? {
        val index = nodes.indexOf(childNode)
        if (index != -1) {
            return actions[index]
        } else {
            return null
        }
    }

    val size: Int
        get() = nodes.size

    companion object {
        fun createRootFor(state: IGameState): Node =
            Node(parent = null, depth = 1, state)
    }
}



