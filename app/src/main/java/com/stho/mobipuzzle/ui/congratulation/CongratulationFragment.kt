package com.stho.mobipuzzle.ui.congratulation

import android.graphics.Path
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.stho.mobipuzzle.*
import com.stho.mobipuzzle.databinding.FragmentCongratulationBinding
import com.stho.mobipuzzle.databinding.FragmentDashboardBinding
import com.stho.mobipuzzle.game.MyGame
import com.stho.mobipuzzle.ui.dashboard.DashboardViewModel
import com.stho.mobipuzzle.ui.home.HomeFragmentDirections
import java.lang.Exception

class CongratulationFragment : Fragment() {

    private lateinit var viewModel: CongratulationViewModel
    private lateinit var binding: FragmentCongratulationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = CongratulationViewModel.build(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCongratulationBinding.inflate(inflater, container, false)

        binding.image.setImageResource(
            when (viewModel.settings.theme) {
                Theme.BORDEAUX -> R.drawable.bordeaux
                Theme.GREEN -> R.drawable.beer
                else -> R.drawable.smiley
            }
        )

        binding.buttonNewGame.setOnClickListener { onNewGame() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.headlineLD.observe(viewLifecycleOwner, { headline -> onObserveHeadline(headline) })
        viewModel.summaryLD.observe(viewLifecycleOwner, { summary -> onObserveSummary(summary) })
        viewModel.gameLD.observe(viewLifecycleOwner, { game -> onObserveGame(game) })
        updateActionBar()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setStatusCongratulated()
    }

    private fun onObserveHeadline(headline: String) {
        binding.headline.text = headline
    }

    private fun onObserveSummary(summary: Summary) {
        // TODO: display texts...
    }

    private fun onObserveGame(game: MyGame) {
        // TODO...
    }

    private fun onNewGame() {
        viewModel.startNewGame()
        navController.navigate(R.id.actionHomeFragment)
    }

    private val navController: NavController
        get() = Navigation.findNavController(binding.root)

    private fun updateActionBar() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.also {
            it.title = getString(R.string.title_congratulation)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }
}
