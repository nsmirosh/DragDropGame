package apps.smoll.dragdropgame

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


const val IMAGEVIEW_TAG = "icon bitmap"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        generateShapesOnScreen()

        myImageView.setOnLongClickListener() { v: View ->
            val item = ClipData.Item(v.tag as? CharSequence)
            val dragData = ClipData(
                v.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            val myShadow = MyDragShadowBuilder(myImageView)
            val dragShadow = View.DragShadowBuilder(myImageView)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, dragShadow, null, 0);
            } else {
                v.startDrag(
                    dragData,
                    myShadow,
                    null,
                    0
                )
            }
        }


        val dragListen = View.OnDragListener { v, event ->

            // Handles each of the expected events
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                        (v as? ImageView)?.setColorFilter(Color.BLUE)

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate()

                        // returns true to indicate that the View can accept the dragged data.
                        true
                    } else {
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Applies a green tint to the View. Return true; the return value is ignored.
                    (v as? ImageView)?.setColorFilter(Color.GREEN)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION ->
                    // Ignore the event
                    true
                DragEvent.ACTION_DRAG_EXITED -> {
                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    (v as? ImageView)?.setColorFilter(Color.BLUE)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    // Gets the item containing the dragged data
                    val item: ClipData.Item = event.clipData.getItemAt(0)

                    myImageView.x = event.x
                    myImageView.y = event.y

                    // Gets the text data from the item.
                    val dragData = item.text

                    // Displays a message containing the dragged data.
                    Toast.makeText(this, "Dragged data is  + $dragData", Toast.LENGTH_LONG).show()

                    // Turns off any color tints
                    (v as? ImageView)?.clearColorFilter()

                    // Invalidates the view to force a redraw
                    v.invalidate()

                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Turns off any color tinting
                    (v as? ImageView)?.clearColorFilter()

                    // Invalidates the view to force a redraw
                    v.invalidate()

                    // Does a getResult(), and displays what happened.
                    when (event.result) {
                        true ->
                            Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG)
                        else ->
                            Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG)
                    }.show()

                    // returns true; the value is ignored.
                    true
                }
                else -> {
                    // An unknown action type was received.
                    false
                }
            }
        }
        containerView.setOnDragListener(dragListen)
    }


    private fun generateShapesOnScreen() {
        val imageShapeSet = setOf(
            R.drawable.ic_square,
            R.drawable.ic_hexagonal,
            R.drawable.ic_star,
            R.drawable.ic_circle
        )

        var startX = 100f
        var startY = 100f

        for (shape in imageShapeSet) {
            val imageView = ImageView(this)

            imageView.apply {
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity, shape))

                id = View.generateViewId();
                containerView.addView(this)

                layoutParams.height = 150;
                layoutParams.width = 150;
                this.requestLayout();
                x = startX
                y = startY

                startX += 200
                startY += 200
            }


            val set = ConstraintSet()
            set.clone(containerView);
            // connect start and end point of views, in this case top of child to top of parent.
            set.connect(
                imageView.getId(),
                ConstraintSet.TOP,
                containerView.getId(),
                ConstraintSet.TOP,
                60
            );
            // ... similarly add other constraints
            set.applyTo(containerView);
        }


    }
}