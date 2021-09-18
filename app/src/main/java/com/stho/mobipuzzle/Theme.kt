package com.stho.mobipuzzle

import java.lang.Exception

enum class Theme(name: String) {
    DEFAULT("Default"),
    WHITE("White"),
    GREEN("Green"),
    BORDEAUX("Bordeaux");

    override fun toString(): String {
        return name
    }

    companion object {

        private val defaultValue: Theme = Theme.DEFAULT

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
