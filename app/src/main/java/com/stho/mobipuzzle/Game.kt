package com.stho.mobipuzzle

import java.security.InvalidParameterException
import kotlin.random.Random

/**
 * FieldNumber: Int in 1..16
 * array[0..15] --> pieceNumber
 */
class Game {

    private val array: Array<Int> = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0)

    private operator fun get(fieldNumber: Int): Int {
        return array[fieldNumber - 1]
    }

    private operator fun set(fieldNumber: Int, value: Int) {
        array[fieldNumber - 1] = value
    }

    fun canSwap(fromFieldNumber: Int, toFieldNumber: Int): Boolean {
        return isNextTo(fromFieldNumber, toFieldNumber) && isEmpty(toFieldNumber)
    }

    private fun isEmpty(fieldNumber: Int): Boolean {
        return get(fieldNumber) == 0
    }

    /**
     * from: Int in 1..16 and to: Int in 1..16
     */
    fun swap(fromFieldNumber: Int, toFieldNumber: Int) {
        val x = get(fromFieldNumber)
        val y = get(toFieldNumber)
        set(fromFieldNumber, y)
        set(toFieldNumber, x)
    }

    fun shuffle(): Game {
        initialize()
        // Richard Durstenfeld in 1964, based on Fisher and Yates
        for (i: Int in 15 downTo 1) {
            val j = Random.nextInt(i)
            array.swap(i, j)
        }
        return this
    }

    fun shuffleByMoves(n: Int): Game {
        initialize()
        val i = getIndexOf(0)
        move(n, i)
        return this
    }

    private fun initialize() {
        for (i: Int in 0..14) {
            array[i] = (i + 1)
        }
        array[15] = 0
    }

    private fun move(n: Int, i: Int) {
        if (n > 0) {
            var col = i % 4
            var row = i / 4
            when (Random.nextInt(4)) {
                0 -> row -= 1
                1 -> col += 1
                2 -> row += 1
                3 -> col -= 1
            }
            if (row < 0 || row > 3 || col < 0 || col > 3) {
                move(n, i)
            } else {
                val j = row * 4 + col
                if (i == j) {
                    move(n, i)
                } else {
                    array.swap(i, j)
                    move(n - 1, j)
                }
            }
        }
    }

    val isSolved: Boolean
        get() {
            for (i: Int in 0..14) {
                if (array[i] != i + 1) {
                    return false
                }
            }
            return true
        }


    /**
     * Returns the fieldNumber in the range of 1..16 where the piece is found, or 0 otherwise
     */
    fun getFieldNumberOf(pieceNumber: Int): Int {
        return getIndexOf(pieceNumber) + 1
    }

    private fun getIndexOf(pieceNumber: Int): Int {
        for (i: Int in 0..15) {
            if (array[i] == pieceNumber) {
                return i
            }
        }
        return -1
    }

    /**
     *
     *         1      2      3      4
     *
     *         5      6      7      8
     *
     *         9     10     11     12
     *
     *        13     14     15    (16)
     */

    private fun isNextTo(from: Int, to: Int): Boolean {
        return when (from) {
            1 -> { to == 2 || to == 5 }
            2 -> { to == 1 || to == 3 || to == 6 }
            3 -> { to == 2 || to == 4 || to == 7 }
            4 -> { to == 3 || to == 8 }

            5 -> { to == 1 || to == 6 || to == 9 }
            6 -> { to == 2 || to == 5 || to == 7 || to == 10 }
            7 -> { to == 3 || to == 6 || to == 8 || to == 11 }
            8 -> { to == 4 || to == 7 || to == 12 }

            9 -> { to == 5 || to == 10 || to == 13 }
            10 -> { to == 6 || to == 9 || to == 11 || to == 14 }
            11 -> { to == 7 || to == 10 || to == 12 || to == 15 }
            12 -> { to == 8 || to == 11 || to == 16 }

            13 -> { to == 9 || to == 14 }
            14 -> { to == 10 || to == 13 || to == 15  }
            15 -> { to == 11 || to == 14 || to == 16  }
            16 -> { to == 12 || to == 15 }

            else -> throw InvalidParameterException()
        }
    }
}

private fun Array<Int>.swap(i: Int, j: Int) {
    val x = this[i]
    this[i] = this[j]
    this[j] = x
}
