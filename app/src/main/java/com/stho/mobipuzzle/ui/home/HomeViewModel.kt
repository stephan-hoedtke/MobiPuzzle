package com.stho.mobipuzzle.ui.home

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.stho.mobipuzzle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val game: Game = Game()

    private var started: Boolean = false
    private var movesCounter: Int = 0
    private var startTimeMillis: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val repository: Repository = application.getRepository()

    private val gameLiveData = MutableLiveData<Game>().apply { value = game }
    private val movesCounterLiveData = MutableLiveData<Int>().apply { value = 0 }
    private val secondsCounterLiveData = MutableLiveData<Long>().apply { value = 0 }

    val gameLD: LiveData<Game> = gameLiveData
    val movesCounterLD: LiveData<Int> = movesCounterLiveData
    val secondsCounterLD: LiveData<Long> = secondsCounterLiveData

    fun canMoveTo(pieceNumber: Int, fieldNumber: Int): Boolean {
        val from = game.getFieldNumberOf(pieceNumber)
        return canMoveFromTo(from, fieldNumber)
    }

    private fun canMoveFromTo(from: Int, to: Int): Boolean =
        game.canMoveTo(from, to)

    fun moveTo(pieceNumber: Int, fieldNumber: Int): Boolean {
        val from = game.getFieldNumberOf(pieceNumber)
        return moveTo(pieceNumber, from, fieldNumber)
    }

    private fun moveTo(pieceNumber: Int, from: Int, to: Int): Boolean =
        if (canMoveFromTo(from, to)) {
            Log.d("MOVE", "Moving of $pieceNumber from $from to $to ...")
            moveFromTo(from, to)
            true
        } else {
            Log.d("MOVE", "Cannot move $pieceNumber from $from to $to")
            false
        }

    private fun moveFromTo(from: Int, to: Int) {
        game.moveTo(from, to)
        gameLiveData.postValue(game)
        countMove()
        if (game.isSolved) {
            if (started) {
                repository.registerGame(
                    movesCounterLD.value ?: 0,
                    seconds = secondsCounterLD.value ?: 0L,
                    true
                )
                stopSecondsCounter()
                started = false
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
        if (!game.isSolved) {
            repository.registerGame(movesCounterLD.value ?: 0, secondsCounterLD.value ?: 0L, false)
        }
        game.shuffle(1000)
        movesCounter = 0
        startTimeMillis = currentMillis
        gameLiveData.postValue(game)
        secondsCounterLiveData.postValue(0)
        movesCounterLiveData.postValue(0)
        startSecondsCounter()
        started = true
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