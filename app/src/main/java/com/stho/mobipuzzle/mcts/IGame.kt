package com.stho.mobipuzzle.mcts

import com.stho.mobipuzzle.game.Mode

interface IGame {
    val gameState: IGameState
    val isAlive: Boolean
    val isSolved: Boolean
}

