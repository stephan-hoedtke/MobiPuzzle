package com.stho.mobipuzzle.game

enum class Direction {
    UP,
    LEFT,
    RIGHT,
    DOWN,
    NOTHING;

    fun isDifferentFrom(direction: Direction): Boolean =
        when (this) {
            RIGHT, LEFT -> (direction != RIGHT && direction != LEFT)
            UP, DOWN -> (direction != UP && direction != DOWN)
            NOTHING -> true
        }

    companion object {

        fun getDirection(fromFieldNumber: Int, toFieldNumber: Int): Direction =
            when (toFieldNumber - fromFieldNumber) {
                +1 -> RIGHT
                +4 -> DOWN
                -1 -> LEFT
                -4 -> UP
                else -> NOTHING
            }
    }
}


