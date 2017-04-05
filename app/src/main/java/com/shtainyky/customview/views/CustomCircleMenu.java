package com.shtainyky.customview.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.shtainyky.customview.R;

import java.util.ArrayList;
import java.util.List;

public class CustomCircleMenu extends View {

    private final static int DEFAULT_DURATION_SHORT = 300;  //for chosen sector
    private final static int DEFAULT_DURATION_LONG = 1500;   //for fling

    private int mBackgroundColor;
    private int mColorMainBorder;
    private int mColorChosenSector;
    private int mCircleRadius;
    private int mNumberOfSectors;
    private float mWidthMainBorder;
    private List<Integer> mIconsForMenu;

    private float mStartX;
    private float mStartY;
    private boolean[] mQuadrantTouched;
    private int mAngleOneSector;
    private float mStartAngle;
    private double mStartMovingAngle;
    private float mStartPaintingAngle;
    private float mCenterX, mCenterY;

    private Paint mBackgroundPaint;
    private Matrix mMatrix;
    private RectF mRectF;
    private Resources mResources;
    private List<Bitmap> mBitmaps = new ArrayList<>();
    private final GestureDetector gestureDetector;
    private ValueAnimator rotateAnimator;
    private OnMenuItemClickListener mOnMenuItemClickListener;


