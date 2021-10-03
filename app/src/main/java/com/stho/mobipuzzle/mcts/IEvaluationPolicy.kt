package com.stho.mobipuzzle.mcts

interface IEvaluationPolicy {
    fun evaluate(node: Node): Double
}
