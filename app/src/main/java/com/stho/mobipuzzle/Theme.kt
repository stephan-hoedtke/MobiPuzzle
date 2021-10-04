package com.stho.mobipuzzle

import java.lang.Exception

enum class Theme(private val text: String) {
    WHITE("White"),
    GREEN("Green"),
    BORDEAUX("Bordeaux");

    override fun toString(): String {
        return text
    }

    companion object {

        private val defaultValue: Theme = Theme.WHITE

        fun parseTheme(themeString: String?, defaultValue: Theme = Theme.defaultValue): Theme {
            themeString?.also {
                try {
                    if (it.isNotBlank()) {
                        return values().first { theme -> theme.text == themeString }
                    }
                } catch (ex: Exception) {
                    // ignore
                }
            }
            return defaultValue
        }
    }
}
