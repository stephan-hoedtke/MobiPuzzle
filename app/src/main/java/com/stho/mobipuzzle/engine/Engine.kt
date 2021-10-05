package com.stho.mobipuzzle.engine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stho.mobipuzzle.game.MyGameFabric
import com.stho.mobipuzzle.mcts.*
import com.stho.mobipuzzle.mcts.policies.*
import kotlinx.coroutines.*


class Engine(private var scope: CoroutineScope) {
    private var shallContinue: ThreadSafeBoolean = ThreadSafeBoolean()
    private var isRunning: ThreadSafeBoolean = ThreadSafeBoolean()
    private val isRunningLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    private val rolloutPolicy: ISimulationPolicy = BestEvaluationSimulationPolicy()
    private val evaluationPolicy: IEvaluationPolicy = UTC(C)
    private val propagationPolicy: IPropagationPolicy = MaxValuePropagationPolicy()
    private val bestActionLiveData: MutableLiveData<MCTS.BestActionInfo?> = MutableLiveData<MCTS.BestActionInfo?>().apply { value = null }
    private val mcts = MCTS(MyGameFabric(), rolloutPolicy, evaluationPolicy, propagationPolicy, MAX_DEPTH)
    private var job1: Job? = null
    private var job2: Job? = null

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

        // clear best action info
        updateBestAction(null)

        // set the flag that the background job shall stop
        shallContinue.unset()

        // wait until it has stopped
        job1?.join()
        job2?.join()
    }

    private fun run() {
        isRunning.set()
        isRunningLiveData.postValue(true)
        job1 = scope.launch(Dispatchers.IO) {
            runInBackground()
        }
        job2 = scope.launch(Dispatchers.IO) {
            updateInBackground()
        }
    }

    private suspend fun runInBackground() {
        updateBestAction(null)
        runLoop()
        isRunning.unset()
        isRunningLiveData.postValue(false)
    }

    private suspend fun runLoop() {
        while (isNotStopped) {
            mcts.run()
        }
    }

    private suspend fun updateInBackground() {
        updateBestAction(null)
        delay(300L)
        while (isNotStopped) {
            updateBestAction(mcts.getBestActionInfo())
            delay(1000L) // 1/2 second
        }
    }

    private val isNotStopped: Boolean
        get() = shallContinue.isSet

    private fun updateBestAction(info: MCTS.BestActionInfo?) {
        if (!areEqual(bestActionLiveData.value, info)) {
            bestActionLiveData.postValue(info)
        }
    }

    private fun areEqual(a: MCTS.BestActionInfo?, b: MCTS.BestActionInfo?): Boolean =
        (a == null && b == null) || (a != null && b != null && a.isEqualTo(b))

    companion object {
        private const val MAX_DEPTH = 200
        private const val C = 0.2
     }
}


