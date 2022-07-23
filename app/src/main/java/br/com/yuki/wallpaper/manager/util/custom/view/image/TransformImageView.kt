package br.com.yuki.wallpaper.manager.util.custom.view.image

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnEnd
import androidx.core.graphics.values
import androidx.core.view.doOnLayout
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.util.tools.TransformationUtil
import kotlin.math.sqrt

class TransformImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0
) : AppCompatImageView(context, attrs, defStyleInt) {

    enum class State {
        NONE, DRAG, ZOOM
    }

    private val pointer: PointF = PointF()
    private val pointerCenter: PointF = PointF()
    private var size: Size = Size(0, 0)

    private val currentImageMatrix: Matrix = Matrix()
    private val saveMatrix: Matrix = Matrix()

    private var state: State = State.NONE
    private var lastDistance: Float = 1f
    private var zoomImage: TransformationUtil.MatrixData = TransformationUtil.MatrixData(Matrix(), 1f)

    private var animation: ValueAnimator? = null

    private val gestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            if (animation == null)
                animation = animateCenter()
            return super.onDoubleTap(e)
        }
    })

    val zoom: Float
        get() {
            return currentImageMatrix.values()[Matrix.MSCALE_X] / zoomImage.baseScale
        }

    val point: PointF
        get() = currentImageMatrix.values().run {
            return@run PointF(this[Matrix.MTRANS_X] / width.toFloat(), this[Matrix.MTRANS_Y] / height.toFloat())
        }

    var info: Image.Info? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || animation != null)
            return false

        gestureDetector.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                saveMatrix.set(currentImageMatrix)
                pointer.set(event.x, event.y)
                state = State.DRAG
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> state = State.NONE
            MotionEvent.ACTION_POINTER_DOWN -> {
                lastDistance = distance(event)
                if (lastDistance > 5) {
                    saveMatrix.set(currentImageMatrix)
                    center(event)
                    state = State.ZOOM
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == State.DRAG) {
                    currentImageMatrix.set(saveMatrix)
                    currentImageMatrix.postTranslate(
                        event.x - pointer.x,
                        event.y - pointer.y,
                    )

                } else if (state == State.ZOOM) {
                    val distance = distance(event)
                    if (distance > 5) {
                        currentImageMatrix.set(saveMatrix)
                        val scale = distance / lastDistance
                        currentImageMatrix.postScale(scale, scale, pointerCenter.x, pointerCenter.y)
                    }
                }
            }
        }

        imageMatrix = currentImageMatrix
        return true
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        if (animation != null) {
            animation?.end()
            animation = null
        }

        this.size = drawable?.run { Size(intrinsicWidth, intrinsicHeight) } ?: return
        doOnLayout {
            val currentInfo = if (info == null) TransformationUtil.centerCrop(size, this)
            else TransformationUtil.makeMatrix(size, this.run { Size(width, height) }, info!!)

            zoomImage = currentInfo
            currentImageMatrix.set(currentInfo.matrix)
            imageMatrix = currentInfo.matrix
        }
    }

    private fun distance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)

        return sqrt(x * x + y * y)
    }

    private fun center(event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)

        pointerCenter.set(x / 2, y / 2)
    }

    private fun animateCenter(): ValueAnimator {
        val matrix = TransformationUtil.centerCrop(size, this).matrix.values()

        val currentMatrix = currentImageMatrix.values()
        val updateMatrix = currentImageMatrix.values()

        return ObjectAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener { animation ->
                val proportion = animation.animatedValue as Float

                updateMatrix[Matrix.MSCALE_X] = (matrix[Matrix.MSCALE_X] - currentMatrix[Matrix.MSCALE_X]) * proportion + currentMatrix[Matrix.MSCALE_X]
                updateMatrix[Matrix.MSCALE_Y] = (matrix[Matrix.MSCALE_Y] - currentMatrix[Matrix.MSCALE_Y]) * proportion + currentMatrix[Matrix.MSCALE_Y]
                updateMatrix[Matrix.MTRANS_X] = (matrix[Matrix.MTRANS_X] - currentMatrix[Matrix.MTRANS_X]) * proportion + currentMatrix[Matrix.MTRANS_X]
                updateMatrix[Matrix.MTRANS_Y] = (matrix[Matrix.MTRANS_Y] - currentMatrix[Matrix.MTRANS_Y]) * proportion + currentMatrix[Matrix.MTRANS_Y]

                currentImageMatrix.setValues(updateMatrix)
                imageMatrix = currentImageMatrix
            }
            doOnEnd {
                animation = null
            }

            duration = 300
            start()
        }
    }


}