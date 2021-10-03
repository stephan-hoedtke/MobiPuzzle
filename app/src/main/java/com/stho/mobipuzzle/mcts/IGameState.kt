package com.stho.mobipuzzle.mcts

interface IGameState {
    val isAlive: Boolean
    val isSolved: Boolean
    fun isEqualTo(other: IGameState): Boolean
    fun evaluate(): Double
    fun getLegalActions(): Sequence<IAction>
    fun apply(action: IAction): IGameState
}

