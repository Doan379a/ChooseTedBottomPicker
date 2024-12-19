package com.example.chot_tv.test_chucnang_tudo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.chot_tv.R
import com.example.chot_tv.zoomimg.GestureController
import com.example.chot_tv.zoomimg.Settings
import com.example.chot_tv.zoomimg.State
import com.example.chot_tv.zoomimg.views.interfaces.GestureView

class Demoo @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs), GestureView {

    companion object {
        private const val BORDER_WIDTH = 2f
    }

    private val items: MutableList<Item> = ArrayList()
    private val controller: GestureController = GestureController(this)
    private val matrix: Matrix = Matrix()
    private val inverseMatrix: Matrix = Matrix()
    private val paint: Paint = Paint()
    private var selected: Item? = null
    private val point = FloatArray(2)
    private var deleteIcon: Bitmap? = null
    private var flipIcon: Bitmap? = null

    init {
        init()
    }

    private fun init() {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        val itemSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics
        ).toInt()

        controller.settings
            .setRotationEnabled(true)
            .setDoubleTapEnabled(false)
            .setFitMethod(Settings.Fit.INSIDE)
            .setBoundsType(Settings.Bounds.INSIDE)
            .setOverscrollDistance( 1000f, 1000f)
            .setMinZoom(0.3f)
            .setMaxZoom(3f)
            .setImage(itemSize, itemSize)
        deleteIcon = VectorDrawableCompat.create(resources, R.drawable.baseline_delete_24, null)?.let {
            Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888).apply {
                val canvas = Canvas(this)
                it.setBounds(0, 0, canvas.width, canvas.height)
                it.draw(canvas)
            }
        }

        flipIcon = VectorDrawableCompat.create(resources, R.drawable.baseline_flip_24, null)?.let {
            Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888).apply {
                val canvas = Canvas(this)
                it.setBounds(0, 0, canvas.width, canvas.height)
                it.draw(canvas)
            }
        }
        controller.addOnStateChangeListener(object : GestureController.OnStateChangeListener {
            override fun onStateChanged(state: State) {
                applyState(state)
            }

            override fun onStateReset(oldState: State, newState: State) {
                applyState(newState)
            }
        })

        controller.setOnGesturesListener(object : GestureController.SimpleOnGestureListener() {
            override fun onSingleTapUp(event: MotionEvent): Boolean {
                selectItem(event.x, event.y)
                return true
            }
        })
    }

    override fun getController(): GestureController = controller

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        controller.settings.setViewport(
            width - paddingLeft - paddingRight,
            height - paddingTop - paddingBottom
        )
        controller.updateState()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return controller.onTouch(this, event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        // Vẽ tất cả các item
        for (item in items) {
            item.state.get(matrix) // Lấy ma trận (vị trí, zoom, xoay) của item
            canvas.save()
            canvas.concat(matrix) // Áp dụng ma trận cho canvas

            val drawable = item.drawable as? BitmapDrawable
            drawable?.let {
                it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)

                // Kiểm tra xem ảnh có được lật không
                if (item.isFlipped) {
                    // Nếu ảnh đã lật, lật theo trục X
                    canvas.scale(-1f, 1f, (it.intrinsicWidth / 2).toFloat(), (it.intrinsicHeight / 2).toFloat())
                }
                it.draw(canvas)
            }

            canvas.restore()
        }

        // Vẽ viền item đã chọn
        selected?.let { selectedItem ->
            paint.strokeWidth = 6f / controller.state.zoom
            paint.color = Color.RED
            canvas.save()
            controller.state.get(matrix)
            canvas.concat(matrix)

            val selectedDrawable = selectedItem.drawable as? BitmapDrawable
            selectedDrawable?.let {
                val bounds = it.bounds
                canvas.drawRect(bounds, paint)
                val iconSize = 100
                // Vẽ icon dấu "X" (delete) ở góc trên bên trái
                deleteIcon?.let { icon ->
                    canvas.drawBitmap(
                        Bitmap.createScaledBitmap(icon, iconSize, iconSize, false),
                        bounds.left.toFloat() - iconSize / 2,
                        bounds.top.toFloat() - iconSize / 2,
                        null
                    )
                }

                // Vẽ icon lật ảnh ở góc trên bên phải
                flipIcon?.let { icon ->
                    canvas.drawBitmap(
                        Bitmap.createScaledBitmap(icon, iconSize, iconSize, false),
                        bounds.right - iconSize.toFloat() / 2,
                        bounds.top.toFloat() - iconSize / 2,
                        null
                    )
                }
            }

            canvas.restore()
        }

        canvas.restore()
    }


    fun setItems(newItems: List<Item>) {
        items.clear() // Xóa danh sách cũ để tránh việc trùng lặp ảnh
        items.addAll(newItems) // Cập nhật danh sách với các ảnh mới
        selected = null // Đảm bảo không còn item nào được chọn sau khi cập nhật
        invalidate() // Gọi invalidate một lần để vẽ lại toàn bộ
    }

    private fun applyState(state: State) {
        selected?.state?.set(state)
        invalidate()
    }

    private fun selectItem(eventX: Float, eventY: Float) {
        for (i in items.indices.reversed()) {
            val item = items[i]
            item.state.get(matrix)
            matrix.invert(inverseMatrix)

            // Biến đổi vị trí chạm
            point[0] = eventX
            point[1] = eventY
            inverseMatrix.mapPoints(point)
            val newX = point[0].toInt()
            val newY = point[1].toInt()

            val drawable = item.drawable as? BitmapDrawable ?: continue
            val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            val transformedBounds = RectF(bounds)
            matrix.mapRect(transformedBounds) // Áp dụng ma trận vào bounds

            if (transformedBounds.contains(newX.toFloat(), newY.toFloat())) {
                selectItem(item) // Chọn item nếu chạm nằm trong bounds
                return
            }

            if (bounds.contains(newX, newY)) {
                selectItem(item)
                return
            }
            val iconSize = 300f
            drawable?.let {
                val bounds = RectF()
                bounds.set(it.bounds)
                matrix.mapRect(bounds)

                // Vị trí của các icon
                val deleteIconBounds = RectF(
                    bounds.left - iconSize / 2,
                    bounds.top - iconSize / 2,
                    bounds.left + iconSize / 2,
                    bounds.top + iconSize / 2
                )

                val flipIconBounds = RectF(
                    bounds.right - iconSize / 2,
                    bounds.top - iconSize / 2,
                    bounds.right + iconSize / 2,
                    bounds.top + iconSize / 2
                )
                Log.d("doanddo","idselect${selected?.id},iditem${item.id}")
                // Kiểm tra nếu nhấn vào biểu tượng delete
                if (deleteIconBounds.contains(eventX, eventY) && selected?.id == item.id) {
                    deleteItem(item)
                    invalidate()
                    return
                }


                // Kiểm tra nếu nhấn vào biểu tượng flip
                if (flipIconBounds.contains(eventX, eventY)) {
                    item.isFlipped = !item.isFlipped
                    invalidate()
                    return
                }
            }
        }

        selectItem(null) // Nếu không chạm vào item nào, bỏ chọn
    }
    private fun deleteItem(item: Item) {
        Log.d("SceneViewSS", "Deleting item: ${item.id}")
        items.remove(item)
        selected = null
        postInvalidate()
        (context as? MainActivity)?.onItemDeleted(item.id)
        Log.d("SceneViewSS", "Remaining items: ${items.size}")
    }


    private fun selectItem(item: Item?) {
        controller.stopAllAnimations()
        controller.updateState()

        item?.let {
            // Đưa item được chọn lên đầu danh sách
            items.remove(it)
            items.add(it)  // Thêm lại vào cuối danh sách (vẽ sau cùng)

            // Cập nhật bounds và trạng thái
            val bounds = Rect(0, 0, it.drawable.intrinsicWidth, it.drawable.intrinsicHeight)
            controller.state.set(it.state)
            controller.settings.setImage(bounds.width(), bounds.height())
        }

        selected = item // Cập nhật item đã chọn
        invalidate() // Yêu cầu vẽ lại
    }
}
//val drawable = item.drawable as? BitmapDrawable ?: continue
//val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//val transformedBounds = RectF(bounds)
//matrix.mapRect(transformedBounds)
//
//if (transformedBounds.contains(newX.toFloat(), newY.toFloat())) {
//    selectItem(item)
//    return
//}
//if (bounds.contains(newX, newY)) {
//    selectItem(item)
//    return
//}
//if (selected?.id == item.id) {
//    Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
//    Log.d("doanddo", "22222${selected?.id},iditem${item.id}")
//    val iconSize = 80f
//    drawable?.let {
//        val bounds = RectF()
//        bounds.set(it.bounds)
//        matrix.mapRect(bounds)
//
//        val deleteIconBounds = RectF(
//            bounds.left - iconSize / 2,
//            bounds.top - iconSize / 2,
//            bounds.left + iconSize / 2,
//            bounds.top + iconSize / 2
//        )
//
//        val flipIconBounds = RectF(
//            bounds.right - iconSize / 2,
//            bounds.top - iconSize / 2,
//            bounds.right + iconSize / 2,
//            bounds.top + iconSize / 2
//        )
//        val editIconBounds = RectF(
//            bounds.left - iconSize / 2,
//            bounds.bottom - iconSize / 2,
//            bounds.left + iconSize / 2,
//            bounds.bottom + iconSize / 2
//        )
//        // Kiểm tra nếu nhấn vào biểu tượng delete
//
//        if (deleteIconBounds.contains(eventX, eventY) && selected?.id == item.id) {
//            deleteItem(item)
//            Toast.makeText(context, "xoa", Toast.LENGTH_SHORT).show()
//            invalidate()
//            return
//        }
//        if (flipIconBounds.contains(eventX, eventY)) {
//            item.isFlipped = !item.isFlipped
//            Toast.makeText(context, "lat", Toast.LENGTH_SHORT).show()
//
//            invalidate()
//            return
//        }
//        if (editIconBounds.contains(eventX, eventY) ) {
//            showEditTextDialog(item)
//            Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show()
//
//            invalidate()
//            return
//        }
//    }