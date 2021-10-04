package com.stho.mobipuzzle.engine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.mobipuzzle.game.MyGameFabric
import com.stho.mobipuzzle.mcts.*
import com.stho.mobipuzzle.mcts.policies.RandomSimulationPolicy
import com.stho.mobipuzzle.mcts.policies.UTC
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

        updateBestAction(null)

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
        runLoop()
        isRunning.unset()
        isRunningLiveData.postValue(false)
    }

    private fun runLoop() {
        while (true) {
            for (i in 0 until LOOP) {
                if (isStopped) {
                    return
                }
                mcts.run()
            }
            updateBestAction(mcts.getBestActionInfo())
        }
    }

    private val isStopped: Boolean
        get() = shallContinue.isUnset

    private fun updateBestAction(info: MCTS.BestActionInfo?) {
        bestActionLiveData.postValue(info)
    }

    companion object {
        private const val MAX_DEPTH = 500
        private const val LOOP = 100
    }
}


