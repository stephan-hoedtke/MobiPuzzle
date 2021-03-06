package com.stho.mobipuzzle.mcts.policies

import com.stho.mobipuzzle.mcts.IPropagationPolicy
import com.stho.mobipuzzle.mcts.Node

class AveragePropagationPolicy : IPropagationPolicy {

    override fun propagate(node: Node, value: Double) {
        node.simulations += 1
        node.cumulatedReward += value
        node.averageReward = node.cumulatedReward / node.simulations
        node.touch()
    }
}

