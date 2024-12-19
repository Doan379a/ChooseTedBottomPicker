package com.example.chot_tv.hoc_ve_canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.View
import androidx.compose.ui.graphics.Color



class CustomView(context: Context):View(context) {
    private val paint= Paint().apply {
        color= android.graphics.Color.RED
        style=android.graphics.Paint.Style.FILL
        isAntiAlias=true//khu rang cua
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /// hinh tron
        canvas.drawCircle(200f,200f,100f,paint)
        ///hinh chu nhat
        paint.color=android.graphics.Color.BLUE
        canvas.drawRect(300f, 500f, 500f, 700f, paint)


        ////ddwong thang
        paint.color=android.graphics.Color.GREEN
        paint.strokeWidth=10f//do day duong vien
        canvas.drawLine(600f,600f,300f,300f,paint)

        ///
      val path=Path().apply {
          //diem bat dau
          moveTo(100f,1000f)
          ///diem thu 2
          lineTo(200f,300f)
          ///vien so 3
          lineTo(50f,300f)
          close()//ket noi diem cuoi ve diem dau de hoan thnah hinh ram giac
      }
        canvas.drawPath(path,paint)
/////
        paint.color=android.graphics.Color.YELLOW
        //tam ngoi sao
        val centerx=500f
        val centery=500f
        ///ban kin cua vong ngoai (dinh ngoi sao)
        val outerRadius=200f
        //ban kinh cua vong trong (ranh ngoi sao )
        val innerRadius=100f
        ///so luong canh cua ngoi sao
        val numPoints=5
        ////goc cua moi phan (360 do chia cho so dinh cua ngoi sao)
        val angle=(2 * Math.PI / numPoints).toFloat()

        for (i in 0 until numPoints*2){
            //tinh toan goc
            val currentAngle=i*angle/2
            ///xac dinh toa do cho dinh va ranh
            val radius=if (i%2==0) outerRadius else innerRadius
            val x=centerx+(radius*Math.cos(currentAngle.toDouble())).toFloat()
            val y=centery+(radius*Math.sin(currentAngle.toDouble())).toFloat()
            if (i==0){
                path.moveTo(x,y)//diem dau tien
            }else{
                path.lineTo(x,y)//jet noi cac diem con lai
            }

        }
        path.close()
        canvas.drawPath(path,paint)
////mat trang
        val radius=200f
        canvas.drawCircle(centerx,centery,radius,paint)
        // Vẽ hình tròn nhỏ (che khuất phần của mặt trăng)
        val smallCircleX = centerx + 100f // Di chuyển sang phải
        val smallCircleY = centerx // Giữ Y không thay đổi
        val smallCircleRadius = 200f // Bán kính của hình tròn nhỏ

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT) // Chế độ xóa
        canvas.drawCircle(smallCircleX, smallCircleY, smallCircleRadius, paint) // Vẽ hình tròn nhỏ

        paint.xfermode = null
    }
}