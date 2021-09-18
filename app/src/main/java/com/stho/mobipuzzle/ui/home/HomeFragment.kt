package com.stho.mobipuzzle.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.stho.mobipuzzle.*
import com.stho.mobipuzzle.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.InvalidParameterException



class HomeFragment : Fragment() {

    private class Measures(
        val x0: Float,
        val dx: Float,
        val y0: Float, val
        dy: Float,
    )

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val fieldTexts: HashMap<Int, String> = HashMap<Int, String>().also { it.initialize(Mode.NUMBERS) }
    private val handler = Handler(Looper.getMainLooper())
    private var measures: Measures? = null

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

        binding.buttonNewGame.setOnClickListener { viewModel.startNewGame() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.gameLD.observe(viewLifecycleOwner, { game -> onObserveGame(game) })
        viewModel.settingsLD.observe(viewLifecycleOwner, { settings -> onObserveSettings(settings) })
        viewModel.movesCounterLD.observe(viewLifecycleOwner, { moves -> onObserveMovesCounter(moves) })
        viewModel.secondsCounterLD.observe(viewLifecycleOwner, { seconds -> onObserveSecondsCounter(seconds) })
        viewModel.summaryLD.observe(viewLifecycleOwner, { summary -> onObserveSummary(summary) })

        updateActionBar()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().application.saveRepository()
        stopSecondsCounter()
    }

    override fun onResume() {
        super.onResume()
        viewModel.touchGame()
        startSecondsCounter()
    }

    private fun getMeasures(): Measures? {
        if (binding.board.piece1.x > 0) {
            return Measures(
                x0 = binding.board.piece1.x,
                y0 = binding.board.piece1.y,
                dx = binding.board.piece2.x - binding.board.piece1.x,
                dy = binding.board.piece2.y - binding.board.piece2.y,
            )
        } else {
            return null
        }
    }

    private fun onObserveGame(game: Game) {

        if (measures == null)
            measures = getMeasures()

        measures?.also {

            for (pieceNumber: Int in 1..15) {
                val fieldNumber = game.getFieldNumberOf(pieceNumber)
                if (fieldNumber > 0) {
                    val row = (fieldNumber - 1) / 4
                    val col = (fieldNumber - 1) % 4
                    val x = it.x0 + col * it.dx
                    val y = it.y0 + row * it.dx
                    drawPiece(pieceNumber, x, y)
                }
            }

            if (game.isSolved) {
                binding.game.setBackgroundColor(Color.CYAN)
            } else {
                binding.game.setBackgroundColor(Color.TRANSPARENT)
            }

            if (game.status == Status.FINISHED) {
                showCongratulation()
            }
        }
    }

    private fun onObserveSettings(settings: Settings) {
        fieldTexts.initialize(settings.mode)
    }

    private fun onObserveMovesCounter(moves: Int) {
        binding.moves.text = resources.getQuantityString(R.plurals.label_moves_plurals, moves, moves)
    }

    private fun onObserveSecondsCounter(seconds: Long) {
        binding.seconds.text = Helpers.toTimeString(seconds)
    }

    private fun onObserveSummary(summary: Summary) {
        // Nothing...
    }

    private fun showCongratulation() {
        if (viewModel.showCongratulation) {
            navController.navigate(HomeFragmentDirections.actionNavigationHomeToNavigationCongratulation())
        } else {
            viewModel.setStatusCongratulated()
        }
    }

    private val navController: NavController
        get() = Navigation.findNavController(binding.root)

    private fun drawPiece(pieceNumber: Int, x: Float, y: Float) {
        val piece = getPiece(pieceNumber)
        piece.x = x
        piece.y = y
        piece.text = fieldTexts[pieceNumber]
        piece.visibility = View.VISIBLE
        piece.alpha = 1f
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

    private fun startSecondsCounter() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.countSeconds()
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }

    private fun stopSecondsCounter() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateActionBar() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.also {
            it.title = getString(R.string.title_home)
            it.setDisplayHomeAsUpEnabled(false)
            it.setHomeButtonEnabled(true)
        }
    }
}

fun HashMap<Int, String>.initialize(mode: Mode) {
    when (mode) {
        Mode.NUMBERS -> {
            for (i in 1..15) this[i] = i.toString()
        }
        Mode.TEXT -> {
            this[1] = "O"
            this[2] = "h"
            this[3] = "n"
            this[4] = "e"
            this[5] = "F"
            this[6] = "l"
            this[7] = "ei"
            this[8] = "ss"
            this[9] = "k"
            this[10] = "e"
            this[11] = "i"
            this[12] = "n"
            this[13] = "Pr"
            this[14] = "ei"
            this[15] = "s"
        }
    }
}
