package com.example.chot_tv.dawr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chot_tv.R
import com.example.chot_tv.databinding.DawrBinding

class drawview_activity:AppCompatActivity() {
    private lateinit var drawView: DrawView
    private lateinit var binding: DawrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DawrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawView = findViewById(R.id.draw_view)

        binding.btnSolid.setOnClickListener {
            drawView.setSolidLine()
        }

        binding.btnDashed.setOnClickListener {
            drawView.setDashedLine()
        }

        binding.btnDotted.setOnClickListener {
            drawView.setDottedLine()
        }
    }
}