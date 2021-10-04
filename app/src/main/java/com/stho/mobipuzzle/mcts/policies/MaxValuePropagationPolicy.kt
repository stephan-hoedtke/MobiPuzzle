package com.stho.mobipuzzle.mcts.policies

import com.stho.mobipuzzle.mcts.IPropagationPolicy
import com.stho.mobipuzzle.mcts.Node

class MaxValuePropagationPolicy : IPropagationPolicy {

    override fun propagate(node: Node, value: Double) {
        node.simulations += 1
        node.cumulatedReward += value
        node.averageReward = node.averageReward.coerceAtLeast(value)
        node.touch()
    }
}

