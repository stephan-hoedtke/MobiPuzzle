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

        fun parseTheme(colorString: String?, defaultValue: Theme = Theme.defaultValue): Theme {
            colorString?.also {
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
