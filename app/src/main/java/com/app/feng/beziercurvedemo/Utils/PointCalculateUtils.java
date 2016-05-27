package com.app.feng.beziercurvedemo.Utils;

import android.graphics.PointF;

/**
 * Created by feng on 2016/5/6.
 */
public class PointCalculateUtils {


    public static float getLengthForTwoPointF(PointF p1, PointF p2) {
        float x = getSquare(p1.x - p2.x);
        float y = getSquare(p1.y - p2.y);

        return (float) Math.sqrt(x + y);
    }

    public static PointF getMiddlePoint(PointF p1, PointF p2) {
        float x = (p1.x + p2.x) / 2;
        float y = (p1.y + p2.y) / 2;

        return new PointF(x, y);
    }

    public static float getSquare(float x){
        return x*x;
    }
}
