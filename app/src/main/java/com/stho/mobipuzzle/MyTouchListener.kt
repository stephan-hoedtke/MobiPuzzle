package com.stho.mobipuzzle

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.annotation.SuppressLint
import android.graphics.Point
import android.opengl.Visibility
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.os.persistableBundleOf
import androidx.dynamicanimation.animation.DynamicAnimation
import com.stho.mobipuzzle.databinding.FragmentHomeBinding
import com.stho.mobipuzzle.ui.home.HomeFragment
import com.stho.mobipuzzle.ui.home.HomeViewModel
import org.w3c.dom.Text
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
            moveDragViews(move)
            viewModel.moveTo(move.pieceNumber, move.toFieldNumber)
        } else {
            restoreDragViews(move)
        }
    }

    private fun getStartPosition(pieceNumber: Int): Position {
        return pieceStartPositions[pieceNumber] ?: Position()
    }

    private fun restoreDragViews(move: Game.Move) {
        for (fieldNumber in move.movingFields) {
            val pieceNumber = game.getPieceNumberOf(fieldNumber)
            val piece: TextView = getPiece(pieceNumber)
            val position = getStartPosition(pieceNumber)
            piece.x = position.x
            piece.y = position.y
            piece.visibility = View.INVISIBLE
        }
        viewModel.touch()
//            if (piece.x != position.x || piece.y != position.y)
//                piece.animate()
//                    .setDuration(DURATION)
//                    .x(position.x)
//                    .y(position.y)
//                    .setListener(object : AnimatorListener {
//                        override fun onAnimationStart(p0: Animator?) {
//                            // ignore
//                        }
//
//                        override fun onAnimationEnd(p0: Animator?) {
//                            // ignore
//                            viewModel.touch()
//                        }
//
//                        override fun onAnimationCancel(p0: Animator?) {
//                            // ignore
//                        }
//
//                        override fun onAnimationRepeat(p0: Animator?) {
//                            // ignore
//                        }
//                    })
    }

    private fun getPositionOf(fieldNumber: Int): Position {
        val field: FrameLayout = getFieldFor(fieldNumber)
        return Position(field.x, field.y)
    }

    private fun moveDragViews(move: Game.Move) {
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
        viewModel.touch()
//
//            if (piece.x != targetPosition.x || piece.y != targetPosition.y) {
//                val animator = piece.animate()
//                    .setDuration(DURATION)
//                    .x(targetPosition.x)
//                    .y(targetPosition.y)
//                    .setListener(object : AnimatorListener {
//                        override fun onAnimationStart(p0: Animator?) {
//                            // ignore
//                        }
//
//                        override fun onAnimationEnd(p0: Animator?) {
//                            piece.visibility = View.INVISIBLE
//                            piece.x = position.x
//                            piece.y = position.y
//                            viewModel.touch()
//                        }
//
//                        override fun onAnimationCancel(p0: Animator?) {
//                            // ignore
//                        }
//
//                        override fun onAnimationRepeat(p0: Animator?) {
//                            // ignore
//                        }
//                    })
//            } else {     }
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

