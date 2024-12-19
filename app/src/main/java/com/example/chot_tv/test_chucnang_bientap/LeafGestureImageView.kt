package com.example.chot_tv.test_chucnang_bientap
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import com.example.chot_tv.zoomimg.views.GestureImageView

class LeafGestureImageView(context: Context, attrs: AttributeSet?) : GestureImageView(context, attrs) {
    private val clipPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createLeafPath(w, h)
    }

    private fun createLeafPath(w: Int, h: Int) {
        clipPath.reset()
        clipPath.moveTo(w / 2f, h * 0.1f)
        clipPath.lineTo(w * 0.2f, h * 0.8f)
        clipPath.lineTo(w * 0.8f, h * 0.8f)
        clipPath.close() // Đóng path để tạo ra hình đa giác lá
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        canvas.restore()
    }
}
