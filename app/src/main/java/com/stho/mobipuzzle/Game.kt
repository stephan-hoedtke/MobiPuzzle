package com.stho.mobipuzzle

import java.security.InvalidParameterException
import kotlin.random.Random

/**
 * FieldNumber: Int in 1..16
 * array[0..15] --> pieceNumber
 */
class Game {

    private val array: Array<Int> = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0)

    var status: Status = Status.NONE
        private set

    val isAlive: Boolean
        get() = (status == Status.ALIVE)

    val isSolved: Boolean
        get() = (status == Status.FINISHED || status == Status.CONGRATULATED)

    fun setStatusCongratulated() {
        status = Status.CONGRATULATED
    }

    private operator fun get(fieldNumber: Int): Int {
        return array[fieldNumber - 1]
    }

    private operator fun set(fieldNumber: Int, value: Int) {
        array[fieldNumber - 1] = value
    }

    fun canMoveTo(fromFieldNumber: Int, toFieldNumber: Int): Boolean {
        if (!canMove) {
            return false
        }
        if (isNextTo(fromFieldNumber, toFieldNumber)) {
            if (isEmpty(toFieldNumber)) {
                return true
            }
            if (canMoveTo(toFieldNumber, toFieldNumber + (toFieldNumber - fromFieldNumber))) {
                return true
            }
        }
        return false
    }

    private fun isEmpty(fieldNumber: Int): Boolean =
        get(fieldNumber) == 0

    private fun setEmpty(fieldNumber: Int) {
        set(fieldNumber, 0)
    }

    fun moveTo(fromFieldNumber: Int, toFieldNumber: Int): Boolean {
        if (!canMove) {
            return false
        }
        moveToRecursive(fromFieldNumber, toFieldNumber)
        setEmpty(fromFieldNumber)
        status = if (isSorted()) Status.FINISHED else Status.ALIVE
        return true
    }

    private val canMove: Boolean
        get() = (status == Status.NEW || status == Status.ALIVE)

    private fun moveToRecursive(fromFieldNumber: Int, toFieldNumber: Int) {
        if (!isEmpty(toFieldNumber)) {
            moveToRecursive(toFieldNumber, toFieldNumber + (toFieldNumber - fromFieldNumber))
        }
        val value = get(fromFieldNumber)
        set(toFieldNumber, value)
    }

    enum class Direction {
        UP,
        LEFT,
        RIGHT,
        DOWN,
        NOTHING,
    }

    class Move(val pieceNumber: Int, val fromFieldNumber: Int, val toFieldNumber: Int, val movingFields: ArrayList<Int>) {
        val direction: Direction =
            when (toFieldNumber - fromFieldNumber) {
                1 -> Direction.RIGHT
                4 -> Direction.DOWN
                -1 -> Direction.LEFT
                -4 -> Direction.UP
                else -> Direction.NOTHING
            }
    }

    fun analyze(pieceNumber: Int): Move? {
        val fromFieldNumber = getFieldNumberOf(pieceNumber)
        val neighbours = arrayOf(fromFieldNumber - 4, fromFieldNumber - 1, fromFieldNumber + 1, fromFieldNumber + 4)
        for (toFieldNumber: Int in neighbours) {
            val array = analyze(fromFieldNumber, toFieldNumber)
            if (array.isNotEmpty()) {
                return Move(
                    pieceNumber,
                    fromFieldNumber,
                    toFieldNumber,
                    movingFields = array,
                )
            }
        }
        return null
    }

    private fun analyze(from: Int, to: Int): ArrayList<Int> {
        if (isNextTo(from, to)) {
            if (isEmpty(to)) {
                return ArrayList<Int>().also { it.add(from) }
            }
            val array = analyze(to, to + (to - from))
            if (array.isNotEmpty()) {
                return array.also { it.add(from) }
            }
        }
        return ArrayList()
    }

    fun shuffle(n: Int): Game {
        initialize()
        val i = getIndexOf(0)
        move(n, i)
        status = Status.NEW
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

    private fun isSorted(): Boolean {
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

    /**
     * Returns the pieceNumber in the range of 1..15 which is found in the field, or 0 if there is no piece
     */
    fun getPieceNumberOf(fieldNumber: Int): Int {
        return get(fieldNumber)
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

            else -> false
        }
    }
}

private fun Array<Int>.swap(i: Int, j: Int) {
    val x = this[i]
    this[i] = this[j]
    this[j] = x
}
