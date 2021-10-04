package com.stho.mobipuzzle.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.stho.mobipuzzle.*
import com.stho.mobipuzzle.engine.Engine
import com.stho.mobipuzzle.game.MyGame
import com.stho.mobipuzzle.mcts.MCTS
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = application.getRepository()
    private val engine: Engine = Engine(viewModelScope)
    val gameLD: LiveData<MyGame> = repository.gameLD
    val settingsLD: LiveData<Settings> = repository.settingsLD
    val movesCounterLD: LiveData<Int> = repository.movesCounterLD
    val secondsCounterLD: LiveData<Long> = repository.secondsCounterLD
    val summaryLD: LiveData<Summary> = repository.summaryLD
    val bestActionLD: LiveData<MCTS.BestActionInfo?> = engine.bestActionLD
    val isAnalyserRunningLD: LiveData<Boolean> = engine.isAnalyserRunningLD

    fun moveFromTo(fromFieldNumber: Int, fieldNumber: Int) {
        repository.movePieceFromTo(fromFieldNumber, fieldNumber)
        restartRunningAnalyser()
    }

    val game: MyGame
        get() = repository.game

    val showCongratulation: Boolean
        get() = repository.settings.showCongratulation

    fun touchGame() {
        repository.touchGame()
    }

    fun startNewGame() {
        repository.startNewGame()
        engine.restartRunningAnalyser(game.gameState)
    }

    fun countSeconds() {
        repository.countSeconds()
    }

    fun setStatusCongratulated() {
        repository.setStatusCongratulated()
    }

    fun toggleAnalyser() {
        engine.toggleAnalyser(game.gameState)
    }

    private fun restartRunningAnalyser() {
        engine.restartRunningAnalyser(game.gameState)
    }

    companion object {

        fun build(fragment: HomeFragment): HomeViewModel =
            ViewModelProvider(fragment.requireActivity()).get(HomeViewModel::class.java)

    }
}