    public CustomCircleMenu(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, new MyGestureListener());
        init(context);
    }

    public CustomCircleMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new MyGestureListener());
        init(context, attrs);
        init(context);


    }

    public CustomCircleMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context, new MyGestureListener());
        init(context, attrs);
        init(context);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomCircleMenu, 0, 0);
        try {
            mBackgroundColor = attributes.getColor(R.styleable.CustomCircleMenu_backgroundColor, 0);
            mColorMainBorder = attributes.getColor(R.styleable.CustomCircleMenu_colorMainBorder, 0);
            mColorChosenSector = attributes.getColor(R.styleable.CustomCircleMenu_colorChosenSector, 0);
            mCircleRadius = attributes.getInteger(R.styleable.CustomCircleMenu_circleRadius, 0);
            mWidthMainBorder = attributes.getFloat(R.styleable.CustomCircleMenu_widthMainBorder, 0);
            mNumberOfSectors = attributes.getInteger(R.styleable.CustomCircleMenu_numberOfSectors, 0);

        } finally {
            attributes.recycle();
        }
    }


    private void init(Context context) {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMatrix = new Matrix();
        mRectF = new RectF();
        mResources = context.getResources();
        mIconsForMenu = new ArrayList<>();
        mQuadrantTouched = new boolean[]{false, false, false, false, false};
        if (mNumberOfSectors == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        mAngleOneSector = 360 / mNumberOfSectors;
        mStartAngle = -90 - mAngleOneSector / 2;
        Log.d("myLog", "init mStartAngle = " + mStartAngle);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) (2 * mCircleRadius + 2 * mWidthMainBorder);
        int width = resolveSizeAndState(size, widthMeasureSpec, 0);
        int height = resolveSizeAndState(size, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("myLog", "onDraw mStartAngle = " + mStartAngle);
        fillCircleBackground(canvas);
        drawSectors(canvas);
        fillChosenSector(canvas);
        drawMenuIcon(canvas);
        drawInnerCircle(canvas);
        drawMainCircleBorder(canvas);
    }

    private void fillCircleBackground(Canvas canvas) {
        //fill background
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius, mBackgroundPaint);
    }

    private void drawSectors(Canvas canvas) {
        //draw sector's line
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mWidthMainBorder / 2);
        mBackgroundPaint.setColor(mColorMainBorder);
        Log.d("myLog", "drawSectors mStartAngle = " + mStartAngle);
        mRectF.set(mCenterX - mCircleRadius, mCenterY - mCircleRadius,
                mCenterX + mCircleRadius, mCenterY + mCircleRadius);
        for (int i = 0; i < mNumberOfSectors; i++) {
            canvas.drawArc(mRectF, mStartAngle + i * mAngleOneSector, mAngleOneSector, true, mBackgroundPaint);
        }
        Log.d("myLog", "drawSectors mStartAngle = " + mStartAngle);

    }


    private void fillChosenSector(Canvas canvas) {
        //fill sector
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(mColorChosenSector);
        mRectF.set(mCenterX - mCircleRadius, mCenterY - mCircleRadius,
                mCenterX + mCircleRadius, mCenterY + mCircleRadius);
        canvas.drawArc(mRectF, mStartAngle + mStartPaintingAngle, mAngleOneSector, true, mBackgroundPaint);
        Log.d("myLog", "fillChosenSector mStartAngle = " + mStartAngle);
    }

    private void drawMenuIcon(Canvas canvas) {
        //draw sector's icon
        if (mBitmaps != null)
            if (mBitmaps.size() > 0) {
                // Log.d("myLog", "mBitmaps.size() = " + mBitmaps.size());
                mMatrix.reset();
                mMatrix.setTranslate((float) (mCenterX + 2 * mCircleRadius * Math.cos(Math.toRadians(mStartAngle)) / 3 - mBitmaps.get(0).getHeight() / 2),
                        (float) (mCenterY + 2 * mCircleRadius * Math.sin(Math.toRadians(mStartAngle)) / 3) - mBitmaps.get(0).getHeight() / 2);
                mMatrix.postRotate(mAngleOneSector / 2, mCenterX, mCenterY);
                canvas.drawBitmap(mBitmaps.get(0), mMatrix, null);

                for (int i = 1; i < mNumberOfSectors; i++) {
                    mMatrix.postRotate(mAngleOneSector, mCenterX, mCenterY);
                    canvas.drawBitmap(mBitmaps.get(i), mMatrix, null);
                }
//                mMatrix.reset();
//                mMatrix.postTranslate((float) (mCenterX + mCircleRadius * Math.sin(mAngleOneSector * Math.PI / 360) - mBitmaps.get(0).getWidth() / 2),
//                        (float) (mCenterY + mCircleRadius * Math.cos(mAngleOneSector * Math.PI / 360) - mBitmaps.get(0).getHeight())/2);
//                mMatrix.setRotate(mAngleOneSector, mCenterX, mCenterY);
//                canvas.drawBitmap(mBitmaps.get(0), mMatrix, null);
//                for (int i = 1; i < mNumberOfSectors; i++) {
//                    mMatrix.postRotate(mAngleOneSector, mCenterX, mCenterY);
//                    canvas.drawBitmap(mBitmaps.get(i), mMatrix, null);
//                }

            }
    }

    private void drawInnerCircle(Canvas canvas) {
        //draw inner circle
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(Color.RED);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius / 6, mBackgroundPaint);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(mColorChosenSector);
        mBackgroundPaint.setStrokeWidth(mWidthMainBorder);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius / 6, mBackgroundPaint);
    }

    private void drawMainCircleBorder(Canvas canvas) {
        //draw stroke
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mWidthMainBorder);
        mBackgroundPaint.setColor(mColorMainBorder);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius, mBackgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchRadius = (float) Math.sqrt(Math.pow(event.getX() - mCenterX, 2)
                + Math.pow(event.getY() - mCenterY, 2));
        //all movement inside circle
        if (touchRadius < mCircleRadius) {
            gestureDetector.onTouchEvent(event);
            int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    Log.d("newOne", "Action was ACTION_DOWN");
                    //reset rotating
                    if (rotateAnimator != null && rotateAnimator.isStarted())
                        rotateAnimator.cancel();
                    // reset the touched quadrants
                    for (int i = 0; i < mQuadrantTouched.length; i++) {
                        mQuadrantTouched[i] = false;
                    }
                    //turn mStartAngle between 0 and 360 degrees or between -360 and 0
                    if (mStartAngle > 360 || mStartAngle < -360)
                        mStartAngle = mStartAngle % 360;
                    mStartX = event.getX();
                    mStartY = event.getY();
                    mStartMovingAngle = getAngle(mStartX, mStartY);
                    Log.d("newOne", "mStartX x = " + mStartX);
                    Log.d("newOne", "mStartY y = " + mStartY);
                    break;

                case (MotionEvent.ACTION_MOVE):
                    Log.d("myLog", "Action was ACTION_MOVE");
                    double currentAngle = getAngle(event.getX(), event.getY());
                    mStartAngle = mStartAngle + (float) (currentAngle - mStartMovingAngle);
                    invalidate();
                    Log.d("newOne", "Action was ACTION_MOVE diff = " + (currentAngle - mStartMovingAngle));
                    mStartMovingAngle = currentAngle;
                    break;

                case (MotionEvent.ACTION_UP):
                    Log.d("newOne", "Action was ACTION_UP");
                    if (mStartX == event.getX() && mStartY == event.getY()) {
                        Log.d("newOne", "Action was ACTION_UP true!!!!!!");
                        if (mStartAngle < 0) mStartAngle = mStartAngle + 360;
                        double paintAngle = (getAngle(mStartX, mStartY) - mStartAngle) % 360;
                        if (paintAngle < 0) paintAngle = paintAngle + 360;
                        Log.d("newOne", "paintAngle = " + paintAngle);
                        for (int i = 0; i < mNumberOfSectors; i++) {
                            if ((mAngleOneSector * i <= paintAngle)
                                    && (paintAngle < mAngleOneSector * (i + 1))) {
                                if (mOnMenuItemClickListener != null)
                                    mOnMenuItemClickListener.onIconClick(mIconsForMenu.get(i));
                                mStartPaintingAngle = mAngleOneSector * i;
                                invalidate();
                                double animDiff =  (270 - mAngleOneSector/2 - mStartPaintingAngle - mStartAngle)%360;
                                if (animDiff < -180) animDiff = 360 + animDiff;
                                if (animDiff > 180) animDiff = -360 + animDiff;
                                Log.d("newOne", "mStartAngle = " + mStartAngle);
                                Log.d("newOne", "getAngle = " + getAngle(mStartX, mStartY));
                                buildRotateAnimation((int) animDiff, DEFAULT_DURATION_SHORT);
                                rotateAnimator.start();
                                Log.d("newOne", "i = " + i);
                                Log.d("newOne", "(float) animDiff = " + (float)animDiff);
                                break;
                            }
                        }
                    }

                    break;
            }
            // set the touched quadrant to true
            mQuadrantTouched[getQuadrant(event.getX() - mCenterX, event.getY() - mCenterY)] = true;
        }
        return true;
    }


    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - mCenterX;
        double y = yTouch - mCenterY;

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    private int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    private void buildRotateAnimation(int angle, long duration) {
        rotateAnimator = ValueAnimator.ofInt(0, angle);
        rotateAnimator.setDuration(duration);
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        final float rememberStartAngle = mStartAngle;
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mStartAngle = rememberStartAngle + (int) animator.getAnimatedValue();
                invalidate();
            }
        });
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (mStartAngle > 360 || mStartAngle < -360)
                mStartAngle = mStartAngle % 360;
            buildRotateAnimation((int) (getDirectionSign(e1, e2) * Math.abs(velocityX + velocityY)), DEFAULT_DURATION_LONG);
            rotateAnimator.start();
            Log.d("newOne", "onFling");
            return true;
        }

        private int getDirectionSign(MotionEvent e1, MotionEvent e2) {
            double fromAngle = getAngle(e1.getX(), e1.getY());
            double toAngle = getAngle(e2.getX(), e2.getY());
            int startQuadrant = getQuadrant(e1.getX() - mCenterX, e1.getY() - mCenterY);
            int endQuadrant = getQuadrant(e2.getX() - mCenterX, e2.getY() - mCenterY);
            final int sign;
            if ((startQuadrant == 1 && endQuadrant == 1 && fromAngle > toAngle)
                    || (startQuadrant == 2 && endQuadrant == 2 && fromAngle > toAngle)
                    || (startQuadrant == 3 && endQuadrant == 3 && fromAngle > toAngle)
                    || (startQuadrant == 4 && endQuadrant == 4 && fromAngle > toAngle)
                    || (startQuadrant == 1 && endQuadrant == 2 && mQuadrantTouched[3])
                    || (startQuadrant == 1 && endQuadrant == 3 && mQuadrantTouched[4])
                    || (startQuadrant == 1 && endQuadrant == 4 && !mQuadrantTouched[3])
                    || (startQuadrant == 2 && endQuadrant == 1 && !mQuadrantTouched[3])
                    || (startQuadrant == 2 && endQuadrant == 3 && mQuadrantTouched[4])
                    || (startQuadrant == 2 && endQuadrant == 4 && mQuadrantTouched[1])
                    || (startQuadrant == 3 && endQuadrant == 1 && mQuadrantTouched[2])
                    || (startQuadrant == 3 && endQuadrant == 2 && !mQuadrantTouched[1])
                    || (startQuadrant == 3 && endQuadrant == 4 && mQuadrantTouched[1])
                    || (startQuadrant == 4 && endQuadrant == 1 && mQuadrantTouched[3])
                    || (startQuadrant == 4 && endQuadrant == 2 && mQuadrantTouched[3])
                    || (startQuadrant == 4 && endQuadrant == 3 && !mQuadrantTouched[2])) {
                sign = -1;
            } else {
                // the clockwise rotation
                sign = 1;
            }
            return sign;
        }
    }

    /*Getters  and setters*/

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        invalidate();
    }

    public int getColorMainBorder() {
        return mColorMainBorder;
    }

    public void setColorMainBorder(int colorMainBorder) {
        this.mColorMainBorder = colorMainBorder;
        invalidate();
    }

    public int getColorChosenSector() {
        return mColorChosenSector;
    }

    public void setColorChosenSector(int colorChosenSector) {
        this.mColorChosenSector = colorChosenSector;
        invalidate();
    }

    public int getCircleRadius() {
        return mCircleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.mCircleRadius = circleRadius;
        invalidate();
    }

    public int getNumberOfSectors() {
        return mNumberOfSectors;
    }

    public void setNumberOfSectors(int numberOfSectors) {
        if (numberOfSectors == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        this.mNumberOfSectors = numberOfSectors;
        mAngleOneSector = 360 / numberOfSectors;
        mStartAngle = -90 - mAngleOneSector / 2;
        invalidate();
    }

    public float getWidthMainBorder() {
        return mWidthMainBorder;
    }

    public void setWidthMainBorder(float widthMainBorder) {
        this.mWidthMainBorder = widthMainBorder;
        invalidate();
    }

    public interface OnMenuItemClickListener {
        void onIconClick(int drawableId);

    }

    public List<Integer> getIconsForMenu() {
        return mIconsForMenu;
    }

    public void setIconsForMenu(List<Integer> iconsForMenu) {
        if (getNumberOfSectors() == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        if (iconsForMenu.size() == 0 || iconsForMenu.size() != getNumberOfSectors())
            throw new IllegalArgumentException("Length of list of icon for menu must be equal amount of sectors and can not be equal zero");
        this.mIconsForMenu.clear();
        this.mIconsForMenu.addAll(iconsForMenu);
        if (iconsForMenu.size() != 0) {
            for (int i = 0; i < iconsForMenu.size(); i++) {
                mBitmaps.add(BitmapFactory
                        .decodeResource(mResources, iconsForMenu.get(i)));
            }
        }
        invalidate();
    }
}
