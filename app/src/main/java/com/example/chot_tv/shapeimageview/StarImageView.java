package com.example.chot_tv.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.example.chot_tv.R;
import com.example.chot_tv.shapeimageview.shader.ShaderHelper;
import com.example.chot_tv.shapeimageview.shader.SvgShader;

public class StarImageView extends ShaderImageView {

    public StarImageView(Context context) {
        super(context);
    }

    public StarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_star);
    }
}
