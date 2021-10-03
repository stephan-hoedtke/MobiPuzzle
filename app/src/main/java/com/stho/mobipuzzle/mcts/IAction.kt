package com.stho.mobipuzzle.mcts

interface IAction {
    fun isEqualTo(other: IAction): Boolean
}

