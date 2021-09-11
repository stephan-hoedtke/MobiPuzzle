package com.stho.mobipuzzle.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.stho.mobipuzzle.*
import com.stho.mobipuzzle.databinding.FragmentHomeBinding
import java.security.InvalidParameterException


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = HomeViewModel.build(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.board.piece1.setOnTouchListener(MyTouchListener(1, viewModel, binding))
        binding.board.piece2.setOnTouchListener(MyTouchListener(2, viewModel, binding))
        binding.board.piece3.setOnTouchListener(MyTouchListener(3, viewModel, binding))
        binding.board.piece4.setOnTouchListener(MyTouchListener(4, viewModel, binding))
        binding.board.piece5.setOnTouchListener(MyTouchListener(5, viewModel, binding))
        binding.board.piece6.setOnTouchListener(MyTouchListener(6, viewModel, binding))
        binding.board.piece7.setOnTouchListener(MyTouchListener(7, viewModel, binding))
        binding.board.piece8.setOnTouchListener(MyTouchListener(8, viewModel, binding))
        binding.board.piece9.setOnTouchListener(MyTouchListener(9, viewModel, binding))
        binding.board.piece10.setOnTouchListener(MyTouchListener(10, viewModel, binding))
        binding.board.piece11.setOnTouchListener(MyTouchListener(11, viewModel, binding))
        binding.board.piece12.setOnTouchListener(MyTouchListener(12, viewModel, binding))
        binding.board.piece13.setOnTouchListener(MyTouchListener(13, viewModel, binding))
        binding.board.piece14.setOnTouchListener(MyTouchListener(14, viewModel, binding))
        binding.board.piece15.setOnTouchListener(MyTouchListener(15, viewModel, binding))

        binding.buttonNewGame.setOnClickListener { viewModel.startNew() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.gameLD.observe(viewLifecycleOwner, { game -> onObserveGame(game) })
        viewModel.movesCounterLD.observe(viewLifecycleOwner, { moves -> onObserveMovesCounter(moves) })
        viewModel.secondsCounterLD.observe(viewLifecycleOwner, { seconds -> onObserveSecondsCounter(seconds) })
    }

    override fun onPause() {
        super.onPause()
        requireActivity().application.save()
    }

    private fun onObserveGame(game: Game) {
        for (pieceNumber: Int in 1..15) {
            val position = game.getFieldNumberOf(pieceNumber)
            if (position > 0) {
                drawPiece(pieceNumber, position)
            }
        }
        if (game.isSolved) {
            binding.game.setBackgroundColor(Color.CYAN)
        } else {
            binding.game.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun onObserveMovesCounter(moves: Int) {
        binding.moves.text = resources.getQuantityString(R.plurals.label_moves_plurals, moves, moves)
    }

    private fun onObserveSecondsCounter(seconds: Long) {
        binding.seconds.text = Helpers.toTimeString(seconds)
    }

    private fun drawPiece(pieceNumber: Int, position: Int) {
        val frame: FrameLayout = getFrameLayoutForPosition(position)
        val frameLayoutParams = frame.layoutParams as ConstraintLayout.LayoutParams

        val piece = getPiece(pieceNumber)
        val pieceLayoutParams = piece.layoutParams as ConstraintLayout.LayoutParams

        pieceLayoutParams.bottomToBottom = frameLayoutParams.bottomToBottom
        pieceLayoutParams.topToTop = frameLayoutParams.topToTop
        pieceLayoutParams.startToStart = frameLayoutParams.startToStart
        pieceLayoutParams.endToEnd = frameLayoutParams.endToEnd

        piece.layoutParams = pieceLayoutParams
        piece.setBackgroundColor(piece.getColor(R.color.fieldColor))
        piece.setTextColor(piece.getColor(R.color.fieldTextColor))
    }

    private fun getFrameLayoutForPosition(position: Int): FrameLayout {
        return when (position) {
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
}