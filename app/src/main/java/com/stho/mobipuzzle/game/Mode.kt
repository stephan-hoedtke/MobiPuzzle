package com.stho.mobipuzzle.game

import com.stho.mobipuzzle.Theme
import java.lang.Exception

enum class Mode(private val text: String) {
    NUMBERS("Numbers"),
    TEXT("Text");

    override fun toString(): String {
        return text
    }

    companion object {

        private val defaultValue: Mode = NUMBERS

        fun parseMode(modeString: String?, defaultValue: Mode = Companion.defaultValue): Mode {
            modeString?.also {
                try {
                    if (it.isNotBlank()) {
                        return values().first { mode -> mode.text == modeString }
                    }
                } catch (ex: Exception) {
                    // ignore
                }
            }
            return defaultValue
        }
    }
}
