package com.stho.mobipuzzle.mcts

interface IPropagationPolicy {
    fun propagate(node: Node, value: Double)
}
