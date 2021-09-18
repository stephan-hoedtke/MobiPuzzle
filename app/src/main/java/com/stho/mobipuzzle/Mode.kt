package com.stho.mobipuzzle

import java.lang.Exception

enum class Mode(name: String) {
    NUMBERS("Numbers"),
    TEXT("Text");

    override fun toString(): String {
        return name
    }

    companion object {

        private val defaultValue: Mode = Mode.NUMBERS

        fun parseMode(modeString: String?, defaultValue: Mode = Mode.defaultValue): Mode {
            modeString?.also {
                try {
                    if (it.isNotBlank()) {
                        return valueOf(it)
                    }
                } catch (ex: Exception) {
                    // ignore
                }
            }
            return defaultValue
        }
    }
}
