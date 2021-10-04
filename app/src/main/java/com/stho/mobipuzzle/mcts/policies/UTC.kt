package com.stho.mobipuzzle.mcts.policies

import com.stho.mobipuzzle.mcts.IEvaluationPolicy
import com.stho.mobipuzzle.mcts.Node
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Upper Confidence Bound
 */
class UTC(private val C: Double) : IEvaluationPolicy {

    /**
     * The UCT function is called only for notes which are fully expanded
     */
    override fun evaluate(node: Node): Double {
        val w = node.averageReward
        val n = node.simulations
        val t = node.parent?.simulations ?: 0
        return evaluate(w, n, t)
    }

    private fun evaluate(w: Double, n: Int, t: Int): Double =
         w + C * sqrt(ln(t.toDouble()) / n)
}



