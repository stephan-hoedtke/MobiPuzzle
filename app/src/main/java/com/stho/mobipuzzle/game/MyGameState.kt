package com.stho.mobipuzzle.game

import com.stho.mobipuzzle.mcts.IAction
import com.stho.mobipuzzle.mcts.IGameState
import kotlin.math.abs

class MyGameState constructor(
    val mode: Mode,
    val status: Status,
    val empty: Int,
    private val board: MyBoard,
    private val history: Array<MyBoard>,
) : IGameState {

    override val isAlive: Boolean
        get() = status == Status.NEW || status == Status.ALIVE || status == Status.NONE

    override val isSolved: Boolean
        get() = status == Status.FINISHED || status == Status.CONGRATULATED

    override fun isEqualTo(other: IGameState): Boolean =
        isEqualTo(other as MyGameState)

    private fun isEqualTo(other: MyGameState): Boolean =
        mode == other.mode && status != other.status && empty == other.empty && board.isEqualTo(other.board)

    operator fun get(index: Int): Int =
        board.getValue(index)

    /**
     * To set any real value, other than 0
     */
    fun set(index: Int, value: Int): MyGameState =
        MyGameState(mode, status, empty = if (value == 0) value else empty, board = board.setValue(index, value), history)

    fun setMode(newMode: Mode): MyGameState =
        MyGameState(mode = newMode, status, empty, board, history)

    fun setStatus(newStatus: Status): MyGameState =
        MyGameState(mode, status = newStatus, empty, board, history)

    fun swapEmptyTo(index: Int): MyGameState =
        MyGameState(mode, status, empty = index, board = board.swap(empty, index), history)

    fun setEmpty(index: Int): MyGameState =
        MyGameState(mode, status, empty = index, board = board.setValue(index, 0), history)

//    /**
//     * may invalidate the empty field
//     */
//    fun move(from: Int, to: Int): MyGameState =
//        MyGameState(mode, status, empty, field = field.move(from, to), history)
//

    override fun apply(action: IAction): IGameState =
        apply(action as MyAction)

    private fun apply(action: MyAction): MyGameState =
        move(action.fromFieldNumber, action.toFieldNumber)

    fun move(from: Int, to: Int): MyGameState =
        pushHistory()
            .moveBoard(from, to)
            .updateStatus()

    private fun moveBoard(from: Int, to: Int): MyGameState =
        moveBoardRecursive(board, from, to)
            .setEmpty(from)
            .let {
                MyGameState(mode, status, empty = from, board = it, history)
            }

    private fun moveBoardRecursive(board: MyBoard, from: Int, to: Int): MyBoard =
        if (to == empty) {
            board.move(from, to)
        } else {
            val delta = to - from
            moveBoardRecursive(board, to, to + delta).move(from, to)
        }

    private fun isNotEmpty(index: Int): Boolean =
        index == empty

    private fun pushHistory(): MyGameState =
        MyGameState(mode, status, empty, board = board, history = history + board)

    private fun updateStatus(): MyGameState =
        if (isSorted()) setStatus(Status.FINISHED) else this

    fun getArray(): IntArray =
        board.toIntArray()

    fun getLegalActionsIgnoreHistory(): Sequence<MyAction> =
        MyAction.getActionsFor(empty).asSequence()

    override fun getLegalActions(): Sequence<IAction> =
        sequence {
            MyAction.getActionsFor(empty).forEach {
                val newField = board.move(it)
                if (isNotInHistory(newField)) {
                    yield(it)
                }
            }
        }

    private fun isNotInHistory(board: MyBoard): Boolean =
        history.none() { it.isEqualTo(board) }

    /**
     * Returns the data index of a piece in the range of 1..16
     */
    fun getIndexOf(value: Int): Int =
        board.getIndexOf(value)

    /**
     * Return true, of the array is sorted (mind, in TEXT-mode there are multiple sorted solutions)
     */
    fun isSorted(): Boolean =
        (1..15).all { getPieceValueFor(fieldNumber = it) == getPieceValue(pieceNumber = it) }

    private fun getPieceValueFor(fieldNumber: Int): String =
        getPieceValue(pieceNumber = board.getValue(fieldNumber), mode)

    fun getPieceValue(pieceNumber: Int): String =
        getPieceValue(pieceNumber, mode)

    fun getMoveFor(action: MyAction): Move =
        Move(action.fromFieldNumber, action.toFieldNumber, empty)

    /**
     * Return a score which
     * - counts all pieces at a wrong place (good: 0, bad: 15)
     * - counts the distance of all pieces from its original positions (good: 0, bad: 15 * 6 = 90)
     */
    override fun evaluate(): Double {
        var totalMisplaces = 0
        var totalDistance = 0
        for (fieldNumber in 1..16) {
            val pieceNumber = board.getValue(fieldNumber)
            if (pieceNumber > 0) {
                val actualValue = getPieceValue(pieceNumber, mode)
                val expectedDefaultValue = getPieceValue(pieceNumber = fieldNumber, mode)
                if (actualValue != expectedDefaultValue) {
                    totalDistance += distance(fieldNumber, pieceNumber)
                    totalMisplaces += 1
                }
            }
        }

        if (totalMisplaces == 0) {
            return 1.0
        } else {
            val distancePenalty = totalDistance * 0.001 // 0 .. 90/1000 --> 0 .. 0.1
            val misplacesPenalty = totalMisplaces * 0.001 // 0 .. 15/1000 --> 0 .. 0.1
            return 0.3 - distancePenalty - misplacesPenalty
        }
    }

    companion object {

        fun create(mode: Mode, status: Status, array: IntArray) =
            MyBoard.fromArray(array).let {
                MyGameState(mode, status, it.getIndexOf(0), it, emptyArray())
            }

        fun create(mode: Mode, status: Status, empty: Int, array: IntArray) =
            MyGameState(mode, status, empty, MyBoard.fromArray(array), emptyArray())



        // TODO: this is for debugging...
        val defaultValue =
            MyGameState.create(Mode.NUMBERS, Status.NEW, intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 15, 9, 0, 13, 14))

        val plainValue =
            MyGameState.create(Mode.NUMBERS, Status.NEW, intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0))

        private fun getPieceValue(pieceNumber: Int, mode: Mode): String =
            when (mode) {
                Mode.NUMBERS -> NUMBERS[pieceNumber]
                Mode.TEXT -> TEXTS[pieceNumber]
            }

        /**
         * Distance of two points (i, in 0..15, j in 0..3) in rows and columns
         * --> distance in 0..6
         */
        private fun distance(i: Int, j: Int): Int {
            val ci = (i - 1) % 4
            val ri = (i - 1) / 4
            val cj = (j - 1) % 4
            val rj = (j - 1) / 4
            return abs(ci - cj) + abs(ri - rj)
        }

        private const val EMPTY: String = ""
        private const val INVALID: String = "/"

        private val NUMBERS = arrayOf(INVALID, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", EMPTY)
        private val TEXTS = arrayOf(INVALID, "O", "h", "n", "e", "F", "l", "ei", "ss", "k", "e", "i", "n", "Pr", "ei", "s", EMPTY)
    }
}



