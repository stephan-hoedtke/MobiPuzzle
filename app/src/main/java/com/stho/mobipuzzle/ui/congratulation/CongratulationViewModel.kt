package com.stho.mobipuzzle.ui.congratulation

import android.app.Application
import androidx.lifecycle.*
import com.stho.mobipuzzle.*
import com.stho.mobipuzzle.game.MyGame

class CongratulationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = application.getRepository()

    val headlineLD: LiveData<String>
        get() = Transformations.map(summaryLD) { summary ->
            getApplication<Application>().resources.getString(
                R.string.label_summary_params,
                summary.moves,
                Helpers.toTimeString(summary.seconds),
            )
        }

    val gameLD: LiveData<MyGame>
        get() = repository.gameLD

    val summaryLD: LiveData<Summary>
        get() = repository.summaryLD

    val settings: Settings
        get() = repository.settings

    fun startNewGame() {
        repository.startNewGame()
    }

    fun setStatusCongratulated() {
        repository.setStatusCongratulated()
    }

    companion object {

        fun build(fragment: CongratulationFragment): CongratulationViewModel =
            ViewModelProvider(fragment.requireActivity()).get(CongratulationViewModel::class.java)
    }
}
