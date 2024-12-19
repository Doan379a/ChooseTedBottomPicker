package com.example.chot_tv.test_chucnang_bientap

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chot_tv.R

class demo : AppCompatActivity() {

//    private lateinit var movableImage: View
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_bieen_tap) // Thay your_layout bằng tên layout của bạn
//
//        movableImage = findViewById(R.id.movable_image)
//
//        // Thêm chức năng di chuyển cho hình ảnh
//        movableImage.setOnTouchListener { view, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // Lưu tọa độ chạm ban đầu
//                    val dX = view.x - event.rawX
//                    val dY = view.y - event.rawY
//
//                    // Cập nhật tọa độ hình ảnh trong khi di chuyển
//                    view.animate()
//                        .x(event.rawX + dX)
//                        .y(event.rawY + dY)
//                        .setDuration(0)
//                        .start()
//                    true
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    view.animate()
//                        .x(event.rawX + (view.x - event.rawX))
//                        .y(event.rawY + (view.y - event.rawY))
//                        .setDuration(0)
//                        .start()
//                    true
//                }
//                else -> false
//            }
//        }
//    }
}