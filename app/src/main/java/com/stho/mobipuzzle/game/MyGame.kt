package com.stho.mobipuzzle.game

import com.stho.mobipuzzle.mcts.IGame

class MyGame(private var state: MyGameState = MyGameState.defaultValue) : IGame {

    var status: Status
        get() = state.status
        set(value) {
            if (state.status != value) {
                state = state.setStatus(newStatus = value)
            }
        }

    var mode: Mode
        get() = state.mode
        set(value) {
            state = state.setMode(newMode = value)
        }

    override val isAlive: Boolean
        get() = (status == Status.ALIVE || status == Status.NONE || status == Status.NEW)

    override val isSolved: Boolean
        get() = (status == Status.FINISHED || status == Status.CONGRATULATED)

    override val gameState: MyGameState
        get() = state

    fun setStatusCongratulated() {
        status = Status.CONGRATULATED
    }

    fun moveTo(fromFieldNumber: Int, toFieldNumber: Int): MyGame {
        state = state.move(fromFieldNumber, toFieldNumber)
        return this
    }

    private val canMove: Boolean
        get() = (status == Status.NEW || status == Status.ALIVE)

    fun canMoveTo(fromFieldNumber: Int, toFieldNumber: Int): Boolean =
        canMove && MyAction.getActionsFor(state.empty)
            .any { it.fromFieldNumber == fromFieldNumber && it.toFieldNumber == toFieldNumber }

    fun getLegalMoveForPiece(pieceNumber: Int): Move? {
        val fieldNumber = getFieldNumberOf(pieceNumber)
        return getLegalMoveForField(fieldNumber)
    }

    private fun getLegalMoveForField(fieldNumber: Int): Move? =
        MyAction.getActionsFor(state.empty).firstOrNull { it.fromFieldNumber == fieldNumber }
            ?.let { Move.create(it, state.empty) }

    fun shuffle(n: Int) {
        state = Shuffle().run(n)
    }


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
}
