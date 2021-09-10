package com.stho.mobipuzzle

import android.graphics.Color
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import com.stho.mobipuzzle.ui.home.HomeViewModel

/**
 *
 * https://www.raywenderlich.com/24508555-android-drag-and-drop-tutorial-moving-views-and-data
 * https://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
 */
class MyDragListener(private val fieldNumber: Int, private val viewModel: HomeViewModel) : View.OnDragListener {

    override fun onDrag(view: View, event: DragEvent): Boolean {

        when (event.action) {

            DragEvent.ACTION_DRAG_LOCATION -> {
                return true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                val pieceNumber = Helpers.getPieceNumber(event.localState as TextView)
                if (pieceNumber > 0) {
                    if (viewModel.canMoveTo(pieceNumber, fieldNumber)) {
                        Log.d("DRAG", "Entering target $fieldNumber")
                        markAsTarget(view)
                    } else {
                        Log.d("DRAG", "Entering $fieldNumber (invalid)")
                        markAsInvalidTarget(view)
                    }
                    return true
                }
                return false
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("DRAG", "Exiting $fieldNumber")
                markAsNormal(view)
                return true
            }
            DragEvent.ACTION_DROP -> {
                val pieceNumber = Helpers.getPieceNumber(event.localState as TextView)
                if (pieceNumber > 0) {
                    Log.d("DRAG", "Drop into $fieldNumber")
                    return viewModel.moveTo(pieceNumber, fieldNumber)
                }
                return false
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                markAsNormal(view)
                Log.d("DRAG", "Drag ended in $fieldNumber with result ${event.result}")
                viewModel.touch()
                val piece = event.localState as TextView
                piece.setBackgroundColor(piece.getColor(R.color.fieldColor))
                piece.setTextColor(piece.getColor(R.color.fieldTextColor))
                piece.invalidate()
                return true
            }
            else -> {
                // nothing
                Log.d("DRAG", "Unknown for $fieldNumber")
                return false
            }
        }
    }

    private fun markAsNormal(view: View) {
        view.setBackgroundColor(Color.TRANSPARENT)
        view.visibility = View.VISIBLE
        view.invalidate()
    }

    private fun markAsTarget(view: View) {
        view.setBackgroundColor(view.getColor(R.color.secondaryColor))
        view.visibility = View.VISIBLE
        view.invalidate()
    }

    private fun markAsInvalidTarget(view: View) {
        view.setBackgroundColor(Color.TRANSPARENT)
        view.visibility = View.VISIBLE
        view.invalidate()
    }
}
