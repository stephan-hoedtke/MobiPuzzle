package com.stho.mobipuzzle.mcts

interface IGameFabric {
    fun create(state: IGameState): IGame
}