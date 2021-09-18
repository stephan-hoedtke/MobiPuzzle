package com.stho.mobipuzzle

import android.view.View
import androidx.core.content.ContextCompat

class Helpers {
    companion object {
        fun toTimeString(seconds: Long): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds - (60 * minutes)
            return if (minutes > 60) {
                val hours = seconds / 60
                val remainingMinutes = minutes - (60 * hours)
                String.format("%02d:%02d:%02d", hours, remainingMinutes, remainingSeconds)
            } else {
                String.format("%02d:%02d", minutes, remainingSeconds)
            }
        }
    }
}

fun View.getColor(resId: Int): Int {
    return ContextCompat.getColor(this.context, resId)
}