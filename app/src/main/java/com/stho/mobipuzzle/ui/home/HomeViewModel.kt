package com.stho.mobipuzzle.ui.home

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.stho.mobipuzzle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var pieceNumber: Int = 0
    private val game: Game = Game()
    private var movesCounter: Int = 0
    private var startTimeMillis: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val repository: Repository = Dependencies.getRepository(application)


    private val gameLiveData = MutableLiveData<Game>().apply { value = game }
    private val movesCounterLiveData = MutableLiveData<Int>().apply { value = 0 }
    private val secondsCounterLiveData = MutableLiveData<Long>().apply { value = 0 }

    val headlineLD: LiveData<String>
        get() = Transformations.map(repository.repositoryLD) {
            getApplication<Application>().getString(R.string.label_headline_param,
                it.ratingPoints,
                it.games,
            )
        }

    val gameLD: LiveData<Game> = gameLiveData
    val movesCounterLD: LiveData<Int> = movesCounterLiveData
    val secondsCounterLD: LiveData<Long> = secondsCounterLiveData

    fun startDragging(pieceNumber: Int) {
        this.pieceNumber = pieceNumber
    }

    fun finishDragging() {
        this.pieceNumber = 0
    }

    val canStartDragging: Boolean
        get() = (this.pieceNumber == 0)

    fun isMove(fieldNumber: Int): Boolean {
        val from = game.getFieldNumberOf(pieceNumber)
        return fieldNumber != from
    }

    fun canMoveTo(fieldNumber: Int): Boolean {
        val from = game.getFieldNumberOf(pieceNumber)
        return game.canSwap(from, fieldNumber)
    }

    fun moveTo(fieldNumber: Int) {
        val from = game.getFieldNumberOf(pieceNumber)
        if (from > 0 && game.canSwap(from, fieldNumber)) {
            game.swap(from, fieldNumber)
            gameLiveData.postValue(game)
            countMove()
            if (game.isSolved) {
                stopSecondsCounter()
            }
        }
    }

    private fun countMove() {
        val moves = ++movesCounter
        if (moves != movesCounterLiveData.value) {
            movesCounterLiveData.postValue(moves)
        }
    }

    private fun countSeconds() {
        val seconds = (currentMillis - startTimeMillis) / MILLIS_PER_SECOND
        if (seconds != secondsCounterLiveData.value) {
            secondsCounterLiveData.postValue(seconds)
        }
    }

    private fun startSecondsCounter() {
        val runnableCode: Runnable = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.Default).launch {
                    countSeconds()
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnableCode, 200)
    }

    private fun stopSecondsCounter() {
        handler.removeCallbacksAndMessages(null)
    }

    fun touch() {
        gameLiveData.postValue(game)
    }

    fun startNew() {
        game.shuffleByMoves(10000)
        movesCounter = 0
        startTimeMillis = currentMillis
        gameLiveData.postValue(game)
        secondsCounterLiveData.postValue(0)
        movesCounterLiveData.postValue(0)
        startSecondsCounter()
    }

    companion object {
        fun build(fragment: HomeFragment): HomeViewModel {
            return ViewModelProvider(fragment.requireActivity()).get(HomeViewModel::class.java)
        }

        private val currentMillis: Long
            get() = System.nanoTime() / NANOS_PER_MILLIS

        private const val NANOS_PER_MILLIS: Long = 1000000L
        private const val MILLIS_PER_SECOND: Long = 1000L
    }
}