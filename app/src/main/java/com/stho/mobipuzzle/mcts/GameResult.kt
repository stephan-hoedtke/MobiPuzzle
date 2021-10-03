package com.stho.mobipuzzle.mcts

data class GameResult(val value: Double, val isWin: Boolean) {

    companion object {

        fun alive(value: Double) =
            GameResult(value, isWin = false)

        val win =
            GameResult(1.0, isWin = true)

        val dead =
            GameResult(0.0, false)
    }
}


