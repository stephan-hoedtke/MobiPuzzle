package com.stho.mobipuzzle.game

import kotlin.random.Random

class Shuffle() {

    private var state = MyGameState.plainValue

    fun run(n: Int): MyGameState {
        run(n, state.empty)
        return state.setStatus(Status.NEW)
    }

    private fun run(n: Int, i: Int) {
        if (n > 0) {
            var col = (i - 1) % 4
            var row = (i - 1) / 4
            when (Random.nextInt(4)) {
                0 -> row -= 1
                1 -> col += 1
                2 -> row += 1
                3 -> col -= 1
            }
            if (row < 0 || row > 3 || col < 0 || col > 3) {
                run(n, i) // repeat the call, in the end it will make use of another random number...
            } else {
                val j = row * 4 + col + 1
                if (i == j) {
                    run(n, i)
                } else {
                    swapEmptyTo(j)
                    run(n - 1, j)
                }
            }
        }
    }

    private fun swapEmptyTo(j: Int) {
        state = state.swapEmptyTo(j)
    }
}
