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
import android.graphics.Path;
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

    private int backgroundColor;
    private int colorMainBorder;
    private int colorChosenSector;
    private int circleRadius;
    private int numberOfSectors;
    private float widthMainBorder;
    private List<Integer> iconsForMenu = new ArrayList<>();

    private float startX = 0;
    private float startY = 0;
    private boolean isRotating;
    private boolean allowsRotating;
    private boolean[] quadrantTouched;


    private int mAngleOneSector;
    private float mStartAngle;
    private double startMovingAngle;

    private float mCenterX, mCenterY;
    private Paint mBackgroundPaint;
    private Path mPath;
    private Matrix mMatrix;
    private RectF mRectF;
    private Resources resources;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private final GestureDetector gestureDetector;
    private ValueAnimator rotateAnimator;


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
            backgroundColor = attributes.getColor(R.styleable.CustomCircleMenu_backgroundColor, 0);
            colorMainBorder = attributes.getColor(R.styleable.CustomCircleMenu_colorMainBorder, 0);
            colorChosenSector = attributes.getColor(R.styleable.CustomCircleMenu_colorChosenSector, 0);
            circleRadius = attributes.getInteger(R.styleable.CustomCircleMenu_circleRadius, 0);
            widthMainBorder = attributes.getFloat(R.styleable.CustomCircleMenu_widthMainBorder, 0);
            numberOfSectors = attributes.getInteger(R.styleable.CustomCircleMenu_numberOfSectors, 0);

        } finally {
            attributes.recycle();
        }
    }


    private void init(Context context) {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mMatrix = new Matrix();
        mRectF = new RectF();
        resources = context.getResources();
        quadrantTouched = new boolean[]{false, false, false, false, false};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) (2 * circleRadius + 2 * widthMainBorder);
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
        if (numberOfSectors == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        mAngleOneSector = 360 / numberOfSectors;
        Log.d("myLog", "onDraw mStartAngle = " + mStartAngle);
        fillCircleBackground(canvas);
        drawSectors(canvas);
        fillChosenSector(canvas);
        drawMenuIcon(canvas);
        drawInnerCircle(canvas);
        drawMainCircleBorder(canvas);
        //  invalidate();
    }

    private void fillCircleBackground(Canvas canvas) {
        //fill background
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(backgroundColor);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius, mBackgroundPaint);
    }

    private void drawSectors(Canvas canvas) {
        //draw sector's line
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(widthMainBorder / 2);
        mBackgroundPaint.setColor(colorMainBorder);
//        mPath.reset();
//        mPath.moveTo(mCenterX, mCenterY);
//        mPath.lineTo(endRadiusX, endRadiusY);
//        mMatrix.reset();
//        mMatrix.setTranslate(mCenterX, mCenterY);
//        for (int i = 0; i < numberOfSectors; i++) {
//            mMatrix.setRotate(mAngleOneSector, mCenterX, mCenterY);
//            mPath.transform(mMatrix);
//            canvas.drawPath(mPath, mBackgroundPaint);
//            mMatrix.reset();
//        }


        mRectF.set(mCenterX - circleRadius, mCenterY - circleRadius,
                mCenterX + circleRadius, mCenterY + circleRadius);
        for (int i = 0; i < numberOfSectors; i++) {
            canvas.drawArc(mRectF, mStartAngle + i * mAngleOneSector, mAngleOneSector, true, mBackgroundPaint);
        }

    }


    private void fillChosenSector(Canvas canvas) {
        //fill sector
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(colorChosenSector);
        mRectF.set(mCenterX - circleRadius, mCenterY - circleRadius,
                mCenterX + circleRadius, mCenterY + circleRadius);
        canvas.drawArc(mRectF, mStartAngle, mAngleOneSector, true, mBackgroundPaint);
    }

    private void drawMenuIcon(Canvas canvas) {
        //draw sector's icon
        if (bitmaps != null)
            if (bitmaps.size() > 0) {
                // Log.d("myLog", "bitmaps.size() = " + bitmaps.size());
                mMatrix.reset();
                mMatrix.setTranslate((float) (mCenterX + 2 * circleRadius * Math.cos(Math.toRadians(mStartAngle)) / 3 - bitmaps.get(0).getHeight() / 2),
                        (float) (mCenterY + 2 * circleRadius * Math.sin(Math.toRadians(mStartAngle)) / 3) - bitmaps.get(0).getHeight() / 2);
                mMatrix.postRotate(mAngleOneSector / 2, mCenterX, mCenterY);
                canvas.drawBitmap(bitmaps.get(0), mMatrix, null);

                for (int i = 1; i < numberOfSectors; i++) {
                    mMatrix.postRotate(mAngleOneSector, mCenterX, mCenterY);
                    canvas.drawBitmap(bitmaps.get(i), mMatrix, null);
                }
//                mMatrix.reset();
//                mMatrix.postTranslate((float) (mCenterX + circleRadius * Math.sin(mAngleOneSector * Math.PI / 360) - bitmaps.get(0).getWidth() / 2),
//                        (float) (mCenterY + circleRadius * Math.cos(mAngleOneSector * Math.PI / 360) - bitmaps.get(0).getHeight())/2);
//                mMatrix.setRotate(mAngleOneSector, mCenterX, mCenterY);
//                canvas.drawBitmap(bitmaps.get(0), mMatrix, null);
//                for (int i = 1; i < numberOfSectors; i++) {
//                    mMatrix.postRotate(mAngleOneSector, mCenterX, mCenterY);
//                    canvas.drawBitmap(bitmaps.get(i), mMatrix, null);
//                }

            }
    }

    private void drawInnerCircle(Canvas canvas) {
        //draw inner circle
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(Color.RED);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius / 6, mBackgroundPaint);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(colorChosenSector);
        mBackgroundPaint.setStrokeWidth(widthMainBorder);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius / 6, mBackgroundPaint);
    }

    private void drawMainCircleBorder(Canvas canvas) {
        //draw stroke
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(widthMainBorder);
        mBackgroundPaint.setColor(colorMainBorder);
        canvas.drawCircle(mCenterX, mCenterY, circleRadius, mBackgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchRadius = (float) Math.sqrt(Math.pow(event.getX() - mCenterX, 2)
                + Math.pow(event.getY() - mCenterY, 2));
        //all movement inside circle
        if (touchRadius < circleRadius) {
            Log.d("newOne", "Action was inside");
            gestureDetector.onTouchEvent(event);
            int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    Log.d("newOne", "Action was ACTION_DOWN");
                    //reset rotating
                    if (rotateAnimator != null && rotateAnimator.isStarted())
                        rotateAnimator.cancel();
                    // reset the touched quadrants
                    for (int i = 0; i < quadrantTouched.length; i++) {
                        quadrantTouched[i] = false;
                    }
                    //turn mStartAngle between 0 and 360 degrees
                    if (mStartAngle > 360 || mStartAngle < -360)
                        mStartAngle = mStartAngle % 360;
                    startX = event.getX();
                    startY = event.getY();
                    startMovingAngle = getAngle(startX, startY);
                    Log.d("myLog", "startX x = " + startX);
                    Log.d("myLog", "startY y = " + startY);
                    break;

                case (MotionEvent.ACTION_MOVE):
                    Log.d("myLog", "Action was ACTION_MOVE");
                    double currentAngle = getAngle(event.getX(), event.getY());
                    mStartAngle = mStartAngle + (float) (currentAngle - startMovingAngle);
                    invalidate();
                    Log.d("newOne", "Action was ACTION_MOVE diff = " + (currentAngle - startMovingAngle));
                    startMovingAngle = currentAngle;
                    break;

                case (MotionEvent.ACTION_UP):
                    Log.d("newOne", "Action was ACTION_UP");
                    startX = event.getX();
                    startY = event.getY();
                    Log.d("myLog", "startX x = " + startX);
                    Log.d("myLog", "startY y = " + startY);

                    Log.d("myLog", "mStartAngle = " + mStartAngle);
                    Log.d("myLog", "getAngle = " + getAngle(startX, startY));

                    break;
            }
            // set the touched quadrant to true
            quadrantTouched[getQuadrant(event.getX() - mCenterX, event.getY() - mCenterY)] = true;
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

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (mStartAngle > 360 || mStartAngle < -360)
                mStartAngle = mStartAngle % 360;

            rotateAnimator = ValueAnimator.ofInt(0, getDirectionAngle(e1, e2));
            rotateAnimator.setDuration(1500);
            rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
            rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mStartAngle = mStartAngle + (int) animator.getAnimatedValue();
                    invalidate();
                }
            });
            rotateAnimator.start();

            Log.d("newOne", "onFling");
            return true;
        }

        private int getDirectionAngle(MotionEvent e1, MotionEvent e2) {
            double fromAngle = getAngle(e1.getX(), e1.getY());
            double toAngle = getAngle(e2.getX(), e2.getY());
            int startQuadrant = getQuadrant(e1.getX() - mCenterX, e1.getY() - mCenterY);
            int endQuadrant = getQuadrant(e2.getX() - mCenterX, e2.getY() - mCenterY);
            final int end;
            if ((startQuadrant == 1 && endQuadrant == 1 && fromAngle > toAngle)
                    || (startQuadrant == 2 && endQuadrant == 2 && fromAngle > toAngle)
                    || (startQuadrant == 3 && endQuadrant == 3 && fromAngle > toAngle)
                    || (startQuadrant == 4 && endQuadrant == 4 && fromAngle > toAngle)
                    || (startQuadrant == 1 && endQuadrant == 2 && quadrantTouched[3])
                    || (startQuadrant == 1 && endQuadrant == 3 && quadrantTouched[4])
                    || (startQuadrant == 1 && endQuadrant == 4 && !quadrantTouched[3])
                    || (startQuadrant == 2 && endQuadrant == 1 && !quadrantTouched[3])
                    || (startQuadrant == 2 && endQuadrant == 3 && quadrantTouched[4])
                    || (startQuadrant == 2 && endQuadrant == 4 && quadrantTouched[1])
                    || (startQuadrant == 3 && endQuadrant == 1 && quadrantTouched[2])
                    || (startQuadrant == 3 && endQuadrant == 2 && !quadrantTouched[1])
                    || (startQuadrant == 3 && endQuadrant == 4 && quadrantTouched[1])
                    || (startQuadrant == 4 && endQuadrant == 1 && quadrantTouched[3])
                    || (startQuadrant == 4 && endQuadrant == 2 && quadrantTouched[3])
                    || (startQuadrant == 4 && endQuadrant == 3 && !quadrantTouched[2])) {

                end = -1 * (int) Math.abs(toAngle - fromAngle);

            } else {
                // the normal rotation
                end = (int) Math.abs(toAngle - fromAngle);
            }
            return end;
        }
    }

    /*Getters  and setters*/


    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public int getColorMainBorder() {
        return colorMainBorder;
    }

    public void setColorMainBorder(int colorMainBorder) {
        this.colorMainBorder = colorMainBorder;
        invalidate();
    }

    public int getColorChosenSector() {
        return colorChosenSector;
    }

    public void setColorChosenSector(int colorChosenSector) {
        this.colorChosenSector = colorChosenSector;
        invalidate();
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
        invalidate();
    }

    public int getNumberOfSectors() {
        return numberOfSectors;
    }

    public void setNumberOfSectors(int numberOfSectors) {
        if (numberOfSectors == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        this.numberOfSectors = numberOfSectors;
        invalidate();
    }

    public float getWidthMainBorder() {
        return widthMainBorder;
    }

    public void setWidthMainBorder(float widthMainBorder) {
        this.widthMainBorder = widthMainBorder;
        invalidate();
    }

    public interface OnMenuIconClickListener {
        void onIconClick(int drawableId);

    }

    public List<Integer> getIconsForMenu() {
        return iconsForMenu;
    }

    public void setIconsForMenu(List<Integer> iconsForMenu) {
        if (getNumberOfSectors() == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        if (iconsForMenu.size() == 0 || iconsForMenu.size() != getNumberOfSectors())
            throw new IllegalArgumentException("Length of list of icon for menu must be equal amount of sectors and can not be equal zero");
        this.iconsForMenu.clear();
        this.iconsForMenu.addAll(iconsForMenu);
        if (iconsForMenu.size() != 0) {
            for (int i = 0; i < iconsForMenu.size(); i++) {
                bitmaps.add(BitmapFactory
                        .decodeResource(resources, iconsForMenu.get(i)));
            }
        }
        invalidate();
    }
}
