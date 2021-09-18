package com.stho.mobipuzzle

import android.os.Parcel
import android.os.Parcelable
import java.lang.Exception

class Summary(val moves: Int, val seconds: Long, val success: Boolean) {

    fun serialize(): String =
        "$moves:$seconds:$success"

    companion object {

        private val defaultValue: Summary =
            Summary(0, 0L, false)

        fun parseSummary(summaryString: String?, defaultValue: Summary = Summary.defaultValue): Summary {
            summaryString?.also {
                try {
                    val token = it.split(":")
                    if (token.size > 2) {
                        val moves: Int = token[0].toInt()
                        val seconds: Long = token[1].toLong()
                        val success: Boolean = token[2].toBoolean()
                        return Summary(moves, seconds, success)
                    }
                } catch(ex: Exception) {
                    // ignore
                }
            }
            return defaultValue
        }
    }

}