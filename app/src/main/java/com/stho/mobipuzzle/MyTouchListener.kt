package com.stho.mobipuzzle

import android.annotation.SuppressLint
import android.graphics.Point
import android.opengl.Visibility
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.stho.mobipuzzle.databinding.FragmentHomeBinding
import com.stho.mobipuzzle.ui.home.HomeFragment
import com.stho.mobipuzzle.ui.home.HomeViewModel
import org.w3c.dom.Text
import java.security.InvalidParameterException


class MyTouchListener(private val pieceNumber: Int, private val viewModel: HomeViewModel, private val binding: FragmentHomeBinding) : View.OnTouchListener {

    class Position(val x: Float = 0f, val y: Float = 0f)

    private val pieceStartPositions: HashMap<Int, Position> = HashMap()
    private var startPosition: Position = Position()
    private val maxDeltaX
        get() = binding.board.field2.x - binding.board.field1.x
    private val maxDeltaY
        get() = binding.board.field5.y - binding.board.field1.y
    private var move: Game.Move? = null
    private val game: Game = viewModel.game

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

       return when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("DRAG", "Start moving $pieceNumber ...")
                move = game.analyze(pieceNumber)
                move?.also {
                    startPosition = Position(motionEvent.rawX, motionEvent.rawY)
                    initializeDragViews(it)
                } ?: {
                    restoreDragViews()
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
                restoreDragViews()
                move = null
                viewModel.touch()
                true
            }
            else -> {
                //view.visibility = View.VISIBLE
                false
            }
        }

    }

    private fun initializeDragViews(move: Game.Move) {
        pieceStartPositions.clear()
        for (fieldNumber: Int in move.movingFields) {
            val pieceNumber = game.getPieceNumberOf(fieldNumber)
            val piece: TextView = getPiece(pieceNumber)
            pieceStartPositions[pieceNumber] = Position(piece.x, piece.y)
            piece.setBackgroundColor(piece.getColor(R.color.fieldAccentColor))
            piece.invalidate()
        }
    }

    private fun updateDragViewPositions(motionEvent: MotionEvent, move: Game.Move) {
        val dx = when (move.direction) {
            Game.Direction.RIGHT -> (motionEvent.rawX - startPosition.x).coerceIn(0f, maxDeltaX)
            Game.Direction.LEFT -> (motionEvent.rawX - startPosition.x).coerceIn(-maxDeltaX, 0f)
            else -> 0f
        }
        val dy = when (move.direction) {
            Game.Direction.DOWN -> (motionEvent.rawY - startPosition.y).coerceIn(0f, maxDeltaY)
            Game.Direction.UP -> (motionEvent.rawY - startPosition.y).coerceIn(-maxDeltaY, 0f)
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

    private fun moveDragViews(motionEvent: MotionEvent, move: Game.Move) {
        val isMove: Boolean = when (move.direction) {
            Game.Direction.RIGHT -> (motionEvent.rawX - startPosition.x) > THRESHOLD
            Game.Direction.LEFT -> (motionEvent.rawX - startPosition.x) < -THRESHOLD
            Game.Direction.DOWN -> (motionEvent.rawY - startPosition.y) > THRESHOLD
            Game.Direction.UP -> (motionEvent.rawY - startPosition.y) < -THRESHOLD
            else -> false
        }
        if (isMove) {
            restoreDragViews(false)
            viewModel.moveTo(move.pieceNumber, move.toFieldNumber)
        }
    }

    private fun restoreDragViews(visible: Boolean = true) {
        for (pieceNumber in pieceStartPositions.keys) {
            val position = pieceStartPositions[pieceNumber] ?: Position()
            val piece: TextView = getPiece(pieceNumber)
            piece.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            piece.x = position.x
            piece.y = position.y
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
    }

}

