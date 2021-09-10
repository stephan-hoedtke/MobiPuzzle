package com.stho.mobipuzzle

import android.content.ClipData
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

class Helpers {
    companion object {
        fun toTimeString(seconds: Long): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds - (60 * minutes)
            return String.format("%02d:%02d", minutes, remainingSeconds)
        }

        fun createClipData(pieceNumber: Int): ClipData {
            return ClipData.newPlainText(PIECE, pieceNumber.toString())
        }

        fun getPieceNumber(piece: TextView): Int {
            return try {
                piece.text.toString().toInt()
            } catch (ex: Exception) {
                0
            }
        }

        private const val PIECE = "PIECE"
    }
}

fun View.getColor(resId: Int): Int {
    return ContextCompat.getColor(this.context, resId)
}