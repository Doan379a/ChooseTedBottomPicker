package com.example.chot_tv.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.example.chot_tv.shapeimageview.shader.RoundedShader;
import com.example.chot_tv.shapeimageview.shader.ShaderHelper;

public class RoundedImageView extends ShaderImageView {
    private RoundedShader shader;
    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        shader = new RoundedShader();
        return shader;
    }

    public final int getRadius() {
        if(shader != null) {
            return shader.getRadius();
        }
        return 0;
    }

    public final void setRadius(final int radius) {
        if(shader != null) {
            shader.setRadius(radius);
            invalidate();
        }
    }

}
