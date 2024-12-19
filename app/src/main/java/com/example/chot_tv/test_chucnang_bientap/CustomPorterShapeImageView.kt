package com.example.chot_tv.test_chucnang_bientap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.chot_tv.shapeimageview.mask.PorterShapeImageView


class CustomPorterShapeImageView : PorterShapeImageView {
    private var imageBitmap: Bitmap? = null
    private var paint: Paint? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        // Đặt thuộc tính nếu cần
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Vẽ hình ảnh lên khung
        if (imageBitmap != null) {
            canvas.drawBitmap(imageBitmap!!, 0f, 0f, paint)
        }
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        this.imageBitmap = bitmap
        invalidate() // Yêu cầu vẽ lại
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Xử lý sự kiện chạm cho di chuyển hình ảnh
        // Nếu bạn muốn chặn sự kiện chạm, trả về false
        return true // Hoặc thực hiện logic di chuyển ở đây
    }
}
