package com.stho.mobipuzzle.game

import com.stho.mobipuzzle.mcts.IAction
import com.stho.mobipuzzle.mcts.IGame

/**
 * FieldNumber: Int in 1..16
 * array[0..15] --> pieceNumber
 */
class MyGame(
    private var state: MyGameState = MyGameState.defaultValue
) : IGame {

    var status: Status
        get() = state.status
        set(value) {
            if (state.status != value) {
                state = state.setStatus(newStatus = value)
            }
        }

    var mode: Mode
        get() = state.mode
        set(value) { state = state.setMode(newMode = value) }

    override val isAlive: Boolean
        get() = (status == Status.ALIVE || status == Status.NONE || status == Status.NEW)

    override val isSolved: Boolean
        get() = (status == Status.FINISHED || status == Status.CONGRATULATED)

    override val gameState: MyGameState
        get() = state

    fun setStatusCongratulated() {
        status = Status.CONGRATULATED
    }

    private fun isEmpty(fieldNumber: Int): Boolean =
        state[fieldNumber] == 0

    private fun isNotEmpty(fieldNumber: Int): Boolean =
        state[fieldNumber] > 0

    fun moveTo(fromFieldNumber: Int, toFieldNumber: Int): MyGame {
        state = state.move(fromFieldNumber, toFieldNumber)
        return this
    }

    private val canMove: Boolean
        get() = (status == Status.NEW || status == Status.ALIVE)

    fun canMoveTo(fromFieldNumber: Int, toFieldNumber: Int): Boolean {
        if (!canMove) {
            return false
        }
        MyAction.getActionsFor(state.empty).forEach {
            if (it.fromFieldNumber == fromFieldNumber && it.toFieldNumber == toFieldNumber) {
                return true
            }
        }
        return false
    }

    fun getLegalMoveForPiece(pieceNumber: Int): Move? {
        val fieldNumber = getFieldNumberOf(pieceNumber)
        return getLegalMoveFor(fieldNumber)
    }

    private fun getLegalMoveFor(fieldNumber: Int): Move? {
        MyAction.getActionsFor(state.empty).forEach {
            if (it.fromFieldNumber == fieldNumber) {
                return Move(it.fromFieldNumber, it.toFieldNumber, state.empty)
            }
        }
        return null
    }

    fun shuffle(n: Int) {
        state = Shuffle().run(n)
    }


    /**
     * Return true, of the array is sorted (mind, in TEXT-mode there are multiple sorted solutions)
     */
    private fun isSorted(): Boolean =
        state.isSorted()

    /**
     * Returns the fieldNumber in the range of 1..16 where the piece is found, or 0 otherwise
     */
    fun getFieldNumberOf(pieceNumber: Int): Int =
        state.getIndexOf(pieceNumber)

    /**
     * Returns the pieceNumber in the range of 1..15 which is found in the field, or 0 if there is no piece
     */
    fun getPieceNumberOf(fieldNumber: Int): Int =
        state[fieldNumber]

    /**
     * Returns the face value of the piece with respect to the current mode
     */
    fun getPieceValue(pieceNumber: Int): String =
        state.getPieceValue(pieceNumber)

    companion object {

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
        private fun isNeighbour(from: Int, to: Int): Boolean {
            return when (from) {
                1 -> to == 2 || to == 5
                2 -> to == 1 || to == 3 || to == 6
                3 -> to == 2 || to == 4 || to == 7
                4 -> to == 3 || to == 8
                5 -> to == 1 || to == 6 || to == 9
                6 -> to == 2 || to == 5 || to == 7 || to == 10
                7 -> to == 3 || to == 6 || to == 8 || to == 11
                8 -> to == 4 || to == 7 || to == 12
                9 -> to == 5 || to == 10 || to == 13
                10 -> to == 6 || to == 9 || to == 11 || to == 14
                11 -> to == 7 || to == 10 || to == 12 || to == 15
                12 -> to == 8 || to == 11 || to == 16
                13 -> to == 9 || to == 14
                14 -> to == 10 || to == 13 || to == 15
                15 -> to == 11 || to == 14 || to == 16
                16 -> to == 12 || to == 15
                else -> false
            }
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
        private fun getNeighbours(from: Int): IntArray {
            return when (from) {
                1 -> intArrayOf(2, 5)
                2 -> intArrayOf(1, 3, 6)
                3 -> intArrayOf(2, 4, 7)
                4 -> intArrayOf(3, 8)
                5 -> intArrayOf(1, 6, 9)
                6 -> intArrayOf(2, 5, 7, 10)
                7 -> intArrayOf(3, 6, 8, 11)
                8 -> intArrayOf(4, 7, 12)
                9 -> intArrayOf(5, 10, 13)
                10 -> intArrayOf(6, 9, 11, 14)
                11 -> intArrayOf(7, 10, 12, 15)
                12 -> intArrayOf(8, 11, 16)
                13 -> intArrayOf(9, 14)
                14 -> intArrayOf(10, 13, 15)
                15 -> intArrayOf(11, 14, 16)
                16 -> intArrayOf(12, 15)
                else -> intArrayOf()
            }
        }

    }
}
