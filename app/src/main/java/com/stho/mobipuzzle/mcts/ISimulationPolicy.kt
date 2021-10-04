package com.stho.mobipuzzle.mcts;

interface ISimulationPolicy {
    fun chooseAction(state: IGameState, actions: List<IAction>): IAction
}

