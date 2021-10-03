package com.stho.mobipuzzle.engine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.mobipuzzle.game.MyGame
import com.stho.mobipuzzle.game.MyGameFabric
import com.stho.mobipuzzle.mcts.*
import kotlinx.coroutines.*


class Engine(private var scope: CoroutineScope) {
    private var shallContinue: ThreadSafeBoolean = ThreadSafeBoolean()
    private var isRunning: ThreadSafeBoolean = ThreadSafeBoolean()
    private val isRunningLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    private val rolloutPolicy = RandomSimulationPolicy()
    private val evaluationPolicy: IEvaluationPolicy = UTC()
    private val bestActionLiveData: MutableLiveData<MCTS.BestActionInfo?> = MutableLiveData<MCTS.BestActionInfo?>()
    private val mcts = MCTS(MyGameFabric(), rolloutPolicy, evaluationPolicy, MAX_DEPTH)
    private var job: Job? = null

    val bestActionLD: LiveData<MCTS.BestActionInfo?>
        get() = bestActionLiveData

    val isAnalyserRunningLD: LiveData<Boolean>
        get() = isRunningLiveData

    fun toggleAnalyser(state: IGameState) {
        scope.launch {
            if (isRunning.isSet) {
                stopRunning()
            } else {
                mcts.setup(state)
                start()
            }
        }
    }

    fun restartRunningAnalyser(state: IGameState) {
        scope.launch {
            if (isRunning.isSet) {
                stopRunning()
                mcts.setup(state)
                start()
            }
        }
    }

    private fun start() {
        shallContinue.set()
        run()
    }

    private suspend fun stopRunning() {
        // set the flag that the background job shall stop
        shallContinue.unset()

        // wait until it has stopped
        job?.join()
    }

    private fun run() {
        isRunning.set()
        isRunningLiveData.postValue(true)
        job = scope.launch(Dispatchers.IO) {
            runInBackground()
        }
    }

    private fun runInBackground() {
        updateBestAction(null)
        while (shallContinue.isSet) {
            for (j in 1..LOOP) {
                mcts.run()
            }
            updateBestAction(mcts.getBestActionInfo())
        }
        isRunning.unset()
        isRunningLiveData.postValue(false)
    }

    private fun updateBestAction(info: MCTS.BestActionInfo?) {
        info?.also {
            bestActionLiveData.postValue(info)
        }
    }

    companion object {
        private const val MAX_DEPTH = 400
        private const val LOOP = 100
    }
}


