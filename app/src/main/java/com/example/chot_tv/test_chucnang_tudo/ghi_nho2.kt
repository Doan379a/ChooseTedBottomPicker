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

class ghinho2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs), GestureView {

    private val items: MutableList<BaseItem> = ArrayList()
    private val controller: GestureController = GestureController(this)
    private val matrix: Matrix = Matrix()
    private val inverseMatrix: Matrix = Matrix()
    private val paint: Paint = Paint()
    private var selected: BaseItem? = null
    private val point = FloatArray(2)
    private var deleteIcon: Bitmap? = null
    private var flipIcon: Bitmap? = null
    private var editIcon: Bitmap? = null

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
            .setOverscrollDistance(1000f, 1000f)
            .setMinZoom(0.3f)
            .setMaxZoom(3f)
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
                        val paddingVertical = 15 // Padding = 10 pixels
                        val paddingHorizontal = 250 // Padding = 10 pixels
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

    fun setItems(newItems: List<BaseItem>) {
        items.clear()
        items.addAll(newItems)
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
            if (item is Item) {
                val drawable = item.drawable as? BitmapDrawable ?: continue
                val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                val transformedBounds = RectF(bounds)
                matrix.mapRect(transformedBounds)

                if (transformedBounds.contains(newX.toFloat(), newY.toFloat())) {
                    selectItem(item)
                    return
                }
                if (bounds.contains(newX, newY)) {
                    selectItem(item)
                    return
                }
                // Chỉ kiểm tra các icon khi đối tượng đã được chọn
                if (selected?.id == item.id) {
                    val iconSize = 80f
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
                        Log.d("doanddo", "idselect${selected?.id},iditem${item.id}")
                        // Kiểm tra nếu nhấn vào biểu tượng delete
                        if (deleteIconBounds.contains(eventX, eventY) && selected?.id == item.id) {
                            deleteItem(item)
                            Toast.makeText(context, "xoa", Toast.LENGTH_SHORT).show()

                            invalidate()
                            return
                        }
                        if (flipIconBounds.contains(eventX, eventY)) {
                            item.isFlipped = !item.isFlipped
                            Toast.makeText(context, "lat", Toast.LENGTH_SHORT).show()

                            invalidate()
                            return
                        }
                    }
                }
            } else if (item is TextItem) {
                val drawable = item.drawable as? BitmapDrawable ?: continue
                val bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                val transformedBounds = RectF(bounds)
                matrix.mapRect(transformedBounds)

                if (transformedBounds.contains(newX.toFloat(), newY.toFloat())) {
                    selectItem(item)
                    return
                }
                if (bounds.contains(newX, newY)) {
                    selectItem(item)
                    return
                }
                if (selected?.id == item.id) {
                    val iconSize = 100f
                    drawable?.let {
                        val paddingVertical = 15
                        val paddingHorizontal = 250
                        val bounds = RectF(
                            it.bounds.left - paddingHorizontal.toFloat(),
                            it.bounds.top - paddingVertical.toFloat(),
                            it.bounds.right + paddingHorizontal.toFloat(),
                            it.bounds.bottom + paddingVertical.toFloat()
                        )
                        matrix.mapRect(bounds)
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
                        val editIconBounds = RectF(
                            bounds.left - iconSize / 2,
                            bounds.bottom - iconSize / 2,
                            bounds.left + iconSize / 2,
                            bounds.bottom + iconSize / 2
                        )
                        Log.d("doanddo", "idselect${selected?.id},iditem${item.id}")
                        if (deleteIconBounds.contains(eventX, eventY) && selected?.id == item.id) {
                            deleteItem(item)
                            Toast.makeText(context, "xoa", Toast.LENGTH_SHORT).show()
                            invalidate()
                            return
                        }
                        if (flipIconBounds.contains(eventX, eventY)) {
                            item.isFlipped = !item.isFlipped
                            Toast.makeText(context, "lat", Toast.LENGTH_SHORT).show()

                            invalidate()
                            return
                        }
                        if (editIconBounds.contains(eventX, eventY)) {
                            showEditTextDialog(item)
                            Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show()
                            invalidate()
                            return
                        }
                    }
                }
            }
        }
        selectItem(null)
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
                item.text = newText
                (context as? MainActivity)?.onItemedit(item.id,newText)
                invalidate()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
