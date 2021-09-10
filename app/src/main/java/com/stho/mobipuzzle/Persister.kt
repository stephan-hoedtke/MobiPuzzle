package com.stho.mobipuzzle

import android.content.Context
import android.content.Context.MODE_PRIVATE

class Persister(private val context: Context): IPersister {

    override fun load(): Repository {
        val preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        val games = preferences.getInt(GAMES, 0)
        val points = preferences.getFloat(POINTS, 0f).toDouble()
        return Repository(games, points)
    }

    override fun save(repository: Repository) {
        val preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(GAMES, repository.games)
        editor.putFloat(POINTS, repository.ratingPoints.toFloat())
        editor.apply()
    }

    companion object {
        private const val PREFERENCES = "MAIN"
        private const val GAMES = "GAMES"
        private const val POINTS = "POINTS"
    }
}