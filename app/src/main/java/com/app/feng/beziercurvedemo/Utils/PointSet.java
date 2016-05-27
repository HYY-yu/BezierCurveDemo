package com.app.feng.beziercurvedemo.Utils;

import android.graphics.PointF;

/**
 * 三阶贝塞尔曲线的点集合
 * Created by feng on 2016/5/5.
 */
public class PointSet {
    private PointF start;
    private PointF end;
    private PointF control1;
    private PointF control2;

    public PointSet() {
        start = new PointF(0,0);
        end = new PointF(0,0);
        control1 = new PointF(0,0);
        control2 = new PointF(0,0);
    }

    public PointSet(PointF control1, PointF control2, PointF end, PointF start) {
        this.control1 = control1;
        this.control2 = control2;
        this.end = end;
        this.start = start;
    }

    public PointF getControl1() {
        return control1;
    }

    public void setControl1(PointF control1) {
        this.control1 = control1;
    }

    public PointF getControl2() {
        return control2;
    }

    public void setControl2(PointF control2) {
        this.control2 = control2;
    }

    public PointF getEnd() {
        return end;
    }

    public void setEnd(PointF end) {
        this.end = end;

    }

    public PointF getStart() {
        return start;
    }

    public void setStart(PointF start) {
        this.start = start;
    }
}
