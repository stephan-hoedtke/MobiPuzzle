package com.stho.mobipuzzle.game

data class Move(val fromFieldNumber: Int, val toFieldNumber: Int, val empty: Int) {

    enum class Direction {
        UP,
        LEFT,
        RIGHT,
        DOWN,
        NOTHING,
    }

    private val delta: Int =
        toFieldNumber - fromFieldNumber

    val direction: Direction =
       when (delta) {
            +1 -> Direction.RIGHT
            +4 -> Direction.DOWN
            -1 -> Direction.LEFT
            -4 -> Direction.UP
            else -> Direction.NOTHING
        }

    val movingFields: IntArray =
        getMovingFields(fromFieldNumber, toFieldNumber, empty)

    companion object {
        private fun getMovingFields(fromFieldNumber: Int, toFieldNumber: Int, empty: Int): IntArray =
            if (toFieldNumber == empty)
                intArrayOf(fromFieldNumber)
            else
                getMovingFields(toFieldNumber, toFieldNumber + (toFieldNumber - fromFieldNumber), empty) + fromFieldNumber
    }
}

