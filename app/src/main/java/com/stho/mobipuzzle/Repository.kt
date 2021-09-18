package com.stho.mobipuzzle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.mobipuzzle.ui.home.HomeViewModel

class Repository(games: Int, ratingPoints: Double, settings: Settings) {

    interface IData {
        val games: Int
        val ratingPoints: Double
    }

    private class Data(override val games: Int, override val ratingPoints: Double) : IData {
        fun increaseRatingBy(points: Double): Data =
            Data(games + 1, ratingPoints + points)

        fun reduceRatingBy(points: Double): Data =
            Data(games + 1, (ratingPoints - points).coerceAtLeast(0.0))

        companion object {
            val default: Data
                get() = Data(0, 0.0)
        }
    }

    private val gameLiveData = MutableLiveData<Game>().apply { value = Game() }
    private val repositoryLiveData: MutableLiveData<Data> = MutableLiveData<Data>().apply { value = Data(games, ratingPoints) }
    private val movesCounterLiveData = MutableLiveData<Int>().apply { value = 0 }
    private val secondsCounterLiveData = MutableLiveData<Long>().apply { value = 0 }
    private val summaryLiveData: MutableLiveData<Summary> = MutableLiveData()
    private val settingsLiveData: MutableLiveData<Settings> = MutableLiveData<Settings>().apply { value = settings }
    private var timeCounter: TimeCounter = TimeCounter()

    val repositoryLD: LiveData<IData>
        get() = Transformations.map(repositoryLiveData) { data -> data }

    val gameLD: LiveData<Game> = gameLiveData
    val settingsLD: LiveData<Settings> = settingsLiveData
    val summaryLD: LiveData<Summary> = summaryLiveData
    val movesCounterLD: LiveData<Int> = movesCounterLiveData
    val secondsCounterLD: LiveData<Long> = secondsCounterLiveData

    val game: Game
        get() = gameLiveData.value ?: Game()

    val games: Int
        get() = repositoryLiveData.value?.games ?: 0

    val ratingPoints: Double
        get() = repositoryLiveData.value?.ratingPoints ?: 0.0

    val settings: Settings
        get() = settingsLiveData.value ?: Settings()

    private fun countMove() {
        val moves = movesCounterLiveData.value ?: 0
        movesCounterLiveData.postValue(moves + 1)
    }

    fun countSeconds() {
        if (game.isAlive) {
            val seconds = timeCounter.getSeconds()
            if (seconds != secondsCounterLiveData.value) {
                secondsCounterLiveData.postValue(seconds)
            }
        } else {
            if (timeCounter.isAlive) {
                timeCounter.stop()
            }
        }
    }

    fun touchGame() {
        gameLiveData.postValue(game)
    }

    fun touchSettings() {
        settingsLiveData.postValue(settings)
    }

    fun movePieceTo(pieceNumber: Int, fieldNumber: Int): Boolean {
        val fromFieldNumber = game.getFieldNumberOf(pieceNumber)
        return movePieceFromTo(pieceNumber, fromFieldNumber, fieldNumber)
    }

    fun startNewGame() {
        if (game.isAlive) {
            registerGame(movesCounterLD.value ?: 0, secondsCounterLD.value ?: 0L, false)
        }
        game.shuffle(SHUFFLE_MOVES)
        touchGame()
        movesCounterLiveData.postValue(0)
        secondsCounterLiveData.postValue(0L)
    }

    fun setStatusCongratulated() {
        game.setStatusCongratulated()
        touchGame()
    }

    private fun movePieceFromTo(pieceNumber: Int, fromFieldNumber: Int, toFieldNumber: Int): Boolean =
        if (canMoveFromTo(fromFieldNumber, toFieldNumber)) {
            Log.d("MOVE", "Moving of $pieceNumber from $fromFieldNumber to $toFieldNumber ...")
            moveFromTo(fromFieldNumber, toFieldNumber)
            true
        } else {
            Log.d("MOVE", "Cannot move $pieceNumber from $fromFieldNumber to $toFieldNumber")
            false
        }

    private fun canMoveFromTo(fromFieldNumber: Int, toFieldNumber: Int): Boolean =
        game.canMoveTo(fromFieldNumber, toFieldNumber)


    private fun moveFromTo(fromFieldNumber: Int, toFieldNumber: Int) {
        if (game.moveTo(fromFieldNumber, toFieldNumber)) {
            countMove()
            gameLiveData.postValue(game)
            if (game.status == Status.FINISHED) {
                val moves = movesCounterLD.value ?: 0
                val seconds = secondsCounterLD.value ?: 0L
                registerGame(moves, seconds, true)
                summaryLiveData.postValue(Summary(moves, seconds, true))
            }
        }
    }

    private fun registerGame(moves: Int, seconds: Long, result: Boolean) {
        if (result) {
            val a: Double = SECONDS_THRESHOLD / (seconds + SECONDS_THRESHOLD)
            val b: Double = MOVES_THRESHOLD / (moves + MOVES_THRESHOLD)
            val points = a * b
            increaseRatingBy(points)
        } else {
            val points: Double = if (seconds < SECONDS_THRESHOLD) 0.5 else 1.0
            reduceRatingBy(points)
        }
    }

    private fun increaseRatingBy(points: Double) {
        val data = repositoryLiveData.value ?: Data.default
        repositoryLiveData.postValue(data.increaseRatingBy(points))
    }

    private fun reduceRatingBy(points: Double) {
        val data = repositoryLiveData.value ?: Data.default
        repositoryLiveData.postValue(data.reduceRatingBy(points))
    }

    companion object {

        private var singleton: Repository? = null
        private val lock: Any = Object()

        fun getInstance(persister: IPersister): Repository =
            singleton ?:
                synchronized(lock) {
                    singleton ?: persister.loadRepository().also {
                        singleton = it
                    }
                }

        private const val SECONDS_THRESHOLD: Double = 600.0 // 10 Minutes
        private const val MOVES_THRESHOLD: Double = 300.0
        private const val SHUFFLE_MOVES = 7777
    }
 }

