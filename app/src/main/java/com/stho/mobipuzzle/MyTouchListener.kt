package com.stho.mobipuzzle

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.stho.mobipuzzle.ui.home.HomeViewModel


class MyTouchListener(private val pieceNumber: Int, private val viewModel: HomeViewModel) : View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("DRAG", "Start moving $pieceNumber ...")
                val data = Helpers.createClipData(pieceNumber)
                val shadowBuilder = View.DragShadowBuilder(view)
                view.startDragAndDrop(data, shadowBuilder, view, 0)
                val piece: TextView = view as TextView
                piece.setBackgroundColor(piece.getColor(R.color.secondaryDarkColor))
                piece.setTextColor(piece.getColor(R.color.backgroundColor))
                piece.invalidate()
                true
            }
            else -> {
                //view.visibility = View.VISIBLE
                false
            }
        }
    }
}

