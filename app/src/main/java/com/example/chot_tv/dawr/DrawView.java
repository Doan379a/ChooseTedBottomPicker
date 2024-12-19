package com.example.chot_tv.dawr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.chot_tv.R;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    private Paint paint;
    private Paint balloonPaint; // Paint cho bong bóng
    private Path path;
    private Bitmap balloonBitmap; // Bitmap cho bong bóng
    private List<float[]> balloonPositions; // Danh sách các vị trí bong bóng
    private float minDistance = 100; // Khoảng cách tối thiểu giữa các bong bóng

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);

        // Khởi tạo paint cho bong bóng
        balloonPaint = new Paint();

        path = new Path();
        balloonBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boll); // Tải hình ảnh bong bóng
        balloonPositions = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Vẽ đường vẽ
        canvas.drawPath(path, paint);

        // Vẽ bong bóng tại các vị trí đã lưu
        for (float[] position : balloonPositions) {
            canvas.drawBitmap(balloonBitmap, position[0] - balloonBitmap.getWidth() / 2, position[1] - balloonBitmap.getHeight() / 2, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                // Kiểm tra và thêm bong bóng nếu đủ khoảng cách
                if (balloonPositions.isEmpty() || isFarEnough(x, y)) {
                    balloonPositions.add(new float[]{x, y}); // Thêm vị trí bong bóng
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                // Kiểm tra và thêm bong bóng nếu đủ khoảng cách
                if (isFarEnough(x, y)) {
                    balloonPositions.add(new float[]{x, y}); // Thêm vị trí bong bóng
                }
                break;
            case MotionEvent.ACTION_UP:
                // Không thêm bong bóng khi thả tay
                break;
            default:
                return false;
        }

        invalidate(); // yêu cầu vẽ lại
        return true;
    }

    private boolean isFarEnough(float x, float y) {
        for (float[] position : balloonPositions) {
            float distance = (float) Math.sqrt(Math.pow(position[0] - x, 2) + Math.pow(position[1] - y, 2));
            if (distance < minDistance) {
                return false; // Nếu gần hơn khoảng cách tối thiểu thì không thêm
            }
        }
        return true; // Nếu đủ khoảng cách thì thêm
    }

    public void setSolidLine() {
        paint.setPathEffect(null); // Nét liền
        invalidate();
    }

    public void setDashedLine() {
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0)); // Nét đứt
        invalidate();
    }

    public void setDottedLine() {
        paint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0)); // Nét chấm bi
        invalidate();
    }
}
