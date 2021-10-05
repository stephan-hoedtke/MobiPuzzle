package com.stho.mobipuzzle.mcts

data class GameResult(val depth: Int, val value: Double, val isWin: Boolean) {

    companion object {

        fun alive(value: Double, depth: Int) =
            GameResult(depth, decrement(value, depth), isWin = false)

        fun win(depth: Int) =
            GameResult(depth, decrement(WIN, depth), isWin = true)

        fun dead(depth: Int) =
            GameResult(depth, ZERO, false)

        fun decrement(value: Double): Double =
            (value - DELTA).coerceAtLeast(ZERO)

        private fun decrement(value: Double, depth: Int): Double =
            (value - DELTA * depth).coerceAtLeast(ZERO)


        private const val WIN = 1.0
        private const val ZERO = 0.0
        private const val DELTA = 0.0001
    }
}


