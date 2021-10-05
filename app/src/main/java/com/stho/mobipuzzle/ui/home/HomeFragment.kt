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
import com.stho.mobipuzzle.game.MyAction
import com.stho.mobipuzzle.game.MyGame
import com.stho.mobipuzzle.game.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.InvalidParameterException

import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.stho.mobipuzzle.game.MyGameState
import com.stho.mobipuzzle.mcts.MCTS
import java.text.DecimalFormat


class HomeFragment : Fragment() {

    private class Measures(
        val x0: Float,
        val dx: Float,
        val y0: Float,
        val
        dy: Float,
    )

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val handler = Handler(Looper.getMainLooper())
    private var measures: Measures? = null
    private val decimalFormat = DecimalFormat("0.0000").apply {
        decimalFormatSymbols.decimalSeparator = '.'
        decimalFormatSymbols.groupingSeparator = ','
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = HomeViewModel.build(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.buttonBestMove.setOnClickListener { startEngine() }
        binding.bestMoveInfo.setOnClickListener { showBestActionSnackbar() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.gameLD.observe(viewLifecycleOwner, { game -> onObserveGame(game) })
        viewModel.settingsLD.observe(
            viewLifecycleOwner,
            { settings -> onObserveSettings(settings) })
        viewModel.movesCounterLD.observe(
            viewLifecycleOwner,
            { moves -> onObserveMovesCounter(moves) })
        viewModel.secondsCounterLD.observe(
            viewLifecycleOwner,
            { seconds -> onObserveSecondsCounter(seconds) })
        viewModel.summaryLD.observe(viewLifecycleOwner, { summary -> onObserveSummary(summary) })
        viewModel.bestActionLD.observe(
            viewLifecycleOwner,
            { action -> onObserveBestAction(action) })
        viewModel.isAnalyserRunningLD.observe(
            viewLifecycleOwner,
            { isRunning -> onObserveIsAnalyserRunning(isRunning) })

        updateActionBar()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener { onObserverLayout() }
    }

    private fun onObserverLayout() {
        if (measures == null) {
            measures = getScreenMeasures()
            viewModel.touchGame()
        }
    }

    override fun onPause() {
        super.onPause()
        stopSecondsCounter()
    }

    override fun onResume() {
        super.onResume()
        startSecondsCounter()
        viewModel.restartRunningAnalyser()
    }

    /**
     * For some reasons these measure are not available after onResume() when the fragment is started first.
     */
    private fun getScreenMeasures(): Measures? {
        val x = requireView().findViewById<TextView>(R.id.piece1)
        if (x.x == 0f)
            x.invalidate()

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

    private fun onObserveGame(game: MyGame) {

        measures?.also {

            for (pieceNumber: Int in 1..15) {
                val fieldNumber = game.getFieldNumberOf(pieceNumber)
                if (fieldNumber > 0) {
                    val row = (fieldNumber - 1) / 4
                    val col = (fieldNumber - 1) % 4
                    val x = it.x0 + col * it.dx
                    val y = it.y0 + row * it.dx
                    drawPiece(game, pieceNumber, x, y)
                }
            }

            if (game.status == Status.FINISHED) {
                showCongratulation()
            }

            showEngineDetails(game.gameState)
        }
    }

    private fun onObserveSettings(settings: Settings) {
        viewModel.touchGame()
    }

    private fun onObserveMovesCounter(moves: Int) {
        binding.moves.text =
            resources.getQuantityString(R.plurals.label_moves_plurals, moves, moves)
    }

    private fun onObserveSecondsCounter(seconds: Long) {
        binding.seconds.text = Helpers.toTimeString(seconds)
    }

    private fun onObserveSummary(summary: Summary) {
        // Nothing...
    }

    private fun onObserveBestAction(info: MCTS.BestActionInfo?) {
        info?.also {
            onObserveBestAction(it.action as MyAction)
            showBestActionInfo(info)
        } ?: run {
            onObserveBestAction(0)
            hideBestActionInfo()
        }
    }

    private fun showBestActionInfo(info: MCTS.BestActionInfo) {
        if (viewModel.showEngineDetails) {
            val rewardString = decimalFormat.format(info.reward)
            val text = if (info.winDepth > 0) {
                getString(R.string.best_action_win_params, info.winDepth, info.depth, rewardString)
            } else {
                getString(R.string.best_action_params, info.depth, rewardString)
            }
            binding.bestMoveInfo.text = text
            binding.engineInfo.text = info.simulations.toString()
        } else {
            val text = if (info.winDepth > 0) info.winDepth.toString() else ""
            binding.bestMoveInfo.text = text
            binding.engineInfo.text = ""
        }
        showBestActionInfoColor(info.isSolved)
    }

    private fun showBestActionInfoColor(isSolved: Boolean) {
        binding.bestMoveInfo.setTextColor(if (isSolved) highlightTextColor else normalTextColor)
    }

    private fun hideBestActionInfo() {
        binding.bestMoveInfo.text = ""
        binding.engineInfo.text = ""
    }

    private fun onObserveBestAction(action: MyAction) {
        if (viewModel.showBestAction) {
            val fromFieldNumber = action.fromFieldNumber
            val pieceNumber = viewModel.game.getPieceNumberOf(fromFieldNumber)
            onObserveBestAction(pieceNumber)
        } else {
            onObserveBestAction(0)
        }
    }

    private fun onObserveBestAction(pieceNumber: Int) {
        val normalColor = normalBackgroundColor
        val selectedColor = Color.RED
        binding.board.piece1.setBackgroundColor(if (pieceNumber == 1) selectedColor else normalColor)
        binding.board.piece2.setBackgroundColor(if (pieceNumber == 2) selectedColor else normalColor)
        binding.board.piece3.setBackgroundColor(if (pieceNumber == 3) selectedColor else normalColor)
        binding.board.piece4.setBackgroundColor(if (pieceNumber == 4) selectedColor else normalColor)
        binding.board.piece5.setBackgroundColor(if (pieceNumber == 5) selectedColor else normalColor)
        binding.board.piece6.setBackgroundColor(if (pieceNumber == 6) selectedColor else normalColor)
        binding.board.piece7.setBackgroundColor(if (pieceNumber == 7) selectedColor else normalColor)
        binding.board.piece8.setBackgroundColor(if (pieceNumber == 8) selectedColor else normalColor)
        binding.board.piece9.setBackgroundColor(if (pieceNumber == 9) selectedColor else normalColor)
        binding.board.piece10.setBackgroundColor(if (pieceNumber == 10) selectedColor else normalColor)
        binding.board.piece11.setBackgroundColor(if (pieceNumber == 11) selectedColor else normalColor)
        binding.board.piece12.setBackgroundColor(if (pieceNumber == 12) selectedColor else normalColor)
        binding.board.piece13.setBackgroundColor(if (pieceNumber == 13) selectedColor else normalColor)
        binding.board.piece14.setBackgroundColor(if (pieceNumber == 14) selectedColor else normalColor)
        binding.board.piece15.setBackgroundColor(if (pieceNumber == 15) selectedColor else normalColor)
    }

    private fun onObserveIsAnalyserRunning(isRunning: Boolean) {
        val resId = if (isRunning) R.drawable.red_bulb_pressable else R.drawable.green_bulb_pressable
        binding.buttonBestMove.setImageResource(resId)
    }

    private val normalBackgroundColor: Int
        get() = MaterialColors.getColor(requireContext(), R.attr.colorSecondary, Color.TRANSPARENT)

    private val normalTextColor: Int
        get() = MaterialColors.getColor(requireContext(), R.attr.colorOnBackground, Color.TRANSPARENT)

    private val highlightTextColor: Int
        get() = Color.RED

    private fun showCongratulation() {
        if (viewModel.showCongratulation) {
            navController.navigate(HomeFragmentDirections.actionNavigationHomeToNavigationCongratulation())
        } else {
            viewModel.setStatusCongratulated()
        }
    }

    private fun showEngineDetails(state: MyGameState) {
        if (viewModel.showEngineDetails) {
            binding.evaluationInfo.text = decimalFormat.format(state.evaluate())
        } else {
            binding.evaluationInfo.text = ""
        }
    }

    private fun showBestActionSnackbar() {
        viewModel.bestActionLD.value?.also {
            showBestActionSnackbar(it)
        }
    }

    private fun showBestActionSnackbar(info: MCTS.BestActionInfo) {
        val message = info.node.history
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE).apply {
            setAction("Close", View.OnClickListener { this.dismiss() })
            val  snackbarView = getView()
            val  textView = snackbarView.findViewById(R.id.snackbar_text) as TextView
            textView.maxLines = 15 // show multiple line
            show()
        }
    }

    private val navController: NavController
        get() = Navigation.findNavController(binding.root)

    private fun drawPiece(game: MyGame, pieceNumber: Int, x: Float, y: Float) {
        val piece = getPiece(pieceNumber)
        piece.x = x
        piece.y = y
        piece.text = game.getPieceValue(pieceNumber)
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

    private fun startEngine() {
        viewModel.toggleAnalyser()
    }
}
