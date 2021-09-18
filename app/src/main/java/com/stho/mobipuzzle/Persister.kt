package com.stho.mobipuzzle

import android.content.Context
import android.content.Context.MODE_PRIVATE

class Persister(private val context: Context): IPersister {

    override fun loadRepository(): Repository {
        val preferences = context.getSharedPreferences(REPOSITORY, MODE_PRIVATE)
        val games = preferences.getInt(GAMES, 0)
        val points = preferences.getFloat(POINTS, 0f).toDouble()
        val mode = Mode.parseMode(preferences.getString(MODE, null))
        val face = Theme.parseTheme(preferences.getString(FACE, null))
        return Repository(games, points, Settings(mode, face))
    }

    override fun save(repository: Repository) {
        val preferences = context.getSharedPreferences(REPOSITORY, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(GAMES, repository.games)
        editor.putFloat(POINTS, repository.ratingPoints.toFloat())
        editor.putString(MODE, repository.settings.mode.toString())
        editor.putString(FACE, repository.settings.theme.toString())
        editor.apply()
    }

    companion object {
        private const val REPOSITORY = "REPOSITORY"
        private const val GAMES = "GAMES"
        private const val POINTS = "POINTS"
        private const val MODE = "MODE"
        private const val FACE = "FACE"
     }
}

