package com.stho.mobipuzzle.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stho.mobipuzzle.Mode
import com.stho.mobipuzzle.R
import com.stho.mobipuzzle.Settings
import com.stho.mobipuzzle.Theme
import com.stho.mobipuzzle.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var modeAdapter: ArrayAdapter<Mode>
    private lateinit var themeAdapter: ArrayAdapter<Theme>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SettingsViewModel.build(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        modeAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, Mode.values())
        themeAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, Theme.values())

        binding.radioButtonModeNumbers.setOnClickListener { viewModel.setMode(Mode.NUMBERS) }
        binding.radioButtonModeText.setOnClickListener { viewModel.setMode(Mode.TEXT) }
        binding.radioButtonThemeDefault.setOnClickListener { viewModel.setTheme(Theme.DEFAULT) }
        binding.radioButtonThemeWhite.setOnClickListener { viewModel.setTheme(Theme.WHITE) }
        binding.radioButtonThemeGreen.setOnClickListener { viewModel.setTheme(Theme.GREEN) }
        binding.radioButtonThemeBordeaux.setOnClickListener { viewModel.setTheme(Theme.BORDEAUX) }

        binding.switchCongratulation.setOnCheckedChangeListener { _, value -> viewModel.setShowCongratulation(value) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.settingsLD.observe(viewLifecycleOwner, { settings -> obObserveSettings(settings) })
        updateActionBar()
    }

    private fun obObserveSettings(settings: Settings) {
        binding.radioButtonModeNumbers.isChecked = (settings.mode == Mode.NUMBERS)
        binding.radioButtonModeText.isChecked = (settings.mode == Mode.TEXT)
        binding.radioButtonThemeDefault.isChecked = (settings.theme == Theme.DEFAULT)
        binding.radioButtonThemeWhite.isChecked = (settings.theme == Theme.WHITE)
        binding.radioButtonThemeGreen.isChecked = (settings.theme == Theme.GREEN)
        binding.radioButtonThemeBordeaux.isChecked = (settings.theme == Theme.BORDEAUX)
        binding.switchCongratulation.isChecked = settings.showCongratulation
    }

    private fun updateActionBar() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.also {
            it.title = getString(R.string.title_settings)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

}
