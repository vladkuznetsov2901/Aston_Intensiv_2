package com.example.aston_intensiv_2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text: String = ""
    private var currentColor = Color.BLACK
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = currentColor
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    fun setText(value: String) {
        text = value
        invalidate()
    }

    fun setColor(newColor: Int) {
        currentColor = newColor
        paint.color = currentColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(text, width / 2f, height / 2f, paint)
    }
}
