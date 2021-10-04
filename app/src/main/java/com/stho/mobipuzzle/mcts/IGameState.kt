package com.stho.mobipuzzle.mcts

import com.stho.mobipuzzle.game.MyAction

interface IGameState {
    val isAlive: Boolean
    val isSolved: Boolean
    fun isEqualTo(other: IGameState): Boolean
    fun evaluate(): Double
    fun getLegalActions(): Collection<IAction>
    fun apply(action: IAction): IGameState
}

