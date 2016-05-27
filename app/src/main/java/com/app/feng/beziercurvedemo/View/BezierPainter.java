package com.app.feng.beziercurvedemo.View;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by feng on 2016/5/5.
 */
public class BezierPainter extends View {

    public static final int START_POINT = 1;
    public static final int END_POINT = 2;
    public static final int CON1_POINT = 3;
    public static final int CON2_POINT = 4;

    private Paint mPaint;
    private int centerX, centerY;
    private PointF start, end, control1, control2;
    private int whichControl;


    public BezierPainter(Context context) {
        this(context, null);
    }

    public BezierPainter(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(60);

        start = new PointF(0, 0);
        end = new PointF(0, 0);
        control1 = new PointF(0, 0);
        control2 = new PointF(0, 0);
    }

    public void setWhichControl(int whichControl) {
        this.whichControl = whichControl;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //初始化数据点和控制点
        centerX = w / 2;
        centerY = h / 2;
        start.x = centerX - 200;
        start.y = centerY;
        end.x = centerX + 200;
        end.y = centerY;

        control1.x = centerX - 200;
        control1.y = centerY - 100;
        control2.x = centerX + 200;
        control2.y = centerY + 100;

        whichControl = CON1_POINT;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (whichControl == CON1_POINT) {
            //控制点1
            control1.x = event.getX();
            control1.y = event.getY();
        } else if (whichControl == CON2_POINT) {
            //控制点2
            control2.x = event.getX();
            control2.y = event.getY();
        } else if (whichControl == START_POINT) {
            start.x = event.getX();
            start.y = event.getY();
        } else if (whichControl == END_POINT){
            end.x = event.getX();
            end.y = event.getY();
        }else{

        }

        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(20);
        canvas.drawPoint(start.x, start.y, mPaint);
        canvas.drawPoint(end.x, end.y, mPaint);
        canvas.drawPoint(control1.x, control1.y, mPaint);
        canvas.drawPoint(control2.x, control2.y, mPaint);

        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.GRAY);
        canvas.drawLine(start.x, start.y, control1.x, control1.y, mPaint);
        canvas.drawLine(control1.x, control1.y, control2.x, control2.y, mPaint);
        canvas.drawLine(control2.x, control2.y, end.x, end.y, mPaint);

        mPaint.setStrokeWidth(8);
        mPaint.setColor(Color.RED);
        Path path = new Path();

        path.moveTo(start.x, start.y);
        path.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y);

        canvas.drawPath(path, mPaint);
    }
}
