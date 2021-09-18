package com.stho.mobipuzzle.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import com.stho.mobipuzzle.Game
import com.stho.mobipuzzle.R
import com.stho.mobipuzzle.Repository
import com.stho.mobipuzzle.getRepository


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = application.getRepository()

    val headlineLD: LiveData<String>
        get() = Transformations.map(repository.repositoryLD) {
            getApplication<Application>().resources.getQuantityString(R.plurals.label_headline_plurals,
                it.games,
                it.ratingPoints,
                it.games,
            )
        }

    val gameLD: LiveData<Game>
        get() = repository.gameLD

    companion object {

        fun build(fragment: DashboardFragment): DashboardViewModel =
            ViewModelProvider(fragment.requireActivity()).get(DashboardViewModel::class.java)
    }
}
