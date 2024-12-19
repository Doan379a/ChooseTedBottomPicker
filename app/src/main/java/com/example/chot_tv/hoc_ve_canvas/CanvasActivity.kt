package com.example.chot_tv.hoc_ve_canvas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chot_tv.databinding.ActivityCanvasBinding

class CanvasActivity:AppCompatActivity() {
    private lateinit var binding: ActivityCanvasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanvasBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}