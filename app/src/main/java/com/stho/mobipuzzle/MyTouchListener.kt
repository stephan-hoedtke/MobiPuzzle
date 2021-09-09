package com.stho.mobipuzzle

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.stho.mobipuzzle.ui.home.HomeViewModel
import org.w3c.dom.Text
import kotlin.coroutines.coroutineContext


class MyTouchListener(private val pieceNumber: Int, private val viewModel: HomeViewModel) : View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                if (viewModel.canStartDragging) {
                    viewModel.startDragging(pieceNumber)
                    val data = Helpers.createClipData(pieceNumber)
                    val shadowBuilder = View.DragShadowBuilder(view)
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                    val textView: TextView = view as TextView
                    textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.secondaryDarkColor))
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.backgroundColor))

                }
                true
            }
            else -> {
                //view.visibility = View.VISIBLE
                false
            }
        }
    }
}

