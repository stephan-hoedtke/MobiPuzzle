package com.stho.mobipuzzle.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stho.mobipuzzle.R
import com.stho.mobipuzzle.databinding.FragmentDashboardBinding
import com.stho.mobipuzzle.game.MyGame

class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = DashboardViewModel.build(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.headlineLD.observe(viewLifecycleOwner, { headline -> onObserveHeadline(headline) })
        viewModel.gameLD.observe(viewLifecycleOwner, { game -> onObserveGame(game) })
        updateActionBar()
    }

    private fun onObserveHeadline(headline: String) {
        binding.headline.text = headline
    }

    private fun onObserveGame(game: MyGame) {
        // nothing
    }

    private fun updateActionBar() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.also {
            it.title = getString(R.string.title_dashboard)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }
}
