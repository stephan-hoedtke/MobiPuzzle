package com.stho.mobipuzzle

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.stho.mobipuzzle.game.Mode

class Persister(private val context: Context): IPersister {

    override fun loadRepository(): Repository {
        val preferences = context.getSharedPreferences(REPOSITORY, MODE_PRIVATE)
        val games = preferences.getInt(GAMES, 0)
        val points = preferences.getFloat(POINTS, 0f).toDouble()
        val mode = Mode.parseMode(preferences.getString(MODE, null))
        val face = Theme.parseTheme(preferences.getString(FACE, null))
        val showCongratulations = preferences.getBoolean(CONGRATULATIONS, true)
        val showEngineDetails = preferences.getBoolean(ENGINE_DETAILS, false)
        val showBestAction = preferences.getBoolean(BEST_ACTION, false)
        return Repository(games, points, Settings(mode, face, showCongratulations, showEngineDetails, showBestAction))
    }

    override fun save(repository: Repository) {
        val preferences = context.getSharedPreferences(REPOSITORY, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(GAMES, repository.games)
        editor.putFloat(POINTS, repository.ratingPoints.toFloat())
        editor.putString(MODE, repository.settings.mode.toString())
        editor.putString(FACE, repository.settings.theme.toString())
        editor.putBoolean(CONGRATULATIONS, repository.settings.showCongratulation)
        editor.putBoolean(ENGINE_DETAILS, repository.settings.showEngineDetails)
        editor.putBoolean(BEST_ACTION, repository.settings.showBestAction)
        editor.apply()
    }

    companion object {
        private const val REPOSITORY = "REPOSITORY"
        private const val GAMES = "GAMES"
        private const val POINTS = "POINTS"
        private const val MODE = "MODE"
        private const val FACE = "FACE"
        private const val CONGRATULATIONS = "CONGRATULATIONS"
        private const val ENGINE_DETAILS = "ENGINE_DETAILS"
        private const val BEST_ACTION = "BEST_ACTION"
     }
}

