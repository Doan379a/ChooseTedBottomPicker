package com.example.chot_tv.test_chucnang_tudo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import  com.example.chot_tv.databinding.ActivitiMainFrameBinding
import  com.example.chot_tv.zoomimg.views.GestureImageView
import gun0912.tedimagepicker.builder.TedImagePicker

class demo2 : ComponentActivity() {
    private lateinit var binding: ActivitiMainFrameBinding
    private lateinit var list: List<GestureImageView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiMainFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        list = listOf(binding.imageSlot1, binding.imageSlot2, binding.imageSlot3)
        list = listOf( )
        binding.imageSlot1.setOnClickListener {
           val loca= getFixedPosition(binding.imageSlot1)
            Log.d("Locationde", "X: $loca")
        }

        binding.imageSlot2.setOnClickListener {
            getFixedPosition(binding.imageSlot2)

        }

        binding.imageSlot3.setOnClickListener {
            getFixedPosition(binding.imageSlot3)
        }

        binding.selectImageButton.setOnClickListener {
            TedImagePicker.with(this)
                .start { uri ->
                    // Sử dụng Glide để tải ảnh vào imageSlot1, bạn có thể chọn bất kỳ slot nào
                                    Glide.with(this)
                                        .load(it)
                                        .into(binding.imageSlot1) // Hoặc binding.imageSlot2, binding.imageSlot3 tùy theo ý bạn
                                } ?: run {
                                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                                }
        }
    }



    private fun getFixedPosition(view:View): Pair<Float, Float> {
        val location = IntArray(2)

        view.getLocationOnScreen(location) // Hoặc sử dụng getLocationInWindow nếu cần

        // Lấy tọa độ X và Y từ vị trí của View
        val x = location[0].toFloat()
        val y = location[1].toFloat()
        Log.d("Locationde", "X: $x, Y: $y")
        return Pair(x, y)
    }

}
