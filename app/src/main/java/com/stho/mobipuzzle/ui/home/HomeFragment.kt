package com.stho.mobipuzzle.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.stho.mobipuzzle.databinding.FragmentHomeBinding
import java.security.InvalidParameterException
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.stho.mobipuzzle.*


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

        binding.board.field11.setOnDragListener(MyDragListener(1, viewModel))
        binding.board.field12.setOnDragListener(MyDragListener(2, viewModel))
        binding.board.field13.setOnDragListener(MyDragListener(3, viewModel))
        binding.board.field14.setOnDragListener(MyDragListener(4, viewModel))
        binding.board.field21.setOnDragListener(MyDragListener(5, viewModel))
        binding.board.field22.setOnDragListener(MyDragListener(6, viewModel))
        binding.board.field23.setOnDragListener(MyDragListener(7, viewModel))
        binding.board.field24.setOnDragListener(MyDragListener(8, viewModel))
        binding.board.field31.setOnDragListener(MyDragListener(9, viewModel))
        binding.board.field32.setOnDragListener(MyDragListener(10, viewModel))
        binding.board.field33.setOnDragListener(MyDragListener(11, viewModel))
        binding.board.field34.setOnDragListener(MyDragListener(12, viewModel))
        binding.board.field41.setOnDragListener(MyDragListener(13, viewModel))
        binding.board.field42.setOnDragListener(MyDragListener(14, viewModel))
        binding.board.field43.setOnDragListener(MyDragListener(15, viewModel))
        binding.board.field44.setOnDragListener(MyDragListener(16, viewModel))

        binding.board.piece1.setOnTouchListener(MyTouchListener(1, viewModel))
        binding.board.piece2.setOnTouchListener(MyTouchListener(2, viewModel))
        binding.board.piece3.setOnTouchListener(MyTouchListener(3, viewModel))
        binding.board.piece4.setOnTouchListener(MyTouchListener(4, viewModel))
        binding.board.piece5.setOnTouchListener(MyTouchListener(5, viewModel))
        binding.board.piece6.setOnTouchListener(MyTouchListener(6, viewModel))
        binding.board.piece7.setOnTouchListener(MyTouchListener(7, viewModel))
        binding.board.piece8.setOnTouchListener(MyTouchListener(8, viewModel))
        binding.board.piece9.setOnTouchListener(MyTouchListener(9, viewModel))
        binding.board.piece10.setOnTouchListener(MyTouchListener(10, viewModel))
        binding.board.piece11.setOnTouchListener(MyTouchListener(11, viewModel))
        binding.board.piece12.setOnTouchListener(MyTouchListener(12, viewModel))
        binding.board.piece13.setOnTouchListener(MyTouchListener(13, viewModel))
        binding.board.piece14.setOnTouchListener(MyTouchListener(14, viewModel))
        binding.board.piece15.setOnTouchListener(MyTouchListener(15, viewModel))

        binding.buttonNewGame.setOnClickListener { viewModel.startNew() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.headlineLD.observe(viewLifecycleOwner, { headline -> onObserveHeadline(headline) })
        viewModel.gameLD.observe(viewLifecycleOwner, { game -> onObserveGame(game) })
        viewModel.movesCounterLD.observe(viewLifecycleOwner, { moves -> onObserveMovesCounter(moves) })
        viewModel.secondsCounterLD.observe(viewLifecycleOwner, { seconds -> onObserveSecondsCounter(seconds) })
    }

    private fun onObserveHeadline(headline: String) {
        binding.headline.text = headline
    }

    private fun onObserveGame(game: Game) {
        for (pieceNumber: Int in 1..15) {
            val position = game.getFieldNumberOf(pieceNumber)
            if (position > 0) {
                drawPiece(pieceNumber, position)
            }
        }
    }

    private fun onObserveMovesCounter(moves: Int) {
        binding.moves.text = getString(R.string.label_moves_param, moves)
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
        piece.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.fieldColor))
        piece.setTextColor(ContextCompat.getColor(requireContext(), R.color.fieldTextColor))
    }

    private fun getFrameLayoutForPosition(position: Int): FrameLayout {
        return when (position) {
            1 -> binding.board.field11
            2 -> binding.board.field12
            3 -> binding.board.field13
            4 -> binding.board.field14
            5 -> binding.board.field21
            6 -> binding.board.field22
            7 -> binding.board.field23
            8 -> binding.board.field24
            9 -> binding.board.field31
            10 -> binding.board.field32
            11 -> binding.board.field33
            12 -> binding.board.field34
            13 -> binding.board.field41
            14 -> binding.board.field42
            15 -> binding.board.field43
            16 -> binding.board.field44
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