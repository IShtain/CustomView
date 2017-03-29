package com.shtainyky.customview.views;

import android.animation.ObjectAnimator;
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
    private float startRadius = -90;
    private int angle;

    private float mCenterX, mCenterY;
    private Paint mBackgroundPaint;
    private Path mPath;
    private Matrix mMatrix;
    private RectF mRectF;
    private Resources resources;
    private List<Bitmap> bitmaps = new ArrayList<>();


    public CustomCircleMenu(Context context) {
        super(context);
        init(context);
    }

    public CustomCircleMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        init(context);


    }

    public CustomCircleMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        angle = 360 / numberOfSectors;
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
        mPath.reset();
        mPath.moveTo(mCenterX, mCenterY);
        mPath.lineTo(mCenterX, mCenterY - circleRadius);
        if (numberOfSectors == 0)
            throw new IllegalArgumentException("Amount of sectors can not be equal zero");
        mMatrix.reset();
        mMatrix.setTranslate(mCenterX, mCenterY);
        for (int i = 0; i < numberOfSectors; i++) {
            mMatrix.setRotate(angle, mCenterX, mCenterY);
            mPath.transform(mMatrix);
            canvas.drawPath(mPath, mBackgroundPaint);
            mMatrix.reset();
        }
    }

    private void fillChosenSector(Canvas canvas) {
        //fill sector
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(colorChosenSector);
        mRectF.set(mCenterX - circleRadius, mCenterY - circleRadius,
                mCenterX + circleRadius, mCenterY + circleRadius);
        canvas.drawArc(mRectF, startRadius, angle, true, mBackgroundPaint);
    }

    private void drawMenuIcon(Canvas canvas) {
        //draw sector's icon
        if (bitmaps != null)
            if (bitmaps.size() > 0) {
                Log.d("myLog", "bitmaps.size() = " + bitmaps.size());
                mMatrix.reset();
                mMatrix.preTranslate(mCenterX - bitmaps.get(0).getWidth() / 2, mCenterY - circleRadius + bitmaps.get(0).getHeight());
                mMatrix.postRotate(angle / 2, mCenterX, mCenterY);
                canvas.drawBitmap(bitmaps.get(0), mMatrix, null);
                for (int i = 1; i < numberOfSectors; i++) {
                    mMatrix.postRotate(angle, mCenterX, mCenterY);
                    canvas.drawBitmap(bitmaps.get(i), mMatrix, null);
                }

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
        ObjectAnimator rotateAnim;

        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d("myLog", "Action was ACTION_DOWN");
                startX = event.getX();
                startY = event.getY();
                Log.d("myLog", "startX x = " + startX);
                Log.d("myLog", "startY y = " + startY);
                break;
            case (MotionEvent.ACTION_MOVE):
                Log.d("myLog", "Action was ACTION_MOVE");
                double endX = event.getX();
                double endY = event.getY();
                if (Double.isNaN(endX) || Double.isNaN(endY)) break;
                double angle;
                double absVector1 = Math.pow(startX * startX + startY * startY, 0.5);
                double absVector2 = Math.pow(endX * endX + endY * endY, 0.5);
                if (absVector1 == 0 || absVector2 == 0) angle = 0;
                else {
                    double cosAngle = (startX * endX + startY * endY) / (absVector1 * absVector2);
                    angle = Math.acos(cosAngle);
                }
                Log.d("myLog", "angle" + angle * 360);
                if (endX - startX >= 0) {
                    rotateAnim = ObjectAnimator.ofFloat(this, "rotation", 0, (float) angle * 360);
                    Log.d("myLog", "Action was ACTION_MOVE ++++");
                } else {
                    rotateAnim = ObjectAnimator.ofFloat(this, "rotation", (float) angle * 360, 0);
                    Log.d("myLog", "Action was ACTION_MOVE ----");
                }

                Log.d("myLog", "startX x = " + startX);
                Log.d("myLog", "startY y = " + startY);
                Log.d("myLog", "x = " + endX);
                Log.d("myLog", "y = " + endY);
                rotateAnim.setDuration(500);
                rotateAnim.setRepeatMode(ObjectAnimator.RESTART);
                rotateAnim.start();
                break;

            case (MotionEvent.ACTION_UP):
                Log.d("myLog", "Action was ACTION_UP");
                startX = 0;
                startY = 0;
                Log.d("myLog", "startX x = " + startX);
                Log.d("myLog", "startY y = " + startY);
                break;
            case (MotionEvent.ACTION_CANCEL):
                Log.d("myLog", "Action was ACTION_CANCEL");
                break;


        }
        return true;
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
