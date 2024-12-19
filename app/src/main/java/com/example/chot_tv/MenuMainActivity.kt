package com.example.chot_tv

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chot_tv.databinding.ActivityMenuMainBinding
import com.example.chot_tv.dawr.drawview_activity
import com.example.chot_tv.hoc_ve_canvas.CanvasActivity
import com.example.chot_tv.ok_chucnang_tudo.BieenTapActivity

class MenuMainActivity:AppCompatActivity() {
    private lateinit var binding:ActivityMenuMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBientap.setOnClickListener {
           val intent= Intent(this, BieenTapActivity::class.java)
            startActivity(intent)
        }
        binding.btnDrawview.setOnClickListener {
            val intent= Intent(this, drawview_activity::class.java)
            startActivity(intent)
        }
        binding.btnCanvas.setOnClickListener {
            val intent= Intent(this, CanvasActivity::class.java)
            startActivity(intent)
        }
        binding.btnBottomimg.setOnClickListener {
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnTudo.setOnClickListener {
            val intent= Intent(this, com.example.chot_tv.ok_chucnang_tudo.MainActivity::class.java)
            startActivity(intent)
        }
    }
}