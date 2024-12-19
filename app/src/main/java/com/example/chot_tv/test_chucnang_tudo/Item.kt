package com.example.chot_tv.test_chucnang_tudo

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class Item(
    override val drawable: Drawable,
    var isFlipped: Boolean = false,
    var borderWidth: Float = 6f, // Độ dày viền, mặc định là 6dp
    var borderColor: Int = 0xFFE7CFE7.toInt() // Màu viền, mặc định là màu đen
) : BaseItem() {

    override fun draw(canvas: Canvas, matrix: Matrix, paint: Paint) {
        canvas.save()
        canvas.concat(matrix)

        val drawableBitmap = drawable as? BitmapDrawable
        drawableBitmap?.let {
            // Tạo Rect cho drawable
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)

            // Vẽ viền
            val borderPaint = Paint().apply {
                color = borderColor
                style = Paint.Style.STROKE
                strokeWidth = borderWidth
            }

            // Vẽ hình chữ nhật viền
            val borderRect = RectF(
                0f,
                0f,
                it.intrinsicWidth.toFloat(),
                it.intrinsicHeight.toFloat()
            )
            canvas.drawRect(borderRect, borderPaint)

            if (isFlipped) {
                // Nếu ảnh bị lật, lật theo chiều ngang
                canvas.scale(-1f, 1f, (it.intrinsicWidth / 2).toFloat(), (it.intrinsicHeight / 2).toFloat())
            }
            it.draw(canvas)
        }

        canvas.restore()
    }
}