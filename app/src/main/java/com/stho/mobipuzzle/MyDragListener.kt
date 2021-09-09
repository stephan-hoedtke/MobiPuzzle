package com.stho.mobipuzzle

import android.graphics.Color
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.stho.mobipuzzle.ui.home.HomeViewModel

/**
 *
 * https://www.raywenderlich.com/24508555-android-drag-and-drop-tutorial-moving-views-and-data
 * https://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
 */
class MyDragListener(private val fieldNumber: Int, private val viewModel: HomeViewModel) : View.OnDragListener {

    override fun onDrag(view: View, event: DragEvent): Boolean {

//    val enterShape: Drawable = getResources().getDrawable(R.drawable.shape_droptarget);
//    Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        return when (event.action) {

            DragEvent.ACTION_DRAG_LOCATION -> {
                // do nothing ??
                true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                //val pieceNumber = Helpers.getPieceNumber(event.clipData)
                //return (pieceNumber > 0)
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                if (viewModel.isMove(fieldNumber)) {
                    if (viewModel.canMoveTo(fieldNumber)) {
                        markAsTarget(view)
                    } else {
                        markAsInvalidTarget(view)
                    }
                } else {
                    markAsNormal(view)
                }
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                markAsNormal(view)
                true
            }
            DragEvent.ACTION_DROP -> {
                viewModel.moveTo(fieldNumber)
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                markAsNormal(view)
                viewModel.finishDragging()
                viewModel.touch()
                true
            }
            else -> {
                // nothing
                false
            }
        }
    }

    private fun movePieceToTargetView(view: View, event: DragEvent) {
        val piece: View = event.localState as View
        val target: FrameLayout = view as FrameLayout
        movePieceToTargetView(piece, target)
    }

    private fun movePieceToTargetView(piece: View, target: FrameLayout) {
        val owner: ViewGroup = piece.parent as ViewGroup
        owner.removeView(piece)
        target.addView(piece)
        piece.visibility = View.VISIBLE
    }

    private fun markAsNormal(view: View) {
        view.setBackgroundColor(Color.TRANSPARENT)
        view.visibility = View.VISIBLE
    }

    private fun markAsTarget(view: View) {
        view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.secondaryColor))
        view.visibility = View.VISIBLE
    }

    private fun markAsInvalidTarget(view: View) {
        view.setBackgroundColor(Color.TRANSPARENT)
        view.visibility = View.VISIBLE
    }
}
