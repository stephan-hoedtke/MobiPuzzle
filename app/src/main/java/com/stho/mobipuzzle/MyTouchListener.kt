package com.stho.mobipuzzle

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.stho.mobipuzzle.databinding.FragmentHomeBinding
import com.stho.mobipuzzle.game.Direction
import com.stho.mobipuzzle.game.MyGame
import com.stho.mobipuzzle.game.Move
import com.stho.mobipuzzle.game.MyAction
import com.stho.mobipuzzle.ui.home.HomeViewModel
import java.security.InvalidParameterException


class MyTouchListener(private val pieceNumber: Int, private val viewModel: HomeViewModel, private val binding: FragmentHomeBinding) : View.OnTouchListener {

    class Position(val x: Float = 0f, val y: Float = 0f) {
        fun add(dx: Float, dy: Float): Position = Position(x + dx, y + dy)
    }

    private val pieceStartPositions: HashMap<Int, Position> = HashMap()
    private var startPosition: Position = Position()
    private val maxDeltaX
        get() = binding.board.field2.x - binding.board.field1.x
    private val maxDeltaY
        get() = binding.board.field5.y - binding.board.field1.y
    private var move: Move? = null
    private val game: MyGame = viewModel.game

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

       return when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("DRAG", "Start moving $pieceNumber ...")
                move = game.getLegalMoveForPiece(pieceNumber)
                move?.also {
                    startPosition = Position(motionEvent.rawX, motionEvent.rawY)
                    initializeDragViews(it)
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                move?.also {
                    updateDragViewPositions(motionEvent, it)
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                move?.also {
                    moveDragViews(motionEvent, it)
                }
                move = null
                viewModel.touchGame()
                true
            }
            else -> {
                false
            }
        }

    }

    private fun initializeDragViews(move: Move) {
        pieceStartPositions.clear()
        for (fieldNumber: Int in move.movingFields) {
            val pieceNumber = game.getPieceNumberOf(fieldNumber)
            val piece: TextView = getPiece(pieceNumber)
            pieceStartPositions[pieceNumber] = Position(piece.x, piece.y)
            piece.alpha = 0.8f
            piece.invalidate()
        }
    }

    private fun updateDragViewPositions(motionEvent: MotionEvent, move: Move) {
        val dx = when (move.direction) {
            Direction.RIGHT -> (motionEvent.rawX - startPosition.x).coerceIn(0f, maxDeltaX)
            Direction.LEFT -> (motionEvent.rawX - startPosition.x).coerceIn(-maxDeltaX, 0f)
            else -> 0f
        }
        val dy = when (move.direction) {
            Direction.DOWN -> (motionEvent.rawY - startPosition.y).coerceIn(0f, maxDeltaY)
            Direction.UP -> (motionEvent.rawY - startPosition.y).coerceIn(-maxDeltaY, 0f)
            else -> 0f
        }
        for (fieldNumber: Int in move.movingFields) {
            val pieceNumber = game.getPieceNumberOf(fieldNumber)
            val position = pieceStartPositions[pieceNumber] ?: Position()
            val piece: TextView = getPiece(pieceNumber)
            piece.x = position.x + dx
            piece.y = position.y + dy
            piece.invalidate()
        }
    }

    private fun moveDragViews(motionEvent: MotionEvent, move: Move) {
        val isMove: Boolean = when (move.direction) {
            Direction.RIGHT -> (motionEvent.rawX - startPosition.x) > THRESHOLD
            Direction.LEFT -> (motionEvent.rawX - startPosition.x) < -THRESHOLD
            Direction.DOWN -> (motionEvent.rawY - startPosition.y) > THRESHOLD
            Direction.UP -> (motionEvent.rawY - startPosition.y) < -THRESHOLD
            else -> false
        }
        if (isMove) {
            moveDragViews(move)
            viewModel.moveFromTo(move.fromFieldNumber, move.toFieldNumber)
        } else {
            restoreDragViews(move)
        }
    }

    private fun getStartPosition(pieceNumber: Int): Position {
        return pieceStartPositions[pieceNumber] ?: Position()
    }

    private fun restoreDragViews(move: Move) {
        for (fieldNumber in move.movingFields) {
            val pieceNumber = game.getPieceNumberOf(fieldNumber)
            val piece: TextView = getPiece(pieceNumber)
            val position = getStartPosition(pieceNumber)
            piece.x = position.x
            piece.y = position.y
            piece.visibility = View.INVISIBLE
        }
        viewModel.touchGame()
    }

    private fun getPositionOf(fieldNumber: Int): Position {
        val field: FrameLayout = getFieldFor(fieldNumber)
        return Position(field.x, field.y)
    }

    private fun moveDragViews(move: Move) {
        val fromPosition = getPositionOf(move.fromFieldNumber)
        val toPosition = getPositionOf(move.toFieldNumber)
        val dx = toPosition.x - fromPosition.x
        val dy = toPosition.y - fromPosition.y
        for (fieldNumber in move.movingFields) {
            val pieceNumber = game.getPieceNumberOf(fieldNumber)
            val piece: TextView = getPiece(pieceNumber)
            val position = getStartPosition(pieceNumber)
            val targetPosition = position.add(dx, dy)
            piece.visibility = View.INVISIBLE
            piece.x = targetPosition.x
            piece.y = targetPosition.y
        }
        viewModel.touchGame()
    }

    private fun getFieldFor(fieldNumber: Int): FrameLayout {
        return when (fieldNumber) {
            1 -> binding.board.field1
            2 -> binding.board.field2
            3 -> binding.board.field3
            4 -> binding.board.field4
            5 -> binding.board.field5
            6 -> binding.board.field6
            7 -> binding.board.field7
            8 -> binding.board.field8
            9 -> binding.board.field9
            10 -> binding.board.field10
            11 -> binding.board.field11
            12 -> binding.board.field12
            13 -> binding.board.field13
            14 -> binding.board.field14
            15 -> binding.board.field15
            16 -> binding.board.field16
            else -> throw InvalidParameterException()
        }
    }

    private fun getPiece(pieceNumber: Int): TextView {
        return when (pieceNumber) {
            1 -> binding.board.piece1
            2 -> binding.board.piece2
            3 -> binding.board.piece3
            4 -> binding.board.piece4
            5 -> binding.board.piece5
            6 -> binding.board.piece6
            7 -> binding.board.piece7
            8 -> binding.board.piece8
            9 -> binding.board.piece9
            10 -> binding.board.piece10
            11 -> binding.board.piece11
            12 -> binding.board.piece12
            13 -> binding.board.piece13
            14 -> binding.board.piece14
            15 -> binding.board.piece15
            else -> throw InvalidParameterException()
        }
    }

    companion object {
        private const val THRESHOLD = 30f
        private const val DURATION = 300L
    }

}

