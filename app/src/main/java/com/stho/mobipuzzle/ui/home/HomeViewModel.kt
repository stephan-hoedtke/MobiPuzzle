package com.stho.mobipuzzle.ui.home

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.stho.mobipuzzle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = application.getRepository()

    val gameLD: LiveData<Game> = repository.gameLD
    val settingsLD: LiveData<Settings> = repository.settingsLD
    val movesCounterLD: LiveData<Int> = repository.movesCounterLD
    val secondsCounterLD: LiveData<Long> = repository.secondsCounterLD
    val summaryLD: LiveData<Summary> = repository.summaryLD

    fun moveTo(pieceNumber: Int, fieldNumber: Int): Boolean =
        repository.movePieceTo(pieceNumber, fieldNumber)

    val game: Game
        get() = repository.game

    val showCongratulation: Boolean
        get() = repository.settings.showCongratulation

    fun touchGame() {
        repository.touchGame()
    }

    fun startNewGame() {
        repository.startNewGame()
    }

    fun countSeconds() {
        repository.countSeconds()
    }

    fun setStatusCongratulated() {
        repository.setStatusCongratulated()
    }

    companion object {

        fun build(fragment: HomeFragment): HomeViewModel =
            ViewModelProvider(fragment.requireActivity()).get(HomeViewModel::class.java)

    }
}