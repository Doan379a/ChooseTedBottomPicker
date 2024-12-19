package com.example.chot_tv.test_chucnang_bientap
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.chot_tv.zoomimg.views.GestureImageView

class CircleGestureImageView(context: Context, attrs: AttributeSet?) : GestureImageView(context, attrs) {
    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f // Độ dày viền ban đầu
    }
    private val clipPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createCirclePath(w, h)
    }

    private fun createCirclePath(w: Int, h: Int) {
        val radius = Math.min(w, h) / 2f
        clipPath.reset()
        clipPath.addCircle(w / 2f, h / 2f, radius, Path.Direction.CW)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        // Apply clip path to restrict drawing area
        canvas.clipPath(clipPath)

        super.onDraw(canvas)

        // Vẽ viền
        canvas.drawPath(clipPath, borderPaint)

        canvas.restore()
    }
}
