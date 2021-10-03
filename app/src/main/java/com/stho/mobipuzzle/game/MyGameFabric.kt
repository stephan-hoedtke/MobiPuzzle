package com.stho.mobipuzzle.game

import com.stho.mobipuzzle.mcts.IGame
import com.stho.mobipuzzle.mcts.IGameFabric
import com.stho.mobipuzzle.mcts.IGameState

/**
 * Use the game fabric inside MTCS, not an instance of the game itself
 */
class MyGameFabric : IGameFabric {

    override fun create(state: IGameState): IGame =
        MyGame(state as MyGameState)

}

