package com.example.chot_tv.ok_chucnang_tudo

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.example.chot_tv.zoomimg.State
import java.util.UUID

// Lớp cơ sở cho tất cả các đối tượng trong SceneView
abstract class BaseItem {
    val id: String = UUID.randomUUID().toString() // ID duy nhất cho mỗi item
    val state: State = State() // Trạng thái (zoom, vị trí, xoay)
    abstract val drawable: Drawable
    abstract fun draw(canvas: Canvas, matrix: Matrix, paint: Paint)

}
