package com.example.aston_intensiv_2

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.atan2
import kotlin.math.min
import kotlin.random.Random

class RainbowDrumView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val _currentColor = MutableStateFlow<String>("")
    var currentColor = _currentColor

    private var onDrumStoppedListener: OnDrumStoppedListener? = null


    private val colors = arrayOf(
        Color.RED, Color.parseColor("#FF7F00"),
        Color.YELLOW, Color.GREEN, Color.BLUE,
        Color.parseColor("#00BFFF"),
        Color.parseColor("#8B00FF")
    )

    private val colorsNames = arrayOf(
        "RED", "VIOLET", "BLUE LIGHT", "BLUE", "GREEN", "YELLOW", "ORANGE"
    )


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var currentRotation = 0f
    private var lastTouchAngle = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2f * 0.9f

        canvas.save()
        canvas.rotate(currentRotation, centerX, centerY)

        val sweepAngle = 360f / colors.size
        for (i in colors.indices) {
            paint.color = colors[i]
            canvas.drawArc(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius,
                i * sweepAngle, sweepAngle,
                true,
                paint
            )
        }

        canvas.restore()
    }


    private fun getTouchAngle(centerX: Float, centerY: Float, touchX: Float, touchY: Float): Float {
        val dx = touchX - centerX
        val dy = touchY - centerY
        return Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
    }

    fun rotateDrumToRandomSector(numberOfRotations: Int) {
        val sectorCount = colors.size
        val randomSector = Random.nextInt(0, sectorCount)

        val sweepAngle = 360f / sectorCount
        val targetAngle = randomSector * sweepAngle

        val finalAngle = numberOfRotations * 360 + targetAngle

        val animator = ValueAnimator.ofFloat(currentRotation % 360, finalAngle).apply {
            duration = 2000
            interpolator = android.view.animation.DecelerateInterpolator()
            addUpdateListener {
                currentRotation = it.animatedValue as Float
                invalidate()
                _currentColor.value = getCurrentColor()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}

                override fun onAnimationEnd(p0: Animator) {
                    onDrumStoppedListener?.onDrumStopped(getCurrentColor())
                }

                override fun onAnimationCancel(p0: Animator) {}

                override fun onAnimationRepeat(p0: Animator) {}
            })
        }
        animator.start()
    }



    fun getCurrentColor(): String {
        val sectorCount = colors.size
        val sweepAngle = 360f / sectorCount

        val normalizedRotation = (currentRotation % 360 + 360) % 360

        val adjustedRotation = (normalizedRotation + 90) % 360

        val sectorIndex = (adjustedRotation / sweepAngle).toInt() % sectorCount

        Log.d("getCurrentColor", "getCurrentColor: $sectorIndex, $adjustedRotation")

        return colorsNames[sectorIndex]
    }


    fun setOnDrumStoppedListener(listener: OnDrumStoppedListener) {
        onDrumStoppedListener = listener
    }




}
