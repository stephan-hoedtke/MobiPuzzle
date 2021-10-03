package com.stho.mobipuzzle.mcts;

interface ISimulationPolicy {
    fun chooseAction(actions: List<IAction>): IAction
}

