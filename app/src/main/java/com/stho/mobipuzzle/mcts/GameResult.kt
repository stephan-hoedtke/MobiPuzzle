package com.stho.mobipuzzle.mcts

data class GameResult(val value: Double, val isWin: Boolean) {

    companion object {

        fun alive(value: Double, depth: Int) =
            GameResult(decrement(value, depth), isWin = false)

        fun win(depth: Int) =
            GameResult(decrement(WIN, depth), isWin = true)

        fun dead() =
            GameResult(ZERO, false)

        fun decrement(value: Double): Double =
            (value - DELTA).coerceAtLeast(ZERO)

        private fun decrement(value: Double, depth: Int): Double =
            (value - DELTA * depth).coerceAtLeast(ZERO)


        private const val WIN = 1.0
        private const val ZERO = 0.0
        private const val DELTA = 0.0001
    }
}


