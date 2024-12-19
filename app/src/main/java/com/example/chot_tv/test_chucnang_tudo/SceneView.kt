package com.example.chot_tv.test_chucnang_tudo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.chot_tv.R
import com.example.chot_tv.zoomimg.GestureController
import com.example.chot_tv.zoomimg.Settings
import com.example.chot_tv.zoomimg.State
import com.example.chot_tv.zoomimg.views.interfaces.GestureView

class SceneView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs), GestureView {

    private val items: MutableList<BaseItem> = ArrayList()
    private val frameItems: MutableList<Item_frame> = ArrayList()
    private val controller: GestureController = GestureController(this)
    private val matrix: Matrix = Matrix()
    private val inverseMatrix: Matrix = Matrix()
    private val paint: Paint = Paint()
    private var selected: BaseItem? = null
    private val point = FloatArray(2)
    private var deleteIcon: Bitmap? = null
    private var flipIcon: Bitmap? = null
    private var editIcon: Bitmap? = null
    private var selectedItem: Item? = null
    private var onItemSelectedListener: ((Item?) -> Unit)? = null

    fun setOnItemSelectedListener(listener: (Item?) -> Unit) {
        this.onItemSelectedListener = listener
    }


    init {
        init()
    }

    private fun init() {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE

        controller.settings
            .setRotationEnabled(true)
            .setDoubleTapEnabled(false)
            .setFitMethod(Settings.Fit.INSIDE)
            .setBoundsType(Settings.Bounds.INSIDE)
            .setMinZoom(0.2f)
            .setMaxZoom(0f) // Max zoom level = fit zoom
            .setImage(1, 1)

        deleteIcon =
            VectorDrawableCompat.create(resources, R.drawable.baseline_delete_24, null)?.let {
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
        editIcon = VectorDrawableCompat.create(resources, R.drawable.baseline_edit_24, null)?.let {
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
                clickselectItem(event.x, event.y)
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

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        // Vẽ tất cả các đối tượng (bao gồm cả Item và TextItem)
        for (frameItem in frameItems) {
            frameItem.state.get(matrix)
            frameItem.draw(canvas, matrix, paint)
        }

        for (item in items) {
            item.state.get(matrix)
            item.draw(canvas, matrix, paint)
        }

            selected?.let {
                paint.strokeWidth = 6f / controller.state.zoom
                paint.color = Color.RED
                canvas.save()
                controller.state.get(matrix)
                canvas.concat(matrix)

                when (it) {
                    is Item_frame -> {
                        val drawable = it.drawable as? BitmapDrawable
                        drawable?.let { d ->
                            val bounds = d.bounds
                            canvas.drawRect(bounds, paint)
                        }
                    }
                    is Item -> {
                        val drawable = it.drawable as? BitmapDrawable
                        drawable?.let { d ->
                            val bounds = d.bounds
                            canvas.drawRect(bounds, paint)
                            val iconSize = 100
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
                    }

                    is TextItem -> {
                        val drawable = it.drawable as? BitmapDrawable
                        drawable?.let { d ->
                            val paddingVertical = 20 // Padding = 10 pixels
                            val paddingHorizontal = 60 // Padding = 10 pixels
                            val bounds = Rect(
                                d.bounds.left - paddingHorizontal,
                                d.bounds.top - paddingVertical,
                                d.bounds.right + paddingHorizontal,
                                d.bounds.bottom + paddingVertical
                            )

                            // Vẽ khung xung quanh TextItem
                            canvas.drawRect(bounds, paint)

                            val iconSize = 100
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
                            editIcon?.let { icon ->
                                canvas.drawBitmap(
                                    Bitmap.createScaledBitmap(icon, iconSize, iconSize, false),
                                    bounds.left - iconSize.toFloat() / 2,
                                    bounds.bottom.toFloat() - iconSize / 2,
                                    null
                                )
                            }
                        }

                    }

                }

                canvas.restore()
            }
        canvas.restore()
    }
    fun addFrameItem(frameItem: Item_frame, x: Float, y: Float) {
        frameItem.state.set(x, y, frameItem.state.zoom, frameItem.state.rotation)
        frameItems.add(frameItem)
        invalidate()
    }
    fun setItems(newItems: List<BaseItem>) {
        items.clear()
        items.addAll(newItems.filter { it !is Item_frame })
        selected = null
        invalidate()
    }

    private fun applyState(state: State) {
        selected?.state?.set(state)
        invalidate()
    }


    private fun clickselectItem(eventX: Float, eventY: Float) {
        Log.d("SceneView", "selectItem: ${items.size}")
        for (i in items.indices.reversed()) {
            val item = items[i]
            item.state.get(matrix)
            matrix.invert(inverseMatrix)
            point[0] = eventX
            point[1] = eventY
            inverseMatrix.mapPoints(point)
            val newX = point[0].toInt()
            val newY = point[1].toInt()
            when (item) {
                is Item_frame -> {
                    val drawable = item.drawable as? BitmapDrawable ?: continue
                    val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                    val transformedBounds = RectF(bounds)
                    (context as? MainActivity)?.showControlPanel(true)
                    (context as? MainActivity)?.clicksenview()
                    matrix.mapRect(transformedBounds)
                    if (bounds.contains(newX, newY)) {
                        selectItem(item)
                        return
                    }
                    // Xử lý các icon (nếu cần) tương tự như Item
                }
                is Item -> {
                    val drawable = item.drawable as? BitmapDrawable ?: continue
                    val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                    val transformedBounds = RectF(bounds)
                    (context as? MainActivity)?.showControlPanel(true)
                    matrix.mapRect(transformedBounds)
                    if (bounds.contains(newX, newY)) {
                        selectItem(item)
                        return
                    }
                    if (selected?.id == item.id) {
                        val iconSize = 80f
                        // Tọa độ gốc của các icon (trên đối tượng)
                        val deleteIconPos =
                            floatArrayOf(bounds.left.toFloat(), bounds.top.toFloat())
                        val flipIconPos = floatArrayOf(bounds.right.toFloat(), bounds.top.toFloat())

                        // Áp dụng ma trận để tính tọa độ của icon sau khi đã áp dụng scale/rotate
                        matrix.mapPoints(deleteIconPos)
                        matrix.mapPoints(flipIconPos)

                        // Xây dựng `RectF` cho các icon delete và flip sau khi áp dụng ma trận
                        val deleteIconBounds = RectF(
                            deleteIconPos[0] - iconSize / 2,
                            deleteIconPos[1] - iconSize / 2,
                            deleteIconPos[0] + iconSize / 2,
                            deleteIconPos[1] + iconSize / 2
                        )

                        val flipIconBounds = RectF(
                            flipIconPos[0] - iconSize / 2,
                            flipIconPos[1] - iconSize / 2,
                            flipIconPos[0] + iconSize / 2,
                            flipIconPos[1] + iconSize / 2
                        )

                        // Kiểm tra va chạm với icon delete
                        if (deleteIconBounds.contains(eventX, eventY)) {
                            deleteItem(item)
                            Toast.makeText(context, "Xóa", Toast.LENGTH_SHORT).show()
                            invalidate()
                            return
                        }

                        // Kiểm tra va chạm với icon flip
                        if (flipIconBounds.contains(eventX, eventY)) {
                            item.isFlipped = !item.isFlipped
                            Toast.makeText(context, "Lật", Toast.LENGTH_SHORT).show()
                            invalidate()
                            return
                        }
                    }
                }

                is TextItem -> {
                    val drawable = item.drawable as? BitmapDrawable ?: continue
                    val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                    val paddingVertical = 20 // Padding trên dưới
                    val paddingHorizontal = 60 // Padding trái phải
                    (context as? MainActivity)?.clicksenview()

                    // Xác định bounds sau khi thêm padding
                    val paddedBounds = RectF(
                        bounds.left - paddingHorizontal.toFloat(),
                        bounds.top - paddingVertical.toFloat(),
                        bounds.right + paddingHorizontal.toFloat(),
                        bounds.bottom + paddingVertical.toFloat()
                    )

                    // Chuyển đổi bounds đã padding bằng ma trận
                    val transformedBounds = RectF(paddedBounds)
                    matrix.mapRect(transformedBounds)

                    // Kiểm tra va chạm với TextItem sau khi đã thêm padding
                    if (bounds.contains(newX, newY)) {
                        selectItem(item)
                        return
                    }

                    if (selected?.id == item.id) {
                        val iconSize = 100f
                        val deleteIconPos = floatArrayOf(paddedBounds.left, paddedBounds.top)
                        val flipIconPos = floatArrayOf(paddedBounds.right, paddedBounds.top)
                        val editIconPos = floatArrayOf(paddedBounds.left, paddedBounds.bottom)

                        // Áp dụng ma trận lên tọa độ của icon
                        matrix.mapPoints(deleteIconPos)
                        matrix.mapPoints(flipIconPos)
                        matrix.mapPoints(editIconPos)

                        // Xây dựng `RectF` cho các icon delete, flip, và edit
                        val deleteIconBounds = RectF(
                            deleteIconPos[0] - iconSize / 2,
                            deleteIconPos[1] - iconSize / 2,
                            deleteIconPos[0] + iconSize / 2,
                            deleteIconPos[1] + iconSize / 2
                        )

                        val flipIconBounds = RectF(
                            flipIconPos[0] - iconSize / 2,
                            flipIconPos[1] - iconSize / 2,
                            flipIconPos[0] + iconSize / 2,
                            flipIconPos[1] + iconSize / 2
                        )

                        val editIconBounds = RectF(
                            editIconPos[0] - iconSize / 2,
                            editIconPos[1] - iconSize / 2,
                            editIconPos[0] + iconSize / 2,
                            editIconPos[1] + iconSize / 2
                        )

                        // Kiểm tra va chạm với các icon
                        if (deleteIconBounds.contains(eventX, eventY)) {
                            deleteItem(item)
                            Toast.makeText(context, "Xóa", Toast.LENGTH_SHORT).show()
                            invalidate()
                            return
                        }

                        if (flipIconBounds.contains(eventX, eventY)) {
                            item.isFlipped = !item.isFlipped
                            Toast.makeText(context, "Lật", Toast.LENGTH_SHORT).show()
                            invalidate()
                            return
                        }

                        if (editIconBounds.contains(eventX, eventY)) {
                            showEditTextDialog(item)
                            Toast.makeText(context, "Chỉnh sửa", Toast.LENGTH_SHORT).show()
                            invalidate()
                            return
                        }
                    }
                }
            }
        }
        selectItem(null)
        (context as? MainActivity)?.showControlPanel(false)
        (context as? MainActivity)?.clicknenframe()

    }


    private fun deleteItem(item: BaseItem) {
        Log.d("SceneViewSS", "Deleting item: ${item.id}")
        items.remove(item)
        selected = null
        postInvalidate()
        (context as? MainActivity)?.onItemDeleted(item.id)
        Log.d("SceneViewSS", "Remaining items: ${items.size}")
    }

    private fun selectItem(item: BaseItem?) {
        controller.stopAllAnimations()
        controller.updateState()

        item?.let {
            if (it is Item) {
                selectedItem = it // Lưu item đã chọn
                onItemSelectedListener?.invoke(it) // Gọi callback với item được chọn
            }
            items.remove(it)
            items.add(it)
            controller.state.set(it.state)
        }

        selected = item // Cập nhật item đã chọn
        invalidate()
    }

    private fun showEditTextDialog(item: TextItem) {
        val editText = EditText(context)
        editText.setText(item.text)
        AlertDialog.Builder(context)
            .setTitle("Edit Text")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val newText = editText.text.toString()
                Log.d(
                    "MainActivityss",
                    "Updating text for item ID: ${item.id} with new text: $newText"
                )
                (context as? MainActivity)?.onItemedit(item.id, newText)
                invalidate()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


}
