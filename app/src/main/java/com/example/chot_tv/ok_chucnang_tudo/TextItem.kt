package com.example.chot_tv.ok_chucnang_tudo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class TextItem(var text: String,var isFlipped: Boolean = false) : BaseItem() {

    override  var drawable: Drawable = createDrawableFromText(text)

    init {
        // Tạo drawable từ văn bản khi khởi tạo TextItem
        drawable = createDrawableFromText(text)
    }

     fun createDrawableFromText(text: String): Drawable {
        // Tạo Paint để vẽ văn bản
        val paint = Paint().apply {
            color = Color.BLACK // Màu văn bản
            textSize = 200f // Kích thước chữ
            isAntiAlias = true // Khử răng cưa
        }

        // Đo kích thước của văn bản để xác định kích thước bitmap
        val textWidth = paint.measureText(text).toInt()
        val textHeight = (paint.descent() - paint.ascent()).toInt()

        // Thêm padding
         val paddingHorizontal = 20 // Padding ngang
         val paddingVertical = 60 // Padding dọc

        // Tạo bitmap với kích thước chứa văn bản và padding
        val bitmap = Bitmap.createBitmap(
            textWidth + paddingHorizontal * 2, // Thêm padding trái và phải
            textHeight + paddingVertical * 2, // Thêm padding trên và dưới
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // Vẽ văn bản lên bitmap, dịch chuyển theo padding
        canvas.drawText(text, paddingHorizontal.toFloat(), -paint.ascent() + paddingVertical, paint)

        // Tạo drawable từ bitmap
        return BitmapDrawable(bitmap)
    }


    override fun draw(canvas: Canvas, matrix: Matrix, paint: Paint) {
        // Vẽ drawable lên canvas
        canvas.save()
        canvas.concat(matrix)
        val drawableBitmap = drawable as? BitmapDrawable
        drawableBitmap?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            if (isFlipped) {

                canvas.scale(-1f, 1f, (it.intrinsicWidth / 2).toFloat(), (it.intrinsicHeight / 2).toFloat())
            }
            it.draw(canvas)
        }
        canvas.restore()
    }
}
