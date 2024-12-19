package com.example.chot_tv.test_chucnang_bientap
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import com.example.chot_tv.zoomimg.views.GestureImageView

class StarGestureImageView(context: Context, attrs: AttributeSet?) : GestureImageView(context, attrs) {
    private val clipPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createStarPath(w, h)
    }

    private fun createStarPath(w: Int, h: Int) {
        val midX = w / 2f
        val midY = h / 2f
        val radius = Math.min(w, h) / 2f

        // Reset path and create a star-shaped path
        clipPath.reset()
        val points = 5 // Số đỉnh của ngôi sao
        val angle = (2.0 * Math.PI / points).toFloat()

        for (i in 0 until points) {
            val outerX = midX + radius * Math.cos((angle * i).toDouble()).toFloat()
            val outerY = midY + radius * Math.sin((angle * i).toDouble()).toFloat()

            val innerRadius = radius / 2.5f // Kích thước phần giữa của ngôi sao
            val innerX = midX + innerRadius * Math.cos((angle * i + angle / 2).toDouble()).toFloat()
            val innerY = midY + innerRadius * Math.sin((angle * i + angle / 2).toDouble()).toFloat()

            if (i == 0) {
                clipPath.moveTo(outerX, outerY)
            } else {
                clipPath.lineTo(outerX, outerY)
            }
            clipPath.lineTo(innerX, innerY)
        }

        clipPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        canvas.restore()
    }
}
