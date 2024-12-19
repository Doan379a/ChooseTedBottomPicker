package com.example.chot_tv.test_chucnang_tudo

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class Item_frame(override val drawable: Drawable) : BaseItem(){
    fun setPosition(x: Float, y: Float) {
        state.set(x, y, state.zoom, state.rotation)
    }

    override fun draw(canvas: Canvas, matrix: Matrix, paint: Paint) {
        canvas.save()
        canvas.concat(matrix)

        val drawableBitmap = drawable as? BitmapDrawable
        drawableBitmap?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            it.draw(canvas)
        }

        canvas.restore()
    }
}