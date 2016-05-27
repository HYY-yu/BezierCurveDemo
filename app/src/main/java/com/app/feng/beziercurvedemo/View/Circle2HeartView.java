package com.app.feng.beziercurvedemo.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.app.feng.beziercurvedemo.Utils.PointSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用贝塞尔曲线绘制一个圆形,再慢慢变成心形
 * Created by feng on 2016/5/5.
 */
public class Circle2HeartView extends View {

    static final String TAG = "Circle2HeartView";

    public static final float c = 0.551784f;

    private float mRadius;
    private float mCenterX;
    private float mCenterY;

    private List<PointSet> allPoint;

    private Paint mPaint;

    private ChangeAnimation wa;

    private class ChangeAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            Log.i(TAG, "applyTransformation: " + interpolatedTime);
            PointSet pointSet1 = allPoint.get(0);
            pointSet1.getStart().offset(0, 1.5f * interpolatedTime);

            PointSet pointSet2 = allPoint.get(1);
            pointSet2.getControl1().offset(-0.3f * interpolatedTime, 0);
            pointSet2.getControl2().offset(0, -1.3f * interpolatedTime);

            PointSet pointSet3 = allPoint.get(2);
            pointSet3.getControl2().offset(0.3f * interpolatedTime, 0);
            pointSet3.getControl1().offset(0, -1.3f * interpolatedTime);
            invalidate();
        }

    }


    public Circle2HeartView(Context context) {
        this(context, null);
    }

    public Circle2HeartView(Context context, AttributeSet set) {
        this(context, set, 0);
    }

    public Circle2HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获得圆心和圆半径

        mRadius = 100;

        allPoint = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;

        //创建第一象限
        PointSet pointSet1 = new PointSet();
        pointSet1.getStart().x = mCenterX;
        pointSet1.getStart().y = mCenterY - mRadius;
        pointSet1.getEnd().x = mCenterX + mRadius;
        pointSet1.getEnd().y = mCenterY;
        pointSet1.getControl1().x = pointSet1.getStart().x + c * mRadius;
        pointSet1.getControl1().y = pointSet1.getStart().y;
        pointSet1.getControl2().x = pointSet1.getEnd().x;
        pointSet1.getControl2().y = pointSet1.getEnd().y - c * mRadius;

        allPoint.add(pointSet1);

        //创建第四象限
        PointSet pointSet2 = new PointSet();
        pointSet2.setStart(pointSet1.getEnd());
        pointSet2.getEnd().x = mCenterX;
        pointSet2.getEnd().y = mCenterY + mRadius;
        pointSet2.getControl1().x = pointSet2.getStart().x;
        pointSet2.getControl1().y = pointSet2.getStart().y + c * mRadius;
        pointSet2.getControl2().x = pointSet2.getEnd().x + c * mRadius;
        pointSet2.getControl2().y = pointSet2.getEnd().y;

        allPoint.add(pointSet2);

        //创建第三象限
        PointSet pointSet3 = new PointSet();
        pointSet3.setStart(pointSet2.getEnd());
        pointSet3.getEnd().x = mCenterX - mRadius;
        pointSet3.getEnd().y = mCenterY;
        pointSet3.getControl1().x = pointSet3.getStart().x - c * mRadius;
        pointSet3.getControl1().y = pointSet3.getStart().y;
        pointSet3.getControl2().x = pointSet3.getEnd().x;
        pointSet3.getControl2().y = pointSet3.getEnd().y + c * mRadius;

        allPoint.add(pointSet3);

        //创建第二象限
        PointSet pointSet4 = new PointSet();
        pointSet4.setStart(pointSet3.getEnd());
        pointSet4.setEnd(pointSet1.getStart());
        pointSet4.getControl1().x = pointSet4.getStart().x;
        pointSet4.getControl1().y = pointSet4.getStart().y - c * mRadius;
        pointSet4.getControl2().x = pointSet4.getEnd().x - c * mRadius;
        pointSet4.getControl2().y = pointSet4.getEnd().y;

        allPoint.add(pointSet4);

    }

    private void startAnimation() {
        wa = new ChangeAnimation();
        wa.setDuration(1000);

        wa.setInterpolator(new DecelerateInterpolator());
        wa.setFillAfter(true);
        startAnimation(wa);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, new Paint());
        //canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), new Paint());

        Path path = new Path();

        //绘制第一象限
        PointSet pointSet1 = allPoint.get(0);

        path.moveTo(pointSet1.getStart().x, pointSet1.getStart().y);

        path.cubicTo(pointSet1.getControl1().x, pointSet1.getControl1().y,
                pointSet1.getControl2().x, pointSet1.getControl2().y,
                pointSet1.getEnd().x, pointSet1.getEnd().y);

        //绘制第四象限
        PointSet pointSet2 = allPoint.get(1);

        path.cubicTo(pointSet2.getControl1().x, pointSet2.getControl1().y,
                pointSet2.getControl2().x, pointSet2.getControl2().y,
                pointSet2.getEnd().x, pointSet2.getEnd().y);

        //绘制第三象限
        PointSet pointSet3 = allPoint.get(2);
        path.cubicTo(pointSet3.getControl1().x, pointSet3.getControl1().y,
                pointSet3.getControl2().x, pointSet3.getControl2().y,
                pointSet3.getEnd().x, pointSet3.getEnd().y);

        //绘制第二象限
        PointSet pointSet4 = allPoint.get(3);

        path.cubicTo(pointSet4.getControl1().x, pointSet4.getControl1().y,
                pointSet4.getControl2().x, pointSet4.getControl2().y,
                pointSet4.getEnd().x, pointSet4.getEnd().y);

        canvas.drawPath(path, mPaint);
    }
}
