package com.example.chot_tv.shapeimageview.path;

import android.content.Context;

import com.example.chot_tv.shapeimageview.path.parser.IoUtil;
import com.example.chot_tv.shapeimageview.path.parser.PathInfo;
import com.example.chot_tv.shapeimageview.path.parser.SvgToPath;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SvgUtil {
    private static final Map<Integer, PathInfo> PATH_MAP = new ConcurrentHashMap<Integer, PathInfo>();

    public static final PathInfo readSvg(Context context, int resId) {
        PathInfo pathInfo = PATH_MAP.get(resId);
        if(pathInfo == null) {
            InputStream is = null;
            try {
                is = context.getResources().openRawResource(resId);
                pathInfo = SvgToPath.getSVGFromInputStream(is);
                PATH_MAP.put(resId, pathInfo);
            } finally {
                IoUtil.closeQuitely(is);
            }
        }

        return pathInfo;
    }
}
