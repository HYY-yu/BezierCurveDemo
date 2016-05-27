package com.app.feng.beziercurvedemo;

import android.graphics.PointF;

import com.app.feng.beziercurvedemo.Utils.Utils;

import org.junit.Test;

/**
 * Created by feng on 2016/5/6.
 */
public class UnitsTest {

    @Test
    public void testGetLengthForTwoPointF(){
        double temp = Utils.getLengthForTwoPointF(new PointF(0f, 0f), new PointF(0f, 1f));

        System.out.printf(temp + "");
    }
}
