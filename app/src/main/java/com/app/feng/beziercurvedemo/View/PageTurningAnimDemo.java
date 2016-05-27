package com.app.feng.beziercurvedemo.View;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.app.feng.beziercurvedemo.R;
import com.app.feng.beziercurvedemo.Utils.PointCalculateUtils;

/**
 * Created by feng on 2016/5/6.
 */
public class PageTurningAnimDemo extends View {

    private static final String TAG = "PageTurningAnimDemo";

    //-------------已知点
    private PointF mTouch;
    private PointF mScreen;

    //-------------待求点 e---end s---start c---control t---BezierTopPoint
    private PointF E1;
    private PointF E2;
    private PointF C1;
    private PointF C2;
    private PointF T1;
    private PointF T2;
    private PointF S1;
    private PointF S2;
    private PointF shadowTop;

    //-------------辅助点
    private PointF G;

    //-------------mTouch 到 mScreen 的距离
    private float distance;

    private Bitmap mCurPage;
    private Bitmap mCurPage_backarea;
    private Bitmap mNextPage;
    private Path mCurPath;
    private Path mNextPath;
    private Path mCurPath_backarea;


    private Paint mPaint;

    private boolean mIsUpORDown = false; // 判断是以右下为mScree(false)n还是右上.

    private boolean mIsClickable = true; // 是否响应点击事件

    //设置翻起页的颜色过滤器
    private float[] mColorMatrix = new float[]{0.55f,0,0,0,80,0,0.55f,0,0,80,0,0,0.55f,0,80,0,0,0,0.2f,0};

    private GradientDrawable mNextPageDrawable;
    private GradientDrawable mCurPageDrawable_top;
    private GradientDrawable mCurPageDrawable_left;

    private float mMaxLength;


    public PageTurningAnimDemo(Context context) {
        this(context,null);
    }

    public PageTurningAnimDemo(Context context,AttributeSet attrs) {
        this(context,null,0);
    }

    public PageTurningAnimDemo(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);

        /* 初始化所有点 */
        mTouch = new PointF();
        mScreen = new PointF();

        E1 = new PointF();
        E2 = new PointF();
        C1 = new PointF();
        C2 = new PointF();
        T1 = new PointF();
        T2 = new PointF();
        S1 = new PointF();
        S2 = new PointF();
        shadowTop = new PointF();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        mCurPath = new Path();
        mNextPath = new Path();
        mCurPath_backarea = new Path();

        int[] color = new int[]{0xee111111,0x02111111};

        mNextPageDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,color);
        mNextPageDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        int[] color2 = new int[]{0xffaaaaaa,0xff111111,0x02111111};
        mCurPageDrawable_top = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,color2);
        mCurPageDrawable_top.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mCurPageDrawable_left = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,color2);
        mCurPageDrawable_left.setGradientType(GradientDrawable.LINEAR_GRADIENT);

    }

    @Override
    protected void onSizeChanged(int w,int h,int oldw,int oldh) {

        Bitmap temp = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        Matrix matrix = new Matrix();
        matrix.postScale((float) w / temp.getWidth(),(float) h / temp.getHeight());
        mCurPage = Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);

        //翻转当前页
        Matrix change = new Matrix();
        change.setScale(-1,1);
        mCurPage_backarea = Bitmap.createBitmap(mCurPage,0,0,mCurPage.getWidth(),mCurPage.getHeight(),change,true);

        Bitmap temp2 = BitmapFactory.decodeResource(getResources(),R.drawable.bg_next);
        Matrix matrix2 = new Matrix();
        matrix2.postScale((float) w / temp2.getWidth(),(float) h / temp2.getHeight());
        mNextPage = Bitmap.createBitmap(temp2,0,0,temp2.getWidth(),temp2.getHeight(),matrix,true);

        mMaxLength = (float) Math.hypot(w,h);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsClickable) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mTouch.x = event.getX();
                mTouch.y = event.getY();

                if (mTouch.y < getHeight() / 2) {
                    mIsUpORDown = true;
                    mScreen.x = getWidth();
                    mScreen.y = 0;
                } else {
                    mIsUpORDown = false;
                    mScreen.x = getWidth();
                    mScreen.y = getHeight();
                }
                invalidate();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouch.x = event.getX();
                mTouch.y = event.getY();

                invalidate();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //播放动画,此时不响应点击事件
                //Log.i(TAG, "onTouchEvent: " + "点击事件 ACTION_UP");
                //mIsClickable = false;

                if (mTouch.x > getWidth() / 2) {
                    //翻到下一页


                } else {
                    //回到本页


                }

            } else {
                //待定
            }
        }

        return true;
    }

    double range;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateAllPoint();
        drawCurPage(canvas);
        drawNextPage(canvas);
        drawCurPage_BackArea(canvas);


        //当前页阴影--右下角
        if (mScreen.y != 0) drawCurShadow_bottom(canvas);
            //当前页阴影--右上角
        else drawCurShadow_top(canvas);

    }

    private void drawCurShadow_top(Canvas canvas) {
        double range2 = 2 * (-range) - 90;
        double sTop2mTouch = (distance / 2) / 6;

        double ax = sTop2mTouch * Math.cos(Math.toRadians(range));
        double ay = sTop2mTouch * Math.sin(Math.toRadians(range));

        //Log.i(TAG,"drawCurShadow_top: ax  " + ax + " ay " + ay);

        shadowTop.x = (float) (mTouch.x - ax);
        shadowTop.y = (float) (mTouch.y - ay);


        float b1_1 = (float) Math.hypot(shadowTop.x - T1.x,shadowTop.y - T1.y);
        float b1_2 = (float) Math.hypot(shadowTop.x - E2.x,shadowTop.y - E2.y);

        PointF shadowBottom1 = new PointF(shadowTop.x + b1_1,shadowTop.y - b1_2);

        float b2_1 = (float) Math.hypot(shadowTop.x - T2.x,shadowTop.y - T2.y);
        float b2_2 = (float) Math.hypot(shadowTop.x - E1.x,shadowTop.y - E1.y);

        PointF shadowBottom2 = new PointF(shadowTop.x + b2_2,shadowTop.y - b2_1);

        canvas.save();
        canvas.clipPath(mCurPath,Region.Op.DIFFERENCE);
        Path clip = new Path();
        clip.moveTo(shadowTop.x,shadowTop.y);
        clip.lineTo(mTouch.x,mTouch.y);
        clip.lineTo(E2.x,E2.y);
        clip.lineTo(T2.x,T2.y);
        clip.close();
        canvas.clipPath(clip,Region.Op.DIFFERENCE);
        canvas.rotate((float) range2,shadowTop.x,shadowTop.y);

        mCurPageDrawable_top.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);

        mCurPageDrawable_top.setBounds((int) shadowTop.x,(int) (shadowTop.y - b1_2),(int) shadowBottom1.x,(int) (shadowBottom1.y + b1_2));

        mCurPageDrawable_top.draw(canvas);

        canvas.restore();

        canvas.save();
        canvas.clipPath(mCurPath,Region.Op.DIFFERENCE);
        clip.reset();
        clip.moveTo(shadowTop.x,shadowTop.y);
        clip.lineTo(mTouch.x,mTouch.y);
        clip.lineTo(E1.x,E1.y);
        clip.lineTo(T1.x,T1.y);
        clip.close();
        canvas.clipPath(clip,Region.Op.DIFFERENCE);
        canvas.rotate((float) range2,shadowTop.x,shadowTop.y);

        mCurPageDrawable_left.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);

        mCurPageDrawable_left.setBounds((int) shadowTop.x,(int) (shadowTop.y - b2_1),(int) shadowBottom2.x,(int) (shadowBottom2.y + b2_1));

        mCurPageDrawable_left.draw(canvas);
        canvas.restore();

    }

    private void drawCurShadow_bottom(Canvas canvas) {

        double range2 = 2 * range - 90;
        double sTop2mTouch = (distance / 2) / 6;

        double ax = sTop2mTouch * Math.cos(Math.toRadians(range));
        double ay = sTop2mTouch * Math.sin(Math.toRadians(range));

        shadowTop.x = (float) (mTouch.x - ax);
        shadowTop.y = (float) (mTouch.y - ay);


        float b1 = (float) Math.hypot(shadowTop.x - T1.x,shadowTop.y - T1.y);
        float b2 = (float) Math.hypot(shadowTop.x - E2.x,shadowTop.y - E2.y);

        //Log.i(TAG,"drawCurShadow: b1 = " + b1 + " b2 = " + b2);

        PointF shadowBottom1 = new PointF(shadowTop.x + b1,shadowTop.y + b2);

        b1 = (float) Math.hypot(shadowTop.x - T2.x,shadowTop.y - T2.y);
        b2 = (float) Math.hypot(shadowTop.x - E1.x,shadowTop.y - E1.y);

        PointF shadowBottom2 = new PointF(shadowTop.x + b2,shadowTop.y + b1 + 150);

        canvas.save();
        canvas.clipPath(mCurPath,Region.Op.DIFFERENCE);
        Path clip = new Path();
        clip.moveTo(shadowTop.x,shadowTop.y);
        clip.lineTo(mTouch.x,mTouch.y);
        clip.lineTo(E2.x,E2.y);
        clip.lineTo(T2.x,T2.y);
        clip.close();
        canvas.clipPath(clip,Region.Op.DIFFERENCE);
        canvas.rotate((float) -range2,shadowTop.x,shadowTop.y);

        mCurPageDrawable_top.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);

        mCurPageDrawable_top.setBounds((int) shadowTop.x,(int) shadowTop.y,(int) shadowBottom1.x,(int) shadowBottom1.y);

        mCurPageDrawable_top.draw(canvas);

        canvas.restore();

        canvas.save();
        canvas.clipPath(mCurPath,Region.Op.DIFFERENCE);
        clip.reset();
        clip.moveTo(shadowTop.x,shadowTop.y);
        clip.lineTo(mTouch.x,mTouch.y);
        clip.lineTo(E1.x,E1.y);
        clip.lineTo(T1.x,T1.y);
        clip.close();
        canvas.clipPath(clip,Region.Op.DIFFERENCE);
        canvas.rotate((float) -range2,shadowTop.x,shadowTop.y);

        mCurPageDrawable_left.setBounds((int) shadowTop.x,(int) shadowTop.y,(int) shadowBottom2.x,(int) shadowBottom2.y);

        mCurPageDrawable_left.draw(canvas);
        canvas.restore();

    }


    private void drawCurPage(Canvas canvas) {
        //1、裁剪针对画布进行。
        //2、设canvas原来区域为A，传参区域为B,求裁剪后区域:
        //DIFFERENCE  A - B
        //INTERSECT A N B
        //REPLACE B
        //UNION A U B
        //XOR (A U B) - (A N B)
        //REVERSE_DIFFERENCE B - (A N B)
        //默认模式为INTERSECT。
        mCurPath.reset();
        mCurPath.moveTo(S1.x,S1.y);
        mCurPath.quadTo(C1.x,C1.y,E1.x,E1.y);
        mCurPath.lineTo(mTouch.x,mTouch.y);
        mCurPath.lineTo(E2.x,E2.y);
        mCurPath.quadTo(C2.x,C2.y,S2.x,S2.y);
        mCurPath.lineTo(mScreen.x,mScreen.y);
        mCurPath.close();

        canvas.save();
        canvas.clipPath(mCurPath,Region.Op.XOR);
        canvas.drawBitmap(mCurPage,0,0,mPaint);
        canvas.restore();
    }

    private void drawNextPage(Canvas canvas) {
        mNextPath.reset();
        mNextPath.moveTo(S1.x,S1.y);
        mNextPath.lineTo(T1.x,T1.y);
        mNextPath.lineTo(T2.x,T2.y);
        mNextPath.lineTo(S2.x,S2.y);
        mNextPath.lineTo(mScreen.x,mScreen.y);
        mNextPath.close();

        range = Math.toDegrees(Math.atan2(mScreen.y - C1.y,mScreen.x - C2.x));
        float h = distance / 3;
        //Log.i(TAG,"drawNextPage: " + "range = " + range + ", h = " + h + S2.x);

        canvas.save();
        canvas.clipPath(mCurPath,Region.Op.INTERSECT);
        canvas.clipPath(mNextPath,Region.Op.INTERSECT);

        canvas.drawBitmap(mNextPage,0,0,mPaint);
        canvas.rotate((float) -range,S2.x,S2.y);

        if (mScreen.y == 0) {
            //右上
            mNextPageDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            mNextPageDrawable.setBounds((int) S2.x,(int) (S2.y - h),(int) (S2.x + mMaxLength),(int) S2.y);

        } else {
            //右下
            mNextPageDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            mNextPageDrawable.setBounds((int) S2.x,(int) S2.y,(int) (mMaxLength + S2.x),(int) (h + S2.y));
        }
        mNextPageDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawCurPage_BackArea(Canvas canvas) {
        mCurPath_backarea.reset();
        mCurPath_backarea.moveTo(T1.x,T1.y);
        mCurPath_backarea.lineTo(mTouch.x,mTouch.y);
        mCurPath_backarea.lineTo(T2.x,T2.y);
        mCurPath_backarea.close();

        canvas.save();
        Matrix change = new Matrix();
        change.preTranslate(mTouch.x,-(mScreen.y - mTouch.y));

        double range = 180 - 2 * Math.toDegrees(Math.atan2(C2.y - C1.y,C2.x - C1.x));

        //Log.i(TAG,"drawCurPage_BackArea: " + "range = " + range);

        change.postRotate((float) -range,mTouch.x,mTouch.y);

        canvas.clipPath(mCurPath);
        canvas.clipPath(mCurPath_backarea,Region.Op.INTERSECT);

        mPaint.setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
        canvas.drawColor(0xFFBBBBBB);
        canvas.drawBitmap(mCurPage_backarea,change,mPaint);

        mPaint.setColorFilter(null);
        canvas.restore();
    }

    private void calculateAllPoint() {
        //计算点要结合图形来看。
        G = PointCalculateUtils.getMiddlePoint(mTouch,mScreen);

        float gm = mScreen.y - G.y;
        float mf = mScreen.x - G.x;

        C1.x = mScreen.x;
        C1.y = mScreen.y - (PointCalculateUtils.getSquare(gm) + PointCalculateUtils.getSquare(mf)) / gm;

        C2.x = G.x - PointCalculateUtils.getSquare(gm) / mf;
        C2.y = mScreen.y;

        S2.x = mScreen.x - (mScreen.x - C2.x) * 3 / 2;
        S2.y = mScreen.y;

        //Log.i(TAG, "calculateAllPoint: " + "S2.x =" + S2.x);

        //限制S2不能小于0 否则绘制会出问题
        if (S2.x < 0 || S2.x > getWidth()) {
            //当S2小于0 将其设置到屏幕外
            if (S2.x < 0) {
                S2.x = getWidth() - S2.x;
            }

            //计算新的mTouch.x 比原来的x要右移 f2 的距离
            float f1 = Math.abs(mScreen.x - mTouch.x);
            float f2 = getWidth() * f1 / S2.x;
            mTouch.x = Math.abs(mScreen.x - f2);

            //计算新的mTouch.y 比原来的y要下移f3 的距离
            float f3 = Math.abs(mScreen.x - mTouch.x) * Math.abs(mScreen.y - mTouch.y) / f1;
            mTouch.y = Math.abs(mScreen.y - f3);

            // 通过S2与mScrren.x比例计算新的mTouch点，当S2.x 越来越大时，f2 f3 的距离只会越来越大，这就导致了mTonch点
            //更接近于mScrren点。精妙的算法！防止了s2.x 的距离变大导致的BUG，
            // 将mTouch点限制在一定的范围中，如果超过这个范围
            //（这个范围即是S2.x > 0 ） mTouch还要往右下移动。
            // 比起我限制mTouch点在一定的范围中的做法高明的多。语句虽然不多，
            //但是理解起来还是有一定难度，我也没办法完全理解这个算法。

            //下面就是根据mTouch重新计算各个点了。
            G = PointCalculateUtils.getMiddlePoint(mTouch,mScreen);

            float gm1 = mScreen.y - G.y;
            float mf1 = mScreen.x - G.x;

            C1.x = mScreen.x;
            C1.y = mScreen.y - (PointCalculateUtils.getSquare(gm1) + PointCalculateUtils.getSquare(mf1)) / gm1;

            C2.x = G.x - PointCalculateUtils.getSquare(gm1) / mf1;
            C2.y = mScreen.y;

            S2.x = mScreen.x - (mScreen.x - C2.x) * 3 / 2;
            //Log.i(TAG, "calculateAllPoint: calculate " + "S2.x = " + S2.x);
        }

        S1.x = mScreen.x;
        S1.y = mScreen.y - (mScreen.y - C1.y) * 3 / 2;

        E1 = PointCalculateUtils.getMiddlePoint(mTouch,C1);
        E2 = PointCalculateUtils.getMiddlePoint(mTouch,C2);

        PointF s1_e1_mid = PointCalculateUtils.getMiddlePoint(S1,E1);
        T1 = PointCalculateUtils.getMiddlePoint(s1_e1_mid,C1);

        PointF s2_e2_mid = PointCalculateUtils.getMiddlePoint(S2,E2);
        T2 = PointCalculateUtils.getMiddlePoint(s2_e2_mid,C2);

        distance = (float) Math.hypot(mTouch.x - mScreen.x,mTouch.y - mScreen.y);
    }
}
