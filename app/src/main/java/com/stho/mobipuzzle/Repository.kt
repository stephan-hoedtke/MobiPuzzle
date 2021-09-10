package com.stho.mobipuzzle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class Repository(games: Int, ratingPoints: Double) {

    interface IData {
        val games: Int
        val ratingPoints: Double
    }

    private class Data(override val games: Int, override val ratingPoints: Double) : IData{
        fun increaseRatingBy(points: Double): Data =
            Data(games + 1, ratingPoints + points)

        fun reduceRatingBy(points: Double): Data =
            Data(games + 1, (ratingPoints - points).coerceAtLeast(0.0))

        companion object {
            val default: Data
                get() = Data(0, 0.0)
        }
    }

    private val repositoryLiveData: MutableLiveData<Data> = MutableLiveData<Data>().apply { value = Data(games, ratingPoints) }

    val repositoryLD: LiveData<IData>
        get() = Transformations.map(repositoryLiveData) { data -> data }

    val games: Int
       get() = repositoryLiveData.value?.games ?: 0

    val ratingPoints: Double
       get() = repositoryLiveData.value?.ratingPoints ?: 0.0

    fun registerGame(moves: Int, seconds: Long, result: Boolean) {
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
                    singleton ?: persister.load().also {
                        singleton = it
                    }
                }

        private const val SECONDS_THRESHOLD: Double = 600.0 // 10 Minutes
        private const val MOVES_THRESHOLD: Double = 300.0
    }
 }