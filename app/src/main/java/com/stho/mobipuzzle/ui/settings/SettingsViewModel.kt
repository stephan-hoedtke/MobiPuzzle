package com.stho.mobipuzzle.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.stho.mobipuzzle.*
import com.stho.mobipuzzle.game.Mode

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = application.getRepository()

    val settingsLD: LiveData<Settings>
        get() = repository.settingsLD

    fun setMode(mode: Mode) {
        repository.settings.mode = mode
        repository.game.mode = mode
        repository.touchSettings()
    }

    fun setTheme(theme: Theme) {
        repository.settings.theme = theme
        repository.touchSettings()
    }

    fun setShowCongratulation(value: Boolean) {
        repository.settings.showCongratulation = value
        repository.touchSettings()
    }

    companion object {

        fun build(fragment: SettingsFragment): SettingsViewModel =
            ViewModelProvider(fragment.requireActivity()).get(SettingsViewModel::class.java)
    }
}
