package com.stho.mobipuzzle

import android.content.ClipData
import java.lang.Exception

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

        fun getPieceNumber(data: ClipData?): Int {
            try {
                data?.also {
                    if (PIECE == it.description.toString()) {
                        val text: String = it.getItemAt(0).text.toString()
                        return text.toInt()
                    }
                }
                return 0
            } catch (ex: Exception) {
                return 0
            }
        }

        private const val PIECE = "PIECE"
    }
